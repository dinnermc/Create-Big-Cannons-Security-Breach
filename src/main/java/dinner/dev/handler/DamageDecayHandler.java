package dinner.dev.handler;

import dinner.dev.Config;
import dinner.dev.compat.SecurityCraftCompat;
import dinner.dev.data.CannonDamageData;
import dinner.dev.network.BreachNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DamageDecayHandler {

    private static final Random RANDOM = new Random();
    private final Map<ResourceKey<Level>, Long> nextDecayTick = new HashMap<>();

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        // Prevent stale gameTime entries from freezing decay when switching worlds in singleplayer
        nextDecayTick.clear();
    }

    @SubscribeEvent
    public void onLevelTick(LevelTickEvent.Post event) {
        if (!Config.enabled || !Config.damageDecayEnabled) return;

        Level level = event.getLevel();
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) return;

        ResourceKey<Level> key = serverLevel.dimension();
        long gameTime = serverLevel.getGameTime();

        Long next = nextDecayTick.get(key);
        if (next != null && gameTime < next) return;

        CannonDamageData data = CannonDamageData.get(serverLevel);
        if (!data.isEmpty()) {
            for (Map.Entry<BlockPos, Float> entry : data.copyEntries().entrySet()) {
                BlockPos pos = entry.getKey();
                // Never force-load unloaded chunks just to tick decay
                if (!serverLevel.isLoaded(pos)) continue;

                BlockState state = serverLevel.getBlockState(pos);
                // Clean up orphaned damage if the block was removed or replaced by a non-targetable block
                if (!SecurityCraftCompat.isTargetableBlock(state.getBlock())) {
                    CannonDamageService.clearDamageAndSync(serverLevel, pos, data);
                    continue;
                }

                // Respect post-hit combat grace period so walls don't heal while cannons reload
                long lastHit = data.getLastDamagedTick(pos);
                if (Config.decayGracePeriodTicks > 0 && lastHit > 0 && (gameTime - lastHit) < Config.decayGracePeriodTicks) {
                    continue;
                }

                float current = entry.getValue();
                float maxHp = (float) Math.max(1.0, Config.getBlockHp(state.getBlock()));
                float stepHp = (float) Math.max(0.01, maxHp * Config.decayPercentPerStep);
                float newDamage = Math.max(0.0f, current - stepHp);

                if (newDamage <= 0.001f) {
                    CannonDamageService.clearDamageAndSync(serverLevel, pos, data);
                } else {
                    data.setDamage(pos, newDamage);
                    int stage = CannonDamageData.computeStage(newDamage, maxHp);
                    data.updateBreakProgress(serverLevel, pos, stage);
                    BreachNetwork.syncBlockDamage(serverLevel, pos, newDamage);
                }
            }
        }

        int min = Math.min(Config.damageDecayIntervalMin, Config.damageDecayIntervalMax);
        int max = Math.max(Config.damageDecayIntervalMin, Config.damageDecayIntervalMax);
        int delay = (min == max) ? min : RANDOM.nextInt(min, max + 1);
        nextDecayTick.put(key, gameTime + delay);
    }
}
