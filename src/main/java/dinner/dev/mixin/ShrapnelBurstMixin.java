package dinner.dev.mixin;

import dinner.dev.handler.ProjectileHitHelper;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "rbasamoyai/createbigcannons/munitions/big_cannon/shrapnel/ShrapnelBurst")
public class ShrapnelBurstMixin {

    @Inject(
            method = "onSubProjectileHitBlock",
            at = @At("HEAD"),
            remap = false
    )
    private void cbcSecurityBreach$onShrapnelBlockHit(
            BlockHitResult result,
            @Coerce Object subProjectile,
            CallbackInfo ci
    ) {
        ProjectileHitHelper.handleShrapnelHit(this, result, subProjectile);
    }
}
