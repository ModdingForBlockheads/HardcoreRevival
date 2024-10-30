package net.blay09.mods.hardcorerevival.compat;

import com.mrcrayfish.guns.event.GunFireEvent;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.hardcorerevival.PlayerHardcoreRevivalManager;
import net.blay09.mods.hardcorerevival.config.HardcoreRevivalConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Objects;

public class MrCrayfishsGunModAddon {
    private final ResourceLocation PISTOL = ResourceLocation.fromNamespaceAndPath("cgm", "pistol");

    public MrCrayfishsGunModAddon() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onGunFirePre(GunFireEvent.Pre event) {
        if (PlayerHardcoreRevivalManager.isKnockedOut(event.getEntity())) {
            ResourceLocation mainHandItemKey = Balm.getRegistries().getKey(event.getEntity().getMainHandItem().getItem());
            boolean isFiringPistol = Objects.equals(mainHandItemKey, PISTOL);
            if (isFiringPistol && HardcoreRevivalConfig.getActive().allowPistols) {
                return;
            }

            event.setCanceled(true);
        }
    }
}
