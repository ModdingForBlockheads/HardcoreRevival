package net.blay09.mods.hardcorerevival.mixin;

import net.blay09.mods.hardcorerevival.MixinHooks;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "isHurt()Z", at = @At("HEAD"), cancellable = true)
    private void isHurt(CallbackInfoReturnable<Boolean> ci) {
        Player entity = (Player) (Object) this;
        if (MixinHooks.shouldCancelHealing(entity)) {
            ci.setReturnValue(false);
        }
    }

}
