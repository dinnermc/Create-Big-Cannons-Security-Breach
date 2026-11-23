package dinner.dev.handler;

import dinner.dev.Config;
import dinner.dev.compat.SecurityCraftCompat;
import dinner.dev.data.CannonDamageData;
import dinner.dev.network.BreachNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

/**
 * Central service that applies cannon damage, repairs, cleanup, audiovisual
 * feedback, double-block handling, and client sync for SecurityCraft blocks.
 */
public final class CannonDamageService {

    private CannonDamageService() {}

    /**
     * Applies damage to a targetable SecurityCraft block at {@code pos}.
     *
     * @return true if the block was damaged or broken.
     */
    public static boolean applyDamage(ServerLevel level, BlockPos pos, BlockState state, float damage, @Nullable Entity source, boolean playFeedback) {
        if (!Config.enabled || damage < 0.01f || !Float.isFinite(damage)) return false;
        if (!SecurityCraftCompat.isTargetableBlock(state.getBlock())) return false;

        CannonDamageData data = CannonDamageData.get(level);
        float maxHp = (float) Math.max(1.0, Config.getBlockHp(state.getBlock()));
        float current = data.getDamage(pos);
        float newDamage = current + damage;

        if (newDamage >= maxHp) {
            breakBlock(level, pos, state, source, data, true);
        } else {
            data.setDamage(pos, newDamage);
            data.markDamaged(pos, level.getGameTime());
            int stage = CannonDamageData.computeStage(newDamage, maxHp);
            data.updateBreakProgress(level, pos, stage);
            BreachNetwork.syncBlockDamage(level, pos, newDamage);

            if (playFeedback) {
                spawnImpactFeedback(level, pos, state, source, false);
            }
        }
        return true;
    }

    private static void breakBlock(ServerLevel level, BlockPos pos, BlockState state, @Nullable Entity source, CannonDamageData data, boolean playFeedback) {
        // Clean companion half of double blocks (e.g. Reinforced Door, Keypad Door)
        BlockPos companionPos = findCompanionHalf(pos, state);
        if (companionPos != null) {
            clearDamageAndSync(level, companionPos, data);
        }

        if (playFeedback) {
            spawnImpactFeedback(level, pos, state, source, true);
        }

        clearDamageAndSync(level, pos, data);
        level.destroyBlock(pos, Config.dropBlocksOnBreak, source);
    }

    /**
     * Repairs a damaged block by {@code repairAmount} HP.
     *
     * @return remaining damage after repair, or -1 if the block was not damaged.
     */
    public static float repairBlock(ServerLevel level, BlockPos pos, BlockState state, float repairAmount) {
        if (repairAmount <= 0.0f) return -1.0f;
        CannonDamageData data = CannonDamageData.get(level);
        float current = data.getDamage(pos);
        if (current <= 0.0f) return -1.0f;

        float maxHp = (float) Math.max(1.0, Config.getBlockHp(state.getBlock()));
        float newDamage = Math.max(0.0f, current - repairAmount);

        if (newDamage <= 0.001f) {
            clearDamageAndSync(level, pos, data);
            return 0.0f;
        } else {
            data.setDamage(pos, newDamage);
            int stage = CannonDamageData.computeStage(newDamage, maxHp);
            data.updateBreakProgress(level, pos, stage);
            BreachNetwork.syncBlockDamage(level, pos, newDamage);
            return newDamage;
        }
    }

    public static void clearDamageAndSync(ServerLevel level, BlockPos pos, CannonDamageData data) {
        boolean existed = data.clearDamage(pos);
        CannonDamageData.clearBreakProgressImmediate(level, pos);
        if (existed) {
            BreachNetwork.syncBlockDamage(level, pos, 0.0f);
        }
    }

    @Nullable
    public static BlockPos findCompanionHalf(BlockPos pos, BlockState state) {
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            DoubleBlockHalf half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
            return half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        }
        return null;
    }

    private static void spawnImpactFeedback(ServerLevel level, BlockPos pos, BlockState state, @Nullable Entity source, boolean breached) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;

        if (Config.impactSoundsEnabled) {
            SoundType soundType = state.getSoundType(level, pos, source);
            if (breached) {
                level.playSound(null, cx, cy, cz, soundType.getBreakSound(), SoundSource.BLOCKS, 1.3f, 0.85f);
                level.playSound(null, cx, cy, cz, SoundEvents.ANVIL_BREAK, SoundSource.BLOCKS, 0.55f, 0.7f);
            } else {
                level.playSound(null, cx, cy, cz, soundType.getHitSound(), SoundSource.BLOCKS, 0.9f, 0.9f + level.random.nextFloat() * 0.2f);
            }
        }

        if (Config.impactParticlesEnabled) {
            BlockParticleOption blockParticles = new BlockParticleOption(ParticleTypes.BLOCK, state);
            if (breached) {
                level.sendParticles(blockParticles, cx, cy, cz, 24, 0.4, 0.4, 0.4, 0.15);
                level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, cx, cy, cz, 6, 0.3, 0.3, 0.3, 0.03);
            } else {
                level.sendParticles(blockParticles, cx, cy, cz, 8, 0.35, 0.35, 0.35, 0.08);
            }
        }
    }
}
