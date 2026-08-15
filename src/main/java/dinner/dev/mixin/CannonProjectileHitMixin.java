package dinner.dev.mixin;

import dinner.dev.Config;
import dinner.dev.data.CannonDamageData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "rbasamoyai.createbigcannons.munitions.abstract_cannon_projectile.AbstractCannonProjectile")
public class CannonProjectileHitMixin {

    @Inject(
        method = "onHitBlock",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void cbcSecurityBreach$onKineticHit(BlockHitResult result, CallbackInfo ci) {
        if (!Config.enabled) return;

        Entity self = (Entity) (Object) this;
        Level level = self.level();
        if (level.isClientSide) return;

        BlockPos pos = result.getBlockPos();
        BlockState state = level.getBlockState(pos);
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!"securitycraft".equals(blockId.getNamespace())) return;

        double velocity = self.getDeltaMovement().length();
        float damage = (float) (velocity * 20.0 * Config.directHitMultiplier);

        if (level instanceof ServerLevel serverLevel) {
            CannonDamageData data = CannonDamageData.get(serverLevel);
            float current = data.getDamage(pos);
            float maxHp = (float) Config.getBlockHp(blockId.getPath());
            float newDamage = current + damage;

            int crackStage = Math.min(9, (int) (newDamage / maxHp * 10.0f));
            CannonDamageData.setBreakProgress(serverLevel, pos, crackStage);

            if (newDamage >= maxHp) {
                serverLevel.destroyBlock(pos, true, self);
                data.clearDamage(pos);
                CannonDamageData.setBreakProgress(serverLevel, pos, -1);
            } else {
                data.addDamage(pos, damage);
            }
        }

        ci.cancel();
    }
}
