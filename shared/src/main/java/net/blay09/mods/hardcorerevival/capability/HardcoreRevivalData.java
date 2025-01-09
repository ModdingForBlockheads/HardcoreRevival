package net.blay09.mods.hardcorerevival.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface HardcoreRevivalData {
    void setKnockedOut(boolean knockedOut);

    boolean isKnockedOut();

    void setKnockoutTicksPassed(int knockoutTicksPassed);

    int getKnockoutTicksPassed();

    void setLogoutWorldTime(long logoutWorldTime);

    long getLogoutWorldTime();

    void setRescueTime(int rescueTime);

    int getRescueTime();

    void setRescueTarget(Player rescueTarget);

    Player getRescueTarget();

    CompoundTag serialize();

    void deserialize(CompoundTag tag);

    void setKnockoutStartTime(long knockoutStartTime);

    long getKnockoutStartTime();
}
