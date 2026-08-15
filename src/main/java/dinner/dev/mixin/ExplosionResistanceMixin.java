package dinner.dev.mixin;

import dinner.dev.Config;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class ExplosionResistanceMixin {

    @Inject(method = "getExplosionResistance()F", at = @At("RETURN"), cancellable = true)
    private void cbcSecurityBreach$overrideResistance(CallbackInfoReturnable<Float> cir) {
        if (!Config.enabled) return;

        Block self = (Block) (Object) this;

        Float cached = Config.getCachedResistance(self);
        if (cached != null) {
            if (!cached.isNaN()) {
                cir.setReturnValue(cached);
            }
            return;
        }

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(self);
        if ("securitycraft".equals(blockId.getNamespace())) {
            float resistance = (float) Config.getBlockResistance(blockId.getPath());
            Config.cacheResistance(self, resistance);
            cir.setReturnValue(resistance);
        } else {
            Config.cacheResistance(self, Float.NaN);
        }
    }
}
