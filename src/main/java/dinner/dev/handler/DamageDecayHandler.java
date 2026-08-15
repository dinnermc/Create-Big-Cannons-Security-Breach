package dinner.dev.handler;

import dinner.dev.Config;
import dinner.dev.data.CannonDamageData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DamageDecayHandler {

    private static final Random RANDOM = new Random();
    private final Map<ResourceKey<Level>, Long> nextDecayTick = new HashMap<>();

    @SubscribeEvent
    public void onLevelTick(LevelTickEvent.Post event) {
        if (!Config.enabled || !Config.damageDecayEnabled) return;

        Level level = event.getLevel();
        if (level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        ResourceKey<Level> key = serverLevel.dimension();
        long gameTime = serverLevel.getGameTime();

        Long next = nextDecayTick.get(key);
        if (next != null && gameTime < next) return;

        CannonDamageData data = CannonDamageData.get(serverLevel);

        for (Map.Entry<BlockPos, Float> entry : data.copyEntries().entrySet()) {
            BlockPos pos = entry.getKey();
            float current = entry.getValue();

            BlockState state = serverLevel.getBlockState(pos);
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            float maxHp = (float) Math.max(Config.getBlockHp(blockId.getPath()), 1);
            float stageHp = maxHp * 0.1f;

            float newDamage = Math.max(0, current - stageHp);

            if (newDamage <= 0) {
                data.clearDamage(pos);
                CannonDamageData.setBreakProgress(serverLevel, pos, -1);
            } else {
                data.setDamage(pos, newDamage);
                int stage = (int) (newDamage / maxHp * 10.0f);
                CannonDamageData.setBreakProgress(serverLevel, pos, stage);
            }
        }

        int delay = RANDOM.nextInt(Config.damageDecayIntervalMin, Config.damageDecayIntervalMax + 1);
        nextDecayTick.put(key, gameTime + delay);
    }
}
