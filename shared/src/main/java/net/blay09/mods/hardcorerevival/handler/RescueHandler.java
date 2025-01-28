package net.blay09.mods.hardcorerevival.handler;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.*;
import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.blay09.mods.hardcorerevival.capability.HardcoreRevivalData;
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
        if (HardcoreRevival.getManager().isRescuing(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    public static void onUseBlock(UseBlockEvent event) {
        // Prevent player from placing blocks while they're rescuing
        if (HardcoreRevival.getManager().isRescuing(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    public static void onAttack(PlayerAttackEvent event) {
        // Stop rescuing if the player does something other than rescuing
        HardcoreRevival.getManager().abortRescue(event.getPlayer());
    }

    public static void onPlayerTick(ServerPlayer player) {
        // if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
        HardcoreRevivalData revivalData = HardcoreRevival.getRevivalData(player);
        Player rescueTarget = revivalData.getRescueTarget();
        if (rescueTarget != null) {
            // Stop rescuing if the target logged out
            HardcoreRevivalData rescueTargetData = HardcoreRevival.getRevivalData(rescueTarget);
            final int knockoutTicksPassed = rescueTargetData.getKnockoutTicksPassed();
            int maxTicksUntilDeath = HardcoreRevivalConfig.getActive().ticksUntilDeath;
            if (!rescueTarget.isAlive() || (maxTicksUntilDeath > 0 && knockoutTicksPassed >= maxTicksUntilDeath)) {
                HardcoreRevival.getManager().abortRescue(player);
            } else {
                // Stop rescuing if the player is out of range
                float dist = player.distanceTo(rescueTarget);
                if (dist > HardcoreRevivalConfig.getActive().rescueDistance) {
                    HardcoreRevival.getManager().abortRescue(player);
                } else {
                    int rescueTime = revivalData.getRescueTime() + 1;
                    revivalData.setRescueTime(rescueTime);

                    // Delay death while rescuing
                    rescueTargetData.setKnockoutTicksPassed(knockoutTicksPassed - 1);

                    int maxRescueActionTicks = HardcoreRevivalConfig.getActive().rescueActionTicks;
                    int step = maxRescueActionTicks / 4;
                    if (rescueTime >= maxRescueActionTicks) {
                        HardcoreRevival.getManager().finishRescue(player);
                    } else if (rescueTime % step == 0) {
                        Balm.getNetworking()
                                .sendTo(player, new RevivalProgressMessage(rescueTarget.getId(), (float) rescueTime / (float) maxRescueActionTicks));
                        KnockoutSyncHandler.sendHardcoreRevivalData(rescueTarget, rescueTarget, rescueTargetData, true);
                    }
                }
            }
        }
    }


}
