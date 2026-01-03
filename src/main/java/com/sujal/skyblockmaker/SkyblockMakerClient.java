package com.sujal.skyblockmaker;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import com.sujal.skyblockmaker.client.SkyblockHudOverlay; // HUD Import
import com.sujal.skyblockmaker.client.gui.ItemBuilderScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback; // HUD Event Import
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SkyblockMakerClient implements ClientModInitializer {

    private static boolean shouldOpenGui = false;
    private static final String REQUIRED_TAG = "sb_admin"; // Lock System Tag

    @Override
    public void onInitializeClient() {
        System.out.println("SkyblockMaker Client & HUD Loading...");

        // === 1. HUD REGISTER KARNA (Ye naya hai) ===
        HudRenderCallback.EVENT.register(new SkyblockHudOverlay());

        // === 2. COMMAND REGISTER (/sbbuilder) ===
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("sbbuilder")
                .executes(context -> {
                    
                    // Security Check: Kya player ke paas tag hai?
                    if (!context.getSource().getPlayer().getScoreboardTags().contains(REQUIRED_TAG)) {
                        context.getSource().sendError(Text.literal("❌ Access Denied: Missing '" + REQUIRED_TAG + "' tag."));
                        return 0;
                    }

                    // GUI Schedule karna
                    System.out.println("Access Granted! Scheduling GUI...");
                    context.getSource().sendFeedback(Text.literal("Opening Builder..."));
                    shouldOpenGui = true; 
                    return 1;
                }));
        });

        // === 3. TICK EVENT (Safe GUI Opening) ===
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shouldOpenGui) {
                shouldOpenGui = false;
                client.setScreen(new ItemBuilderScreen());
            }
        });

        // === 4. TOOLTIPS (Hover karne par stats dikhana) ===
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.hasNbt() && stack.getNbt().contains(SkyblockStatsApi.NBT_KEY)) {
                
                // Strength
                double strength = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.STRENGTH);
                if (strength != 0) {
                    lines.add(Text.literal("Strength: +" + (int)strength).formatted(Formatting.RED));
                }
                
                // Defense (Tooltip mein bhi dikhana chahiye)
                double defense = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.DEFENSE);
                if (defense != 0) {
                    lines.add(Text.literal("Defense: +" + (int)defense).formatted(Formatting.GREEN));
                }

                // Rarity
                String rarity = stack.getNbt().getCompound(SkyblockStatsApi.NBT_KEY).getString("Rarity");
                if (!rarity.isEmpty()) {
                    lines.add(Text.literal(""));
                    lines.add(Text.literal(rarity.toUpperCase()).formatted(Formatting.GOLD, Formatting.BOLD));
                }
            }
        });
    }
}
