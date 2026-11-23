package dinner.dev.handler;

import com.mojang.logging.LogUtils;
import dinner.dev.Config;
import dinner.dev.compat.SecurityCraftCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * Handles direct kinetic impacts from CBC big cannon projectiles, autocannon rounds,
 * and grapeshot/shrapnel/flak bursts against SecurityCraft blocks.
 */
public final class ProjectileHitHelper {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static volatile boolean subProjVelocityChecked = false;
    private static Method subProjVelocityMethod;

    private ProjectileHitHelper() {}

    public static void handleCannonProjectileHit(Object projectileObj, BlockState state, BlockHitResult result) {
        if (!Config.enabled || Config.directHitMultiplier <= 0.0) return;
        if (!(projectileObj instanceof Entity self)) return;
        if (!(self.level() instanceof ServerLevel serverLevel)) return;
        if (!SecurityCraftCompat.isTargetableBlock(state.getBlock())) return;

        double munitionMult = resolveProjectileMultiplier(self);
        if (munitionMult <= 0.0) return;

        double speed = self.getDeltaMovement().length();
        if (speed < 0.05) return;

        float damage = (float) (speed * 35.0 * Config.directHitMultiplier * munitionMult);
        if (damage < 0.01f) return;

        CannonDamageService.applyDamage(serverLevel, result.getBlockPos(), state, damage, self, true);
    }

    public static void handleShrapnelHit(Object burstObj, BlockHitResult result, Object subProjectileObj) {
        if (!Config.enabled || Config.directHitMultiplier <= 0.0 || Config.shrapnelMultiplier <= 0.0) return;
        if (!(burstObj instanceof Entity burst)) return;
        if (!(burst.level() instanceof ServerLevel serverLevel)) return;

        BlockPos pos = result.getBlockPos();
        if (!serverLevel.isLoaded(pos)) return;
        BlockState state = serverLevel.getBlockState(pos);
        if (!SecurityCraftCompat.isTargetableBlock(state.getBlock())) return;

        double speed = readSubProjectileSpeed(subProjectileObj);
        if (speed < 0.05) return;

        float damage = (float) (speed * 14.0 * Config.directHitMultiplier * Config.shrapnelMultiplier);
        if (damage < 0.01f) return;

        // Avoid sound spam from dozens of simultaneous pellets; breach effects still play on break
        CannonDamageService.applyDamage(serverLevel, pos, state, damage, burst, false);
    }

    private static double resolveProjectileMultiplier(Entity projectile) {
        String name = projectile.getClass().getSimpleName();
        // Utility/non-damaging rounds should not damage fortified blocks
        if (name.contains("TrafficCone") || name.contains("SmokeShell") || name.contains("FluidShell")) {
            return 0.0;
        }
        if (name.contains("APShot")) return Config.apShotMultiplier;
        if (name.contains("APShell")) return Config.apShellMultiplier;
        if (name.contains("SolidShot")) return Config.solidShotMultiplier;
        if (name.contains("HEShell")) return Config.heShellMultiplier;
        if (name.contains("DropMortar")) return Config.dropMortarMultiplier;
        if (name.contains("MortarStone")) return Config.mortarStoneMultiplier;
        if (name.contains("Autocannon") || name.contains("MachineGun")) return Config.autocannonMultiplier;
        if (name.contains("Grapeshot") || name.contains("Shrapnel")) return Config.shrapnelMultiplier;
        return 1.0;
    }

    private static double readSubProjectileSpeed(Object subProjectile) {
        if (subProjectile == null) return 1.0;
        if (!subProjVelocityChecked) {
            synchronized (ProjectileHitHelper.class) {
                if (!subProjVelocityChecked) {
                    try {
                        subProjVelocityMethod = subProjectile.getClass().getMethod("velocity");
                    } catch (NoSuchMethodException e) {
                        LOGGER.debug("SubProjectile#velocity() not found", e);
                    }
                    subProjVelocityChecked = true;
                }
            }
        }
        if (subProjVelocityMethod != null) {
            try {
                Object res = subProjVelocityMethod.invoke(subProjectile);
                if (res instanceof double[] v && v.length >= 3) {
                    return Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
                }
            } catch (Exception e) {
                LOGGER.debug("Failed to read SubProjectile velocity", e);
            }
        }
        return 1.0;
    }
}
