package net.blay09.mods.hardcorerevival.handler;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.*;
import net.blay09.mods.hardcorerevival.HardcoreRevivalManager;
import net.blay09.mods.hardcorerevival.PlayerHardcoreRevivalManager;
import net.blay09.mods.hardcorerevival.config.HardcoreRevivalConfig;
import net.blay09.mods.hardcorerevival.network.RevivalProgressMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class RescueHandler {

    public static void initialize() {
        Balm.getEvents().onEvent(UseItemEvent.class, RescueHandler::onUseItem);
        Balm.getEvents().onEvent(UseBlockEvent.class, RescueHandler::onUseBlock);
        Balm.getEvents().onEvent(PlayerAttackEvent.class, RescueHandler::onAttack);

        Balm.getEvents().onTickEvent(TickType.ServerPlayer, TickPhase.End, RescueHandler::onPlayerTick);
    }

    public static void onUseItem(UseItemEvent event) {
        // Prevent player from using items while they're rescuing
        if (HardcoreRevivalManager.isRescuing(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    public static void onUseBlock(UseBlockEvent event) {
        // Prevent player from placing blocks while they're rescuing
        if (HardcoreRevivalManager.isRescuing(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    public static void onAttack(PlayerAttackEvent event) {
        // Stop rescuing if the player does something other than rescuing
        HardcoreRevivalManager.abortRescue(event.getPlayer());
    }

    public static void onPlayerTick(ServerPlayer player) {
        // if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
        Player rescueTarget = PlayerHardcoreRevivalManager.getRescueTarget(player);
        if (rescueTarget != null) {
            // Stop rescuing if the target logged out
            final int knockoutTicksPassed = PlayerHardcoreRevivalManager.getKnockoutTicksPassed(rescueTarget);
            final int maxTicksUntilDeath = HardcoreRevivalConfig.getActive().secondsUntilDeath * 20;
            if (!rescueTarget.isAlive() || (maxTicksUntilDeath > 0 && knockoutTicksPassed >= maxTicksUntilDeath)) {
                HardcoreRevivalManager.abortRescue(player);
            } else {
                // Stop rescuing if the player is out of range
                float dist = player.distanceTo(rescueTarget);
                if (dist > HardcoreRevivalConfig.getActive().rescueDistance) {
                    HardcoreRevivalManager.abortRescue(player);
                } else {
                    int rescueTime = PlayerHardcoreRevivalManager.getRescueTime(player) + 1;
                    PlayerHardcoreRevivalManager.setRescueTime(player, rescueTime);

                    // Delay death while rescuing
                    PlayerHardcoreRevivalManager.setKnockoutTicksPassed(rescueTarget, knockoutTicksPassed - 1);

                    int maxRescueActionTicks = HardcoreRevivalConfig.getActive().rescueActionTicks;
                    int step = maxRescueActionTicks / 4;
                    if (rescueTime >= maxRescueActionTicks) {
                        HardcoreRevivalManager.finishRescue(player);
                    } else if (rescueTime % step == 0) {
                        Balm.getNetworking()
                                .sendTo(player, new RevivalProgressMessage(rescueTarget.getId(), (float) rescueTime / (float) maxRescueActionTicks));
                        KnockoutSyncHandler.sendHardcoreRevivalData(rescueTarget, rescueTarget, true);
                    }
                }
            }
        }
    }


}
