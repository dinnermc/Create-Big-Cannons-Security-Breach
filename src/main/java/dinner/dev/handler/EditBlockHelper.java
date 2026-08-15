package dinner.dev.handler;

import dinner.dev.Config;
import dinner.dev.data.CannonDamageData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EditBlockHelper {

    public static boolean handleEditBlock(Level level, BlockPos pos, BlockState state, float power, Object explosionInstance) {
        if (!Config.enabled) return false;

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!"securitycraft".equals(blockId.getNamespace())) return false;

        if (level instanceof ServerLevel serverLevel && power >= Config.minExplosionPower) {
            float damage = power * 2.0f * (float) Config.explosionDamageMultiplier;
            if (damage >= 0.01f) {
                CannonDamageData data = CannonDamageData.get(serverLevel);
                float current = data.getDamage(pos);
                float maxHp = (float) Math.max(Config.getBlockHp(blockId.getPath()), 1);
                float newDamage = current + damage;

                int crackStage = Math.min(9, (int) (newDamage / maxHp * 10.0f));
                CannonDamageData.setBreakProgress(serverLevel, pos, crackStage);

                if (newDamage >= maxHp) {
                    serverLevel.destroyBlock(pos, true, null);
                    data.clearDamage(pos);
                    CannonDamageData.setBreakProgress(serverLevel, pos, -1);
                } else {
                    data.addDamage(pos, damage);
                }
            }
        }

        return true;
    }
}
