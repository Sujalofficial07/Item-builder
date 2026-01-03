package com.sujal.skyblockmaker.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sujal.skyblockmaker.api.SkyblockProfileApi;
import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import com.sujal.skyblockmaker.api.SkyblockStatHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ProfileCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        
        // /sbadmin set <stat> <value>
        dispatcher.register(CommandManager.literal("sbadmin")
            .requires(source -> source.hasPermissionLevel(2)) // OP Permission
            .then(CommandManager.literal("set")
                .then(CommandManager.argument("stat", StringArgumentType.word())
                .then(CommandManager.argument("value", DoubleArgumentType.doubleArg())
                .executes(context -> {
                    String statName = StringArgumentType.getString(context, "stat");
                    double val = DoubleArgumentType.getDouble(context, "value");
                    ServerPlayerEntity player = context.getSource().getPlayer();

                    if (player == null) return 0;

                    try {
                        SkyblockStatsApi.StatType type = SkyblockStatsApi.StatType.valueOf(statName.toUpperCase());
                        SkyblockProfileApi.setBaseStat(player, type, val);
                        SkyblockStatHandler.updatePlayerStats(player);
                        
                        // === FIX IS HERE: Added () -> before Text.literal ===
                        context.getSource().sendFeedback(() -> Text.literal("Updated Base " + statName + " to " + val), false);
                        
                    } catch (IllegalArgumentException e) {
                        context.getSource().sendError(Text.literal("Invalid Stat Name! Use: HEALTH, STRENGTH, DEFENSE, etc."));
                    }
                    return 1;
                })))));
    }
}
