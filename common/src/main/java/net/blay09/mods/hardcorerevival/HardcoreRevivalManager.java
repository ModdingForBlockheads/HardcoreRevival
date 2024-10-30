package net.blay09.mods.hardcorerevival;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.hardcorerevival.api.PlayerKnockedOutEvent;
import net.blay09.mods.hardcorerevival.api.PlayerRescuedEvent;
import net.blay09.mods.hardcorerevival.api.PlayerRevivedEvent;
import net.blay09.mods.hardcorerevival.config.HardcoreRevivalConfig;
import net.blay09.mods.hardcorerevival.config.HardcoreRevivalConfigData;
import net.blay09.mods.hardcorerevival.handler.KnockoutSyncHandler;
import net.blay09.mods.hardcorerevival.mixin.ServerPlayerAccessor;
import net.blay09.mods.hardcorerevival.network.RevivalProgressMessage;
import net.blay09.mods.hardcorerevival.network.RevivalSuccessMessage;
import net.blay09.mods.hardcorerevival.stats.ModStats;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;

public class HardcoreRevivalManager {
    public static final ResourceKey<DamageType> NOT_RESCUED_IN_TIME = ResourceKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(HardcoreRevival.MOD_ID, "not_rescued_in_time"));

    public static void knockout(Player player, DamageSource source) {
        if (PlayerHardcoreRevivalManager.isKnockedOut(player)) {
            return;
        }

        player.stopUsingItem();
        player.stopRiding();
        player.removeEffect(MobEffects.REGENERATION);

        PlayerHardcoreRevivalManager.setKnockedOut(player, true);
        PlayerHardcoreRevivalManager.setKnockoutTicksPassed(player, 0);
        PlayerHardcoreRevivalManager.setLastKnockoutAt(player, System.currentTimeMillis());
        player.awardStat(ModStats.knockouts);

        // Punish consecutive knockouts
        final var lastRescuedAt = PlayerHardcoreRevivalManager.getLastRescuedAt(player);
        final var consecutiveThresholdSeconds = HardcoreRevivalConfig.getActive().consecutiveKnockoutThresholdSeconds;
        final var secondsSinceLastRescue = (System.currentTimeMillis() - lastRescuedAt) / 1000;
        final var isConsecutiveKnockout = consecutiveThresholdSeconds > 0 && lastRescuedAt > 0 && secondsSinceLastRescue <= consecutiveThresholdSeconds;
        if (isConsecutiveKnockout) {
            if (HardcoreRevivalConfig.getActive().resumeTimerOnConsecutiveKnockout) {
                PlayerHardcoreRevivalManager.setKnockoutTicksPassed(player, PlayerHardcoreRevivalManager.getLastKnockoutTicksPassed(player));
            }
            final var multiplyTimerOnConsecutiveKnockout = HardcoreRevivalConfig.getActive().multiplyTimerOnConsecutiveKnockout;
            final var maxTicksUntilDeath = HardcoreRevivalConfig.getActive().secondsUntilDeath * 20;
            final var ticksLeft = maxTicksUntilDeath - PlayerHardcoreRevivalManager.getKnockoutTicksPassed(player);
            final var newTicksLeft = (int) (ticksLeft * multiplyTimerOnConsecutiveKnockout);
            PlayerHardcoreRevivalManager.setKnockoutTicksPassed(player, maxTicksUntilDeath - newTicksLeft);
        }

        // Fire event for compatibility addons
        Balm.getEvents().fireEvent(new PlayerKnockedOutEvent(player, source));

        // If enabled, show a death message
        if (player.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
            MinecraftServer server = player.getServer();
            if (server != null) {
                Team team = player.getTeam();
                if (team != null && team.getDeathMessageVisibility() != Team.Visibility.ALWAYS) {
                    if (team.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {
                        server.getPlayerList().broadcastSystemToTeam(player, player.getCombatTracker().getDeathMessage());
                    } else if (team.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OWN_TEAM) {
                        server.getPlayerList().broadcastSystemToAllExceptTeam(player, player.getCombatTracker().getDeathMessage());
                    }
                } else {
                    server.getPlayerList().broadcastSystemMessage(player.getCombatTracker().getDeathMessage(), false);
                }
            }
        }

        updateKnockoutEffects(player);
    }

    public static void wakeup(Player player) {
        wakeup(player, true);
    }

    public static void wakeup(Player player, boolean applyEffects) {
        PlayerHardcoreRevivalManager.setLastRescuedAt(player, System.currentTimeMillis());
        PlayerHardcoreRevivalManager.setLastKnockoutTicksPassed(player, PlayerHardcoreRevivalManager.getKnockoutTicksPassed(player));
        reset(player);

        if (applyEffects) {
            HardcoreRevivalConfigData config = HardcoreRevivalConfig.getActive();
            player.setHealth(config.rescueRespawnHealth);
            player.getFoodData().setFoodLevel(config.rescueRespawnFoodLevel);
            // client only, won't bother: player.getFoodStats().setFoodSaturationLevel((float) config.getRescueRespawnFoodSaturation());

            for (String effectString : config.rescueRespawnEffects) {
                String[] parts = effectString.split("\\|");
                ResourceLocation registryName = ResourceLocation.tryParse(parts[0]);
                if (registryName != null) {
                    final var holder = BuiltInRegistries.MOB_EFFECT.getHolder(registryName);
                    if (holder.isPresent()) {
                        int duration = tryParseInt(parts.length >= 2 ? parts[1] : null, 600);
                        int amplifier = tryParseInt(parts.length >= 3 ? parts[2] : null, 0);
                        player.addEffect(new MobEffectInstance(holder.get(), duration, amplifier));
                    } else {
                        HardcoreRevival.logger.info("Invalid rescue potion effect '{}'", parts[0]);
                    }
                } else {
                    HardcoreRevival.logger.info("Invalid rescue potion effect '{}'", parts[0]);
                }
            }
        }

        Balm.getEvents().fireEvent(new PlayerRevivedEvent(player));
    }

    private static int tryParseInt(@Nullable String text, int defaultVal) {
        if (text != null) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                return defaultVal;
            }
        }
        return defaultVal;
    }

    public static void finishRescue(Player player) {
        Player rescueTarget = PlayerHardcoreRevivalManager.getRescueTarget(player);
        if (rescueTarget != null) {
            MinecraftServer server = rescueTarget.getServer();
            if (server != null) {
                wakeup(rescueTarget);

                Balm.getNetworking().sendTo(player, new RevivalProgressMessage(rescueTarget.getId(), -1f));
                Balm.getNetworking().sendTo(rescueTarget, new RevivalSuccessMessage(rescueTarget.getId()));
                Balm.getNetworking().sendToTracking(rescueTarget, new RevivalSuccessMessage(rescueTarget.getId()));

                PlayerHardcoreRevivalManager.setRescueTarget(player, null);

                Balm.getEvents().fireEvent(new PlayerRescuedEvent(rescueTarget, player));
            }
        }

        Balm.getHooks().setForcedPose(player, null);
    }

    public static void abortRescue(Player player) {
        Player rescueTarget = PlayerHardcoreRevivalManager.getRescueTarget(player);
        if (rescueTarget != null) {
            PlayerHardcoreRevivalManager.setRescueTime(player, 0);
            PlayerHardcoreRevivalManager.setRescueTarget(player, null);
            Balm.getNetworking().sendTo(player, new RevivalProgressMessage(-1, -1));
            KnockoutSyncHandler.sendHardcoreRevivalData(rescueTarget, rescueTarget);

            Balm.getHooks().setForcedPose(player, null);
        }
    }

    public static void notRescuedInTime(Player player) {
        // Disable respawn invulnerability to prevent players from surviving knockout after login with offline timer enabled
        if (player instanceof ServerPlayerAccessor accessor) {
            accessor.setSpawnInvulnerableTime(0);
        }

        final var damageTypes = player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        final var damageSource = new DamageSource(damageTypes.getHolderOrThrow(NOT_RESCUED_IN_TIME));
        PlayerHardcoreRevivalManager.setLastKnockoutTicksPassed(player, 0);
        reset(player);
        player.hurt(damageSource, Float.MAX_VALUE);
    }

    public static void reset(Player player) {
        PlayerHardcoreRevivalManager.setKnockedOut(player, false);
        PlayerHardcoreRevivalManager.setKnockoutTicksPassed(player, 0);

        updateKnockoutEffects(player);
    }

    public static void updateKnockoutEffects(Player player) {
        if (HardcoreRevivalConfig.getActive().glowOnKnockout) {
            player.setGlowingTag(PlayerHardcoreRevivalManager.isKnockedOut(player));
        }

        Balm.getHooks().setForcedPose(player, PlayerHardcoreRevivalManager.isKnockedOut(player) ? Pose.FALL_FLYING : null);

        KnockoutSyncHandler.sendHardcoreRevivalDataToWatching(player);
    }

    public static void startRescue(Player player, Player target) {
        PlayerHardcoreRevivalManager.setRescueTarget(player, target);
        PlayerHardcoreRevivalManager.setRescueTime(player, 0);
        Balm.getNetworking().sendTo(player, new RevivalProgressMessage(target.getId(), 0.1f));
        KnockoutSyncHandler.sendHardcoreRevivalData(target, target, true);

        Balm.getHooks().setForcedPose(player, Pose.CROUCHING);
    }

    public static boolean isRescuing(Player player) {
        Player rescueTarget = PlayerHardcoreRevivalManager.getRescueTarget(player);
        return rescueTarget != null;
    }

    public static boolean isKnockedOut(Player player) {
        return PlayerHardcoreRevivalManager.isKnockedOut(player);
    }
}
