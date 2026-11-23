package dinner.dev.mixin;

import dinner.dev.handler.EditBlockHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "rbasamoyai/createbigcannons/remix/CustomExplosion$Impl")
public class CustomExplosionFinalizeMixin {

    @Inject(
            method = "finalizeExplosion",
            at = @At("HEAD"),
            remap = false
    )
    private void cbcSecurityBreach$onFinalizeExplosion(boolean spawnParticles, CallbackInfo ci) {
        EditBlockHelper.flushExplosion(this);
    }
}
