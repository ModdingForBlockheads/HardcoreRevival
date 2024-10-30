package net.blay09.mods.hardcorerevival.mixin;

import net.blay09.mods.hardcorerevival.PlayerHardcoreRevivalManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "canBeSeenAsEnemy()Z", at = @At("HEAD"), cancellable = true)
    private void canBeSeenAsEnemy(CallbackInfoReturnable<Boolean> cir) {
        final var livingEntity = (LivingEntity) (Object) this;
        if (livingEntity instanceof Player player && PlayerHardcoreRevivalManager.isKnockedOut(player)) {
            cir.setReturnValue(false);
        }
    }
}
