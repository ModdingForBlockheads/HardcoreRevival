package net.blay09.mods.hardcorerevival.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.blay09.mods.hardcorerevival.HardcoreRevivalManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class ReviveCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("revive")
                .requires((source) -> source.hasPermission(2))
                .executes((source) -> reviveEntities(source.getSource(), ImmutableList.of(source.getSource().getEntityOrException()), false))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .executes((source) -> reviveEntities(source.getSource(), EntityArgument.getEntities(source, "targets"), false))
                        .then(Commands.argument("skipEffects", BoolArgumentType.bool())
                                .executes((source) -> reviveEntities(source.getSource(), EntityArgument.getEntities(source, "targets"), BoolArgumentType.getBool(source, "skipEffects"))))));
    }

    private static int reviveEntities(CommandSourceStack source, Collection<? extends Entity> targets, boolean skipEffects) {
        for (Entity entity : targets) {
            if (entity instanceof Player player) {
                HardcoreRevivalManager.wakeup(player, !skipEffects);
            }
        }

        if (targets.size() == 1) {
            source.sendSuccess(() -> Component.translatable("commands.revive.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.revive.success.multiple", targets.size()), true);
        }

        return targets.size();
    }
}
