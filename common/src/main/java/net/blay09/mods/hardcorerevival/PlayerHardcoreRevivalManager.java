package net.blay09.mods.hardcorerevival;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class PlayerHardcoreRevivalManager {

    private static final RevivalDataProvider persistentDataProvider = new PersistentRevivalDataProvider();
    private static final RevivalDataProvider inMemoryDataProvider = new InMemoryRevivalDataProvider();

    public static RevivalDataProvider getRevivalDataProvider(@Nullable Level world) {
        return world == null || world.isClientSide ? inMemoryDataProvider : persistentDataProvider;
    }

    public static void setKnockedOut(Player player, boolean knockedOut) {
        getRevivalDataProvider(player.level()).setKnockedOut(player, knockedOut);
    }

    public static boolean isKnockedOut(Player player) {
        return getRevivalDataProvider(player.level()).isKnockedOut(player);
    }

    public static void setKnockoutTicksPassed(Player player, int knockoutTicksPassed) {
        getRevivalDataProvider(player.level()).setKnockoutTicksPassed(player, knockoutTicksPassed);
    }

    public static int getKnockoutTicksPassed(Player player) {
        return getRevivalDataProvider(player.level()).getKnockoutTicksPassed(player);
    }

    public static void setLastKnockoutTicksPassed(Player player, int lastKnockoutTicksPassed) {
        getRevivalDataProvider(player.level()).setLastKnockoutTicksPassed(player, lastKnockoutTicksPassed);
    }

    public static int getLastKnockoutTicksPassed(Player player) {
        return getRevivalDataProvider(player.level()).getLastKnockoutTicksPassed(player);
    }

    public static void setLastRescuedAt(Player player, long lastRescuedAt) {
        getRevivalDataProvider(player.level()).setLastRescuedAt(player, lastRescuedAt);
    }

    public static long getLastRescuedAt(Player player) {
        return getRevivalDataProvider(player.level()).getLastRescuedAt(player);
    }

    public static void setLastKnockoutAt(Player player, long lastKnockoutAt) {
        getRevivalDataProvider(player.level()).setLastKnockoutAt(player, lastKnockoutAt);
    }

    public static long getLastKnockoutAt(Player player) {
        return getRevivalDataProvider(player.level()).getLastKnockoutAt(player);
    }

    public static void setLastLogoutAt(Player player, long lastLogoutAt) {
        getRevivalDataProvider(player.level()).setLastLogoutAt(player, lastLogoutAt);
    }

    public static long getLastLogoutAt(Player player) {
        return getRevivalDataProvider(player.level()).getLastLogoutAt(player);
    }

    public static void setRescueTime(Player player, int rescueTime) {
        getRevivalDataProvider(player.level()).setRescueTime(player, rescueTime);
    }

    public static int getRescueTime(Player player) {
        return getRevivalDataProvider(player.level()).getRescueTime(player);
    }

    public static void setRescueTarget(Player player, @Nullable Player rescueTarget) {
        getRevivalDataProvider(player.level()).setRescueTarget(player, rescueTarget);
    }

    @Nullable
    public static Player getRescueTarget(Player player) {
        return getRevivalDataProvider(player.level()).getRescueTarget(player);
    }
}