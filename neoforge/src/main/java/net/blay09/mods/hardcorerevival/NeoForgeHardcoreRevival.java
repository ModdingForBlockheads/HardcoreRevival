package net.blay09.mods.hardcorerevival;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(HardcoreRevival.MOD_ID)
public class NeoForgeHardcoreRevival {

    public NeoForgeHardcoreRevival(IEventBus eventBus) {
        final var context = new NeoForgeLoadContext(eventBus);
        Balm.initialize(HardcoreRevival.MOD_ID, context, HardcoreRevival::initialize);
    }

}
