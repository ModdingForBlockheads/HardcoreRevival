package net.blay09.mods.hardcorerevival;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.forge.provider.ForgeBalmProviders;
import net.blay09.mods.hardcorerevival.capability.HardcoreRevivalDataImpl;
import net.blay09.mods.hardcorerevival.client.HardcoreRevivalClient;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

@Mod(HardcoreRevival.MOD_ID)
public class ForgeHardcoreRevival {

    private final Capability<HardcoreRevivalData> hardcoreRevivalDataCapability = CapabilityManager.get(new CapabilityToken<>() {
    });

    public ForgeHardcoreRevival() {
        Balm.initialize(HardcoreRevival.MOD_ID, EmptyLoadContext.INSTANCE, HardcoreRevival::initialize);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> BalmClient.initialize(HardcoreRevival.MOD_ID, EmptyLoadContext.INSTANCE, HardcoreRevivalClient::initialize));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerCapabilities);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, this::attachEntityCapabilities);

        ForgeBalmProviders providers = (ForgeBalmProviders) Balm.getProviders();
        providers.register(HardcoreRevivalData.class, new CapabilityToken<>() {
        });
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(HardcoreRevivalData.class);
    }

    private void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(HardcoreRevival.MOD_ID, "entity_data"), new ICapabilityProvider() {
                private LazyOptional<HardcoreRevivalData> revival;

                private LazyOptional<HardcoreRevivalData> getRevivalCapabilityInstance() {
                    if (revival == null) {
                        revival = LazyOptional.of(HardcoreRevivalDataImpl::new);
                    }

                    return revival;
                }

                @Override
                public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction facing) {
                    return hardcoreRevivalDataCapability.orEmpty(cap, getRevivalCapabilityInstance());
                }
            });
        }
    }
}
