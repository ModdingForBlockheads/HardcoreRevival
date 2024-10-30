package net.blay09.mods.hardcorerevival;

import net.blay09.mods.hardcorerevival.api.InternalMethods;
import net.blay09.mods.hardcorerevival.config.HardcoreRevivalConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class InternalMethodsImpl implements InternalMethods {
    @Override
    public void knockout(ServerPlayer player, DamageSource damageSource) {
        HardcoreRevivalManager.knockout(player, damageSource);
    }

    @Override
    public void wakeup(ServerPlayer player, boolean applyEffects) {
        HardcoreRevivalManager.wakeup(player, applyEffects);
    }

    @Override
    public boolean isKnockedOut(ServerPlayer player) {
        return PlayerHardcoreRevivalManager.isKnockedOut(player);
    }

    @Override
    public int getKnockoutTicksPassed(ServerPlayer player) {
        return PlayerHardcoreRevivalManager.getKnockoutTicksPassed(player);
    }

    @Override
    public int getKnockoutTicksLeft(ServerPlayer player) {
        final var maxTicksUntilDeath = HardcoreRevivalConfig.getActive().secondsUntilDeath * 20;
        return Math.max(0, maxTicksUntilDeath - getKnockoutTicksPassed(player));
    }
}
