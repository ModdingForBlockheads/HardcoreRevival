package net.blay09.mods.hardcorerevival;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.hardcorerevival.client.HardcoreRevivalClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(HardcoreRevival.MOD_ID)
public class ForgeHardcoreRevival {

    public ForgeHardcoreRevival() {
        Balm.initialize(HardcoreRevival.MOD_ID, EmptyLoadContext.INSTANCE, HardcoreRevival::initialize);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> BalmClient.initialize(HardcoreRevival.MOD_ID, EmptyLoadContext.INSTANCE, HardcoreRevivalClient::initialize));
    }

}
