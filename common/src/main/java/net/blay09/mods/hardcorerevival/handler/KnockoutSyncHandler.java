package net.blay09.mods.hardcorerevival.handler;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.ChunkTrackingEvent;
import net.blay09.mods.hardcorerevival.PlayerHardcoreRevivalManager;
import net.blay09.mods.hardcorerevival.network.HardcoreRevivalDataMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class KnockoutSyncHandler {
    public static void initialize() {
        Balm.getEvents().onEvent(ChunkTrackingEvent.Start.class, KnockoutSyncHandler::onStartChunkTracking);
    }

    public static void onStartChunkTracking(ChunkTrackingEvent.Start event) {
        MinecraftServer server = event.getLevel().getServer();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (PlayerHardcoreRevivalManager.isKnockedOut(player)) {
                sendHardcoreRevivalData(event.getPlayer(), player);
            }
        }
    }

    public static void sendHardcoreRevivalDataToWatching(Player player) {
        HardcoreRevivalDataMessage message = new HardcoreRevivalDataMessage(player.getId(),
                PlayerHardcoreRevivalManager.isKnockedOut(player),
                PlayerHardcoreRevivalManager.getKnockoutTicksPassed(player),
                false);
        Balm.getNetworking().sendToTracking(player, message);
        sendHardcoreRevivalData(player, player);
    }

    public static void sendHardcoreRevivalData(Player toPlayer, Player forPlayer) {
        sendHardcoreRevivalData(toPlayer, forPlayer, false);
    }

    public static void sendHardcoreRevivalData(Player toPlayer, Player forPlayer, boolean beingRescued) {
        HardcoreRevivalDataMessage message = new HardcoreRevivalDataMessage(forPlayer.getId(),
                PlayerHardcoreRevivalManager.isKnockedOut(forPlayer),
                PlayerHardcoreRevivalManager.getKnockoutTicksPassed(forPlayer),
                beingRescued);
        Balm.getNetworking().sendTo(toPlayer, message);
    }
}
