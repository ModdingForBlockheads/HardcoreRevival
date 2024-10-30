package net.blay09.mods.hardcorerevival;

import net.minecraft.world.entity.player.Player;

public interface RevivalDataProvider {
    void setKnockedOut(Player player, boolean knockedOut);

    boolean isKnockedOut(Player player);

    void setKnockoutTicksPassed(Player player, int knockoutTicksPassed);

    int getKnockoutTicksPassed(Player player);

    void setLastKnockoutTicksPassed(Player player, int lastKnockoutTicksPassed);

    int getLastKnockoutTicksPassed(Player player);

    void setLastRescuedAt(Player player, long lastRescuedAt);

    long getLastRescuedAt(Player player);

    void setLastKnockoutAt(Player player, long lastKnockoutAt);

    long getLastKnockoutAt(Player player);

    void setLastLogoutAt(Player player, long lastLogoutAt);

    long getLastLogoutAt(Player player);

    void setRescueTime(Player player, int rescueTime);

    int getRescueTime(Player player);

    void setRescueTarget(Player player, Player rescueTarget);

    Player getRescueTarget(Player player);
}
