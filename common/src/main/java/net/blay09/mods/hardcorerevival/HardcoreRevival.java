package net.blay09.mods.hardcorerevival;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.hardcorerevival.command.ReviveCommand;
import net.blay09.mods.hardcorerevival.compat.Compat;
import net.blay09.mods.hardcorerevival.config.HardcoreRevivalConfig;
import net.blay09.mods.hardcorerevival.handler.*;
import net.blay09.mods.hardcorerevival.network.ModNetworking;
import net.blay09.mods.hardcorerevival.stats.ModStats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HardcoreRevival {
    public static final String MOD_ID = "hardcorerevival";

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

}
