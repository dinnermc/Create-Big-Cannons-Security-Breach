package dinner.dev.handler;

import dinner.dev.Config;
import dinner.dev.compat.SecurityCraftCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Buffers and deduplicates CBC explosion ray hits per explosion instance so each
 * affected SecurityCraft block receives damage exactly once per detonation
 * (using the highest ray power that reached that block).
 */
public final class EditBlockHelper {

    private static final class PendingExplosion {
        final ServerLevel level;
        final Map<BlockPos, Float> maxPowerByPos = new HashMap<>();

        PendingExplosion(ServerLevel level) {
            this.level = level;
        }
    }

    private static final Map<Object, PendingExplosion> PENDING =
            Collections.synchronizedMap(new WeakHashMap<>());

    private EditBlockHelper() {}

    /**
     * Called from CBC's CustomExplosion#editBlock during ray marching.
     * Returns true if the block is a targetable SecurityCraft block and CBC's
     * default block transformation should be cancelled.
     */
    public static boolean recordExplosionHit(Level level, BlockPos pos, BlockState state, float power, Object explosionInstance) {
        if (!Config.enabled) return false;
        if (!SecurityCraftCompat.isTargetableBlock(state.getBlock())) return false;

        if (level instanceof ServerLevel serverLevel && power >= Config.minExplosionPower) {
            synchronized (PENDING) {
                PendingExplosion pending = PENDING.computeIfAbsent(explosionInstance, k -> new PendingExplosion(serverLevel));
                pending.maxPowerByPos.merge(pos.immutable(), power, Math::max);
            }
        }
        return true;
    }

    /**
     * Called once at the start of CustomExplosion$Impl#finalizeExplosion after all
     * explosion rays have finished, applying deduplicated damage to each hit block.
     */
    public static void flushExplosion(Object explosionInstance) {
        PendingExplosion pending;
        synchronized (PENDING) {
            pending = PENDING.remove(explosionInstance);
        }
        if (pending == null || pending.maxPowerByPos.isEmpty()) return;
        if (!Config.enabled) return;

        ServerLevel level = pending.level;
        Entity source = (explosionInstance instanceof Explosion exp) ? exp.getDirectSourceEntity() : null;
        double munitionMult = resolveExplosionMultiplier(explosionInstance, source);
        if (munitionMult <= 0.0) return;

        int feedbackCount = 0;
        for (Map.Entry<BlockPos, Float> entry : pending.maxPowerByPos.entrySet()) {
            BlockPos pos = entry.getKey();
            if (!level.isLoaded(pos)) continue;

            BlockState state = level.getBlockState(pos);
            if (!SecurityCraftCompat.isTargetableBlock(state.getBlock())) continue;

            float maxPower = entry.getValue();
            float damage = (float) (maxPower * 25.0 * Config.explosionDamageMultiplier * munitionMult);
            if (damage < 0.01f) continue;

            // Cap non-breach feedback spam on large blasts while still showing visual punch
            boolean playFeedback = feedbackCount < 6;
            if (CannonDamageService.applyDamage(level, pos, state, damage, source, playFeedback)) {
                feedbackCount++;
            }
        }
    }

    private static double resolveExplosionMultiplier(Object explosionInstance, Entity source) {
        String expClass = explosionInstance.getClass().getSimpleName();
        if (expClass.contains("MortarStone")) {
            return Config.mortarStoneMultiplier;
        }
        if (source != null) {
            String srcName = source.getClass().getSimpleName();
            if (srcName.contains("APShell")) return Config.apShellMultiplier;
            if (srcName.contains("DropMortar")) return Config.dropMortarMultiplier;
            if (srcName.contains("MortarStone")) return Config.mortarStoneMultiplier;
            if (srcName.contains("HEShell")) return Config.heShellMultiplier;
            if (srcName.contains("Autocannon")) return Config.autocannonMultiplier;
        }
        return Config.heShellMultiplier;
    }
}
