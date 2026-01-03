package com.sujal.skyblockmaker.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.sujal.skyblockmaker.api.SkyblockEconomyApi;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EconomyCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("sbcoins")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.literal("add")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0))
                .executes(ctx -> {
                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");
                    double amount = DoubleArgumentType.getDouble(ctx, "amount");
                    SkyblockEconomyApi.addCoins(target, amount);
                    ctx.getSource().sendFeedback(() -> Text.literal("Added " + amount + " coins to " + target.getName().getString()), true);
                    return 1;
                }))))
            .then(CommandManager.literal("set")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0))
                .executes(ctx -> {
                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");
                    double amount = DoubleArgumentType.getDouble(ctx, "amount");
                    SkyblockEconomyApi.setCoins(target, amount);
                    ctx.getSource().sendFeedback(() -> Text.literal("Set " + target.getName().getString() + "'s coins to " + amount), true);
                    return 1;
                })))));
    }
}
