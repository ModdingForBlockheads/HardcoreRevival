package net.blay09.mods.hardcorerevival;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.proxy.SidedProxy;
import net.blay09.mods.hardcorerevival.capability.HardcoreRevivalData;
import net.blay09.mods.hardcorerevival.capability.HardcoreRevivalDataImpl;
import net.blay09.mods.hardcorerevival.capability.InvalidHardcoreRevivalData;
import net.blay09.mods.hardcorerevival.command.ReviveCommand;
import net.blay09.mods.hardcorerevival.compat.Compat;
import net.blay09.mods.hardcorerevival.config.HardcoreRevivalConfig;
import net.blay09.mods.hardcorerevival.handler.*;
import net.blay09.mods.hardcorerevival.network.ModNetworking;
import net.blay09.mods.hardcorerevival.stats.ModStats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HardcoreRevival {
    public static final String MOD_ID = "hardcorerevival";

    private static final SidedProxy<HardcoreRevivalManager> manager = Balm.sidedProxy("net.blay09.mods.hardcorerevival.HardcoreRevivalManager", "net.blay09.mods.hardcorerevival.client.HardcoreRevivalClientManager");
    private static final HardcoreRevivalData clientRevivalData = new HardcoreRevivalDataImpl();

    public static final Logger logger = LogManager.getLogger();

    public static void initialize() {
        HardcoreRevivalConfig.initialize();

        ModNetworking.initialize(Balm.getNetworking());
        ModStats.initialize(Balm.getStats());

        Balm.getCommands().register(ReviveCommand::register);

        KnockoutHandler.initialize();
        KnockoutSyncHandler.initialize();
        KnockoutRestrictionHandler.initialize();
        LoginLogoutHandler.initialize();
        RescueHandler.initialize();

        Balm.initializeIfLoaded(Compat.MR_CRAYFISHS_GUN_MOD, "net.blay09.mods.hardcorerevival.compat.MrCrayfishsGunModAddon");
        Balm.initializeIfLoaded(Compat.INVENTORY_TOTEM, "new.blay09.mods.hardcorerevival.compat.InventoryTotemAddon");
    }

    public static HardcoreRevivalManager getManager() {
        return manager.get();
    }

    public static HardcoreRevivalData getRevivalData(Entity entity) {
        return entity instanceof Player player ? getManager().getRevivalData(player) : InvalidHardcoreRevivalData.INSTANCE;
    }

    public static HardcoreRevivalData getClientRevivalData() {
        return clientRevivalData;
    }

}
