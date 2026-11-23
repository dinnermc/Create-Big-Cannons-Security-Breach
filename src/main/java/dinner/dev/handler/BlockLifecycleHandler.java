package dinner.dev.handler;

import dinner.dev.Config;
import dinner.dev.compat.SecurityCraftCompat;
import dinner.dev.data.CannonDamageData;
import dinner.dev.network.BreachNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles block lifecycle synchronization:
 * - Clears stored damage and crack animations when a block is manually broken.
 * - Re-sends crack overlays and damage cache data when a player starts watching a chunk.
 */
public class BlockLifecycleHandler {

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        BlockPos pos = event.getPos();
        CannonDamageData data = CannonDamageData.get(serverLevel);
        if (data.hasDamage(pos)) {
            CannonDamageService.clearDamageAndSync(serverLevel, pos, data);
        }

        // Also clear companion half of double blocks (e.g. Reinforced Door)
        BlockPos companion = CannonDamageService.findCompanionHalf(pos, event.getState());
        if (companion != null && data.hasDamage(companion)) {
            CannonDamageService.clearDamageAndSync(serverLevel, companion, data);
        }
    }

    @SubscribeEvent
    public void onChunkWatchSent(ChunkWatchEvent.Sent event) {
        if (!Config.enabled) return;

        ServerLevel level = event.getLevel();
        ServerPlayer player = event.getPlayer();
        ChunkPos chunkPos = event.getChunk().getPos();

        CannonDamageData data = CannonDamageData.get(level);
        Map<BlockPos, Float> entries = data.getEntriesInChunk(chunkPos);
        if (entries.isEmpty()) return;

        Map<BlockPos, Float> validEntries = new HashMap<>();
        for (Map.Entry<BlockPos, Float> entry : entries.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = event.getChunk().getBlockState(pos);
            if (!SecurityCraftCompat.isTargetableBlock(state.getBlock())) {
                CannonDamageService.clearDamageAndSync(level, pos, data);
                continue;
            }

            float damage = entry.getValue();
            float maxHp = (float) Math.max(1.0, Config.getBlockHp(state.getBlock()));
            int stage = CannonDamageData.computeStage(damage, maxHp);
            CannonDamageData.sendBreakProgressToPlayer(player, pos, stage);
            validEntries.put(pos, damage);
        }

        if (!validEntries.isEmpty()) {
            BreachNetwork.syncChunkDamageToPlayer(player, validEntries);
        }
    }
}
