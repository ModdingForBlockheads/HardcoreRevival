package net.blay09.mods.hardcorerevival.handler;


import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.*;
import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.blay09.mods.hardcorerevival.api.PlayerAboutToKnockOutEvent;
import net.blay09.mods.hardcorerevival.capability.HardcoreRevivalData;
import net.blay09.mods.hardcorerevival.config.HardcoreRevivalConfig;
import net.blay09.mods.hardcorerevival.HardcoreRevivalManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;


public class KnockoutHandler {

    public static void initialize() {
        Balm.getEvents().onEvent(LivingDamageEvent.class, KnockoutHandler::onPlayerDamage);
        Balm.getEvents().onEvent(PlayerRespawnEvent.class, KnockoutHandler::onPlayerRespawn);

        Balm.getEvents().onTickEvent(TickType.ServerPlayer, TickPhase.Start, KnockoutHandler::onPlayerTick);
    }

    public static void onPlayerDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DamageSource damageSource = event.getDamageSource();

            if (HardcoreRevival.getRevivalData(event.getEntity()).isKnockedOut()) {
                Entity attacker = damageSource.getEntity();
                if (attacker instanceof Mob mob) {
                    mob.setTarget(null);
                }
                if (!damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !damageSource.is(HardcoreRevivalManager.NOT_RESCUED_IN_TIME)) {
                    event.setCanceled(true);
                }
                return;
            }

            boolean wouldDie = player.getHealth() - event.getDamageAmount() <= 0f;
            if (wouldDie && isKnockoutEnabledFor(player, damageSource)) {
                final var aboutToKnockOutEvent = new PlayerAboutToKnockOutEvent(player, damageSource);
                Balm.getEvents().fireEvent(aboutToKnockOutEvent);

                if (!aboutToKnockOutEvent.isCanceled()) {
                    event.setDamageAmount(Math.min(event.getDamageAmount(), Math.max(0f, player.getHealth() - 1f)));
                    HardcoreRevival.getManager().knockout(player, damageSource);
                }
            }
        }
    }

    private static boolean holdsDeathProtectionItem(ServerPlayer player) {
        for (final var hand : InteractionHand.values()) {
            final var itemStack = player.getItemInHand(hand);
            final var deathProtection = itemStack.get(DataComponents.DEATH_PROTECTION);
            if (deathProtection != null) {
                return true;
            }
        }

        return false;
    }

    private static boolean isKnockoutEnabledFor(ServerPlayer player, DamageSource damageSource) {
        final var server = player.getServer();
        if (server == null) {
            return false;
        }

        boolean canDamageSourceKnockout = !damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) && !damageSource.is(HardcoreRevivalManager.NOT_RESCUED_IN_TIME);
        final var damageSourceId = player.getServer().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getKey(damageSource.type());
        if (!canDamageSourceKnockout || HardcoreRevivalConfig.getActive().instantDeathSources.contains(damageSourceId)) {
            return false;
        }

        if (HardcoreRevivalConfig.getActive().disableInSingleplayer && server.isSingleplayer() && server.getPlayerCount() == 1) {
            return false;
        } else if (HardcoreRevivalConfig.getActive().disableInLonelyMultiplayer && !server.isSingleplayer() && server.getPlayerCount() == 1) {
            return false;
        }

        if (holdsDeathProtectionItem(player)) {
            return false;
        }

        return true;
    }

    public static void onPlayerTick(ServerPlayer player) {
        //if (event.phase == TickEvent.Phase.START && event.side == LogicalSide.SERVER) {
        HardcoreRevivalData revivalData = HardcoreRevival.getRevivalData(player);
        if (revivalData.isKnockedOut() && player.isAlive()) {
            // Make sure health stays locked at half a heart
            player.setHealth(1f);

            revivalData.setKnockoutTicksPassed(revivalData.getKnockoutTicksPassed() + 1);

            if (player.tickCount % 20 == 0) {
                Balm.getHooks().setForcedPose(player, revivalData.isKnockedOut() ? Pose.FALL_FLYING : null);
            }

            int maxTicksUntilDeath = HardcoreRevivalConfig.getActive().secondsUntilDeath * 20;
            if (maxTicksUntilDeath > 0 && revivalData.getKnockoutTicksPassed() >= maxTicksUntilDeath) {
                HardcoreRevival.getManager().notRescuedInTime(player);
            }
        }
    }

    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        HardcoreRevival.getManager().reset(event.getNewPlayer());
    }


}
