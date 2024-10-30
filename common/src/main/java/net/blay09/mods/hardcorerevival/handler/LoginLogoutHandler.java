package net.blay09.mods.hardcorerevival.handler;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.balm.api.event.PlayerLogoutEvent;
import net.blay09.mods.hardcorerevival.HardcoreRevivalManager;
import net.blay09.mods.hardcorerevival.PlayerHardcoreRevivalManager;
import net.blay09.mods.hardcorerevival.config.HardcoreRevivalConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;


public class LoginLogoutHandler {

    public static void initialize() {
        Balm.getEvents().onEvent(PlayerLoginEvent.class, LoginLogoutHandler::onPlayerLogin);
        Balm.getEvents().onEvent(PlayerLogoutEvent.class, LoginLogoutHandler::onPlayerLogout);
    }

    public static void onPlayerLogin(PlayerLoginEvent event) {
        ServerPlayer player = event.getPlayer();

        if (HardcoreRevivalConfig.getActive().continueTimerWhileOffline && PlayerHardcoreRevivalManager.isKnockedOut(player)) {
            final var now = System.currentTimeMillis();
            final var then = PlayerHardcoreRevivalManager.getLastLogoutAt(player);
            final var millisPassed = (int) Math.max(0, now - then);
            final var secondsPassed = millisPassed / 1000;
            final var ticksPassed = secondsPassed * 20;
            PlayerHardcoreRevivalManager.setKnockoutTicksPassed(player, PlayerHardcoreRevivalManager.getKnockoutTicksPassed(player) + ticksPassed);
        }

        HardcoreRevivalManager.updateKnockoutEffects(player);
    }

    public static void onPlayerLogout(PlayerLogoutEvent event) {
        Player player = event.getPlayer();
        PlayerHardcoreRevivalManager.setLastLogoutAt(player, player.level().getGameTime());
    }

}
