package dinner.dev.mixin;

import dinner.dev.handler.EditBlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "rbasamoyai/createbigcannons/munitions/big_cannon/mortar_stone/MortarStoneExplosion")
public class MortarStoneExplosionMixin {
    @Inject(method = "editBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void cbcSecurityBreach$onEditBlock(Level level, BlockPos pos, BlockState state, FluidState fluidState, float power, CallbackInfo ci) {
        if (EditBlockHelper.handleEditBlock(level, pos, state, power, this)) {
            ci.cancel();
        }
    }
}
