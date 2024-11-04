package net.blay09.mods.hardcorerevival;

import net.blay09.mods.balm.api.Balm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class PersistentRevivalDataProvider implements RevivalDataProvider {

    private static final String TAG_NAME = "HardcoreRevivalData";
    private static final String KNOCKED_OUT = "KnockedOut";
    private static final String KNOCKOUT_TICKS_PASSED = "KnockoutTicksPassed";
    private static final String LAST_KNOCKOUT_TICKS_PASSED = "LastKnockoutTicksPassed";
    private static final String LAST_RESCUED_AT = "LastRescuedAt";
    private static final String LAST_KNOCKOUT_AT = "LastKnockoutAt";
    private static final String LAST_LOGOUT_AT = "LastLogoutAt";
    private static final String RESCUE_TARGET = "RescueTarget";

    private static CompoundTag getRevivalData(Player player) {
        CompoundTag persistedData = Balm.getHooks().getPersistentData(player);
        CompoundTag compound = persistedData.getCompound(TAG_NAME);
        persistedData.put(TAG_NAME, compound);
        return compound;
    }

    @Override
    public void setKnockedOut(Player player, boolean knockedOut) {
        getRevivalData(player).putBoolean(KNOCKED_OUT, knockedOut);
    }

    @Override
    public boolean isKnockedOut(Player player) {
        return getRevivalData(player).getBoolean(KNOCKED_OUT);
    }

    @Override
    public void setKnockoutTicksPassed(Player player, int knockoutTicksPassed) {
        getRevivalData(player).putInt(KNOCKOUT_TICKS_PASSED, knockoutTicksPassed);
    }

    @Override
    public int getKnockoutTicksPassed(Player player) {
        return getRevivalData(player).getInt(KNOCKOUT_TICKS_PASSED);
    }

    @Override
    public void setLastKnockoutTicksPassed(Player player, int lastKnockoutTicksPassed) {
        getRevivalData(player).putInt(LAST_KNOCKOUT_TICKS_PASSED, lastKnockoutTicksPassed);
    }

    @Override
    public int getLastKnockoutTicksPassed(Player player) {
        return getRevivalData(player).getInt(LAST_KNOCKOUT_TICKS_PASSED);
    }

    @Override
    public void setLastRescuedAt(Player player, long lastRescuedAt) {
        getRevivalData(player).putLong(LAST_RESCUED_AT, lastRescuedAt);
    }

    @Override
    public long getLastRescuedAt(Player player) {
        return getRevivalData(player).getLong(LAST_RESCUED_AT);
    }

    @Override
    public void setLastKnockoutAt(Player player, long lastKnockoutAt) {
        getRevivalData(player).putLong(LAST_KNOCKOUT_AT, lastKnockoutAt);
    }

    @Override
    public long getLastKnockoutAt(Player player) {
        return getRevivalData(player).getLong(LAST_KNOCKOUT_AT);
    }

    @Override
    public void setLastLogoutAt(Player player, long lastLogoutAt) {
        getRevivalData(player).putLong(LAST_LOGOUT_AT, lastLogoutAt);
    }

    @Override
    public long getLastLogoutAt(Player player) {
        return getRevivalData(player).getLong(LAST_LOGOUT_AT);
    }

    @Override
    public void setRescueTime(Player player, int rescueTime) {
        getRevivalData(player).putInt(LAST_RESCUED_AT, rescueTime);
    }

    @Override
    public int getRescueTime(Player player) {
        return getRevivalData(player).getInt(LAST_RESCUED_AT);
    }

    @Override
    public void setRescueTarget(Player player, @Nullable Player rescueTarget) {
        if (rescueTarget != null) {
            getRevivalData(player).putUUID(RESCUE_TARGET, rescueTarget.getGameProfile().getId());
        } else {
            getRevivalData(player).remove(RESCUE_TARGET);
        }
    }

    @Override
    public Player getRescueTarget(Player player) {
        final var server = player.level().getServer();
        if (server != null) {
            final var tag = getRevivalData(player);
            return tag.contains(RESCUE_TARGET) ? server.getPlayerList().getPlayer(tag.getUUID(RESCUE_TARGET)) : null;
        }
        return null;
    }
}
