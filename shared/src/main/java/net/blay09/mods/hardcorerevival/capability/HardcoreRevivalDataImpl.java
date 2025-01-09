package net.blay09.mods.hardcorerevival.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class HardcoreRevivalDataImpl implements HardcoreRevivalData {
	private static final String KNOCKED_OUT = "KnockedOut";
	private static final String KNOCKOUT_TICKS_PASSED = "KnockoutTicksPassed";
	private static final String LOGOUT_WORLD_TIME = "LogoutWorldTime";
	private static final String KNOCKOUT_START_TIME = "KnockoutStartTime";

	private boolean knockedOut;
	private int knockoutTicksPassed;
	private long logoutWorldTime;
	private int rescueTime;
	private Player rescueTarget;
	private long knockoutStartTime;

	@Override
	public void setKnockedOut(boolean knockedOut) {
		this.knockedOut = knockedOut;
	}

	@Override
	public boolean isKnockedOut() {
		return knockedOut;
	}

	@Override
	public void setKnockoutTicksPassed(int knockoutTicksPassed) {
		this.knockoutTicksPassed = knockoutTicksPassed;
	}

	@Override
	public int getKnockoutTicksPassed() {
		return knockoutTicksPassed;
	}

	@Override
	public void setLogoutWorldTime(long logoutWorldTime) {
		this.logoutWorldTime = logoutWorldTime;
	}

	@Override
	public long getLogoutWorldTime() {
		return logoutWorldTime;
	}

	@Override
	public void setRescueTime(int rescueTime) {
		this.rescueTime = rescueTime;
	}

	@Override
	public int getRescueTime() {
		return rescueTime;
	}

	@Override
	public void setRescueTarget(Player rescueTarget) {
		this.rescueTarget = rescueTarget;
	}

	@Override
	public Player getRescueTarget() {
		return rescueTarget;
	}

	@Override
	public void setKnockoutStartTime(long knockoutStartTime) {
		this.knockoutStartTime = knockoutStartTime;
	}

	@Override
	public long getKnockoutStartTime() {
		return knockoutStartTime;
	}

	@Override
	public CompoundTag serialize() {
		CompoundTag tagCompound = new CompoundTag();
		tagCompound.putBoolean(KNOCKED_OUT, isKnockedOut());
		tagCompound.putInt(KNOCKOUT_TICKS_PASSED, getKnockoutTicksPassed());
		tagCompound.putLong(LOGOUT_WORLD_TIME, getLogoutWorldTime());
		tagCompound.putLong(KNOCKOUT_START_TIME, getKnockoutStartTime());
		return tagCompound;
	}

	@Override
	public void deserialize(CompoundTag tag) {
		setKnockedOut(tag.getBoolean(KNOCKED_OUT));
		setKnockoutTicksPassed(tag.getInt(KNOCKOUT_TICKS_PASSED));
		setLogoutWorldTime(tag.getLong(LOGOUT_WORLD_TIME));
		setKnockoutStartTime(tag.getLong(KNOCKOUT_START_TIME));
	}
}
