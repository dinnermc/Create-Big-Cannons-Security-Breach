package dinner.dev.mixin;

import dinner.dev.handler.ProjectileHitHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "rbasamoyai/createbigcannons/munitions/big_cannon/AbstractBigCannonProjectile")
public class BigCannonProjectileMixin {

    @Inject(
            method = "calculateBlockPenetration",
            at = @At("HEAD"),
            remap = false
    )
    private void cbcSecurityBreach$onBigCannonBlockHit(
            @Coerce Object context,
            BlockState state,
            BlockHitResult result,
            CallbackInfoReturnable<?> cir
    ) {
        ProjectileHitHelper.handleCannonProjectileHit(this, state, result);
    }
}
