package net.blay09.mods.hardcorerevival.fabric;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.fabricmc.api.ModInitializer;

public class FabricHardcoreRevival implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initialize(HardcoreRevival.MOD_ID, EmptyLoadContext.INSTANCE, HardcoreRevival::initialize);
    }
}
