package net.blay09.mods.hardcorerevival;

import net.blay09.mods.hardcorerevival.api.InternalMethods;
import net.blay09.mods.hardcorerevival.config.HardcoreRevivalConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

public class InternalMethodsImpl implements InternalMethods {
    @Override
    public void knockout(Player player, DamageSource damageSource) {
        HardcoreRevivalManager.knockout(player, damageSource);
    }

    @Override
    public void wakeup(Player player, boolean applyEffects) {
        HardcoreRevivalManager.wakeup(player, applyEffects);
    }

    @Override
    public boolean isKnockedOut(Player player) {
        return PlayerHardcoreRevivalManager.isKnockedOut(player);
    }

    @Override
    public int getKnockoutTicksPassed(Player player) {
        return PlayerHardcoreRevivalManager.getKnockoutTicksPassed(player);
    }

    @Override
    public int getKnockoutTicksLeft(Player player) {
        final var maxTicksUntilDeath = HardcoreRevivalConfig.getActive().secondsUntilDeath * 20;
        return Math.max(0, maxTicksUntilDeath - getKnockoutTicksPassed(player));
    }
}
