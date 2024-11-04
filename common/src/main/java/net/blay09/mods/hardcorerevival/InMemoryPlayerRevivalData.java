package net.blay09.mods.hardcorerevival;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

class InMemoryPlayerRevivalData {
    private boolean knockedOut;
    private int knockoutTicksPassed;
    private int lastKnockoutTicksPassed;
    private long lastRescuedAt;
    private long lastKnockoutAt;
    private long lastLogoutAt;
    private int rescueTime;
    private Player rescueTarget;

    public boolean isKnockedOut() {
        return knockedOut;
    }

    public void setKnockedOut(boolean knockedOut) {
        this.knockedOut = knockedOut;
    }

    public int getKnockoutTicksPassed() {
        return knockoutTicksPassed;
    }

    public void setKnockoutTicksPassed(int knockoutTicksPassed) {
        this.knockoutTicksPassed = knockoutTicksPassed;
    }

    public int getLastKnockoutTicksPassed() {
        return lastKnockoutTicksPassed;
    }

    public void setLastKnockoutTicksPassed(int lastKnockoutTicksPassed) {
        this.lastKnockoutTicksPassed = lastKnockoutTicksPassed;
    }

    public long getLastRescuedAt() {
        return lastRescuedAt;
    }

    public void setLastRescuedAt(long lastRescuedAt) {
        this.lastRescuedAt = lastRescuedAt;
    }

    public long getLastKnockoutAt() {
        return lastKnockoutAt;
    }

    public void setLastKnockoutAt(long lastKnockoutAt) {
        this.lastKnockoutAt = lastKnockoutAt;
    }

    public long getLastLogoutAt() {
        return lastLogoutAt;
    }

    public void setLastLogoutAt(long lastLogoutAt) {
        this.lastLogoutAt = lastLogoutAt;
    }

    public int getRescueTime() {
        return rescueTime;
    }

    public void setRescueTime(int rescueTime) {
        this.rescueTime = rescueTime;
    }

    @Nullable
    public Player getRescueTarget() {
        return rescueTarget;
    }

    public void setRescueTarget(@Nullable Player rescueTarget) {
        this.rescueTarget = rescueTarget;
    }
}
