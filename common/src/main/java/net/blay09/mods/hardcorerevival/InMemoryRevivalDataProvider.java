package net.blay09.mods.hardcorerevival;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryRevivalDataProvider implements RevivalDataProvider {

    private final Map<UUID, InMemoryPlayerRevivalData> playerData = new HashMap<>();

    private InMemoryPlayerRevivalData getPlayerData(Player player) {
        return playerData.computeIfAbsent(player.getUUID(), uuid -> new InMemoryPlayerRevivalData());
    }

    @Override
    public void setKnockedOut(Player player, boolean knockedOut) {
        getPlayerData(player).setKnockedOut(knockedOut);
    }

    @Override
    public boolean isKnockedOut(Player player) {
        return getPlayerData(player).isKnockedOut();
    }

    @Override
    public void setKnockoutTicksPassed(Player player, int knockoutTicksPassed) {
        getPlayerData(player).setKnockoutTicksPassed(knockoutTicksPassed);
    }

    @Override
    public int getKnockoutTicksPassed(Player player) {
        return getPlayerData(player).getKnockoutTicksPassed();
    }

    @Override
    public void setLastKnockoutTicksPassed(Player player, int lastKnockoutTicksPassed) {
        getPlayerData(player).setLastKnockoutTicksPassed(lastKnockoutTicksPassed);
    }

    @Override
    public int getLastKnockoutTicksPassed(Player player) {
        return getPlayerData(player).getLastKnockoutTicksPassed();
    }

    @Override
    public void setLastRescuedAt(Player player, long lastRescuedAt) {
        getPlayerData(player).setLastRescuedAt(lastRescuedAt);
    }

    @Override
    public long getLastRescuedAt(Player player) {
        return getPlayerData(player).getLastRescuedAt();
    }

    @Override
    public void setLastKnockoutAt(Player player, long lastKnockoutAt) {
        getPlayerData(player).setLastKnockoutAt(lastKnockoutAt);
    }

    @Override
    public long getLastKnockoutAt(Player player) {
        return getPlayerData(player).getLastKnockoutAt();
    }

    @Override
    public void setLastLogoutAt(Player player, long lastLogoutAt) {
        getPlayerData(player).setLastLogoutAt(lastLogoutAt);
    }

    @Override
    public long getLastLogoutAt(Player player) {
        return getPlayerData(player).getLastLogoutAt();
    }

    @Override
    public void setRescueTime(Player player, int rescueTime) {
        getPlayerData(player).setRescueTime(rescueTime);
    }

    @Override
    public int getRescueTime(Player player) {
        return getPlayerData(player).getRescueTime();
    }

    @Override
    public void setRescueTarget(Player player, Player rescueTarget) {
        getPlayerData(player).setRescueTarget(rescueTarget);
    }

    @Override
    public Player getRescueTarget(Player player) {
        return getPlayerData(player).getRescueTarget();
    }}
