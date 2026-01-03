package com.sujal.skyblockmaker;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import com.sujal.skyblockmaker.client.SkyblockHudOverlay;
import com.sujal.skyblockmaker.client.gui.ItemBuilderScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SkyblockMakerClient implements ClientModInitializer {

    private static boolean shouldOpenGui = false;
    private static final String REQUIRED_TAG = "sb_admin";

    @Override
    public void onInitializeClient() {
        System.out.println("SkyblockMaker Client & HUD Loading...");

        // 1. HUD Register
        HudRenderCallback.EVENT.register(new SkyblockHudOverlay());

        // 2. Command Register
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("sbbuilder")
                .executes(context -> {
                    
                    // === FIX IS HERE (getScoreboardTags -> getCommandTags) ===
                    if (!context.getSource().getPlayer().getCommandTags().contains(REQUIRED_TAG)) {
                        context.getSource().sendError(Text.literal("❌ Access Denied: Missing '" + REQUIRED_TAG + "' tag."));
                        return 0;
                    }

                    // Schedule GUI
                    System.out.println("Access Granted! Scheduling GUI...");
                    context.getSource().sendFeedback(Text.literal("Opening Builder..."));
                    shouldOpenGui = true; 
                    return 1;
                }));
        });

        // 3. Tick Event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shouldOpenGui) {
                shouldOpenGui = false;
                client.setScreen(new ItemBuilderScreen());
            }
        });

        // 4. Tooltips
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.hasNbt() && stack.getNbt().contains(SkyblockStatsApi.NBT_KEY)) {
                
                double strength = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.STRENGTH);
                if (strength != 0) {
                    lines.add(Text.literal("Strength: +" + (int)strength).formatted(Formatting.RED));
                }
                
                double defense = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.DEFENSE);
                if (defense != 0) {
                    lines.add(Text.literal("Defense: +" + (int)defense).formatted(Formatting.GREEN));
                }

                String rarity = stack.getNbt().getCompound(SkyblockStatsApi.NBT_KEY).getString("Rarity");
                if (!rarity.isEmpty()) {
                    lines.add(Text.literal(""));
                    lines.add(Text.literal(rarity.toUpperCase()).formatted(Formatting.GOLD, Formatting.BOLD));
                }
            }
        });
    }
}
