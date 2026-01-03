package com.sujal.skyblockmaker;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import com.sujal.skyblockmaker.client.SkyblockHudOverlay;
import com.sujal.skyblockmaker.client.gui.ItemBuilderScreen;
import com.sujal.skyblockmaker.client.gui.ProfileScreen; // New Import
import com.sujal.skyblockmaker.registry.ModPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SkyblockMakerClient implements ClientModInitializer {

    private static boolean shouldOpenBuilder = false;

    @Override
    public void onInitializeClient() {
        System.out.println("SkyblockMaker Client Loaded (Profile System Enabled)...");

        // 1. HUD REGISTER (Health/Mana Bar)
        HudRenderCallback.EVENT.register(new SkyblockHudOverlay());

        // 2. COMMANDS REGISTER
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            
            // A. /sbbuilder (Item Creator)
            dispatcher.register(ClientCommandManager.literal("sbbuilder")
                .executes(context -> {
                    shouldOpenBuilder = true; 
                    return 1;
                }));

            // B. /profile (Profile Stats GUI) - NEW!
            dispatcher.register(ClientCommandManager.literal("profile")
                .executes(context -> {
                    // GUI thread safe open logic
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.execute(() -> {
                        client.setScreen(new ProfileScreen(client.player));
                    });
                    return 1;
                }));
        });

        // 3. TICK EVENT (Safe Open for Builder)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shouldOpenBuilder) {
                shouldOpenBuilder = false;
                client.setScreen(new ItemBuilderScreen());
            }
        });

        // 4. ADVANCED TOOLTIP RENDERER (Hypixel Style)
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.hasNbt() && stack.getNbt().contains(SkyblockStatsApi.NBT_KEY)) {
                
                // --- Main Stats ---
                addStat(lines, stack, SkyblockStatsApi.StatType.DAMAGE, "Damage", Formatting.RED);
                addStat(lines, stack, SkyblockStatsApi.StatType.STRENGTH, "Strength", Formatting.RED);
                addStat(lines, stack, SkyblockStatsApi.StatType.CRIT_CHANCE, "Crit Chance", "%", Formatting.BLUE);
                addStat(lines, stack, SkyblockStatsApi.StatType.CRIT_DAMAGE, "Crit Damage", "%", Formatting.BLUE);
                addStat(lines, stack, SkyblockStatsApi.StatType.ATTACK_SPEED, "Bonus Attack Speed", "%", Formatting.YELLOW);

                // --- Defensive Stats ---
                if (SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.HEALTH) > 0 || SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.DEFENSE) > 0) {
                    lines.add(Text.literal(""));
                }
                addStat(lines, stack, SkyblockStatsApi.StatType.HEALTH, "Health", Formatting.GREEN);
                addStat(lines, stack, SkyblockStatsApi.StatType.DEFENSE, "Defense", Formatting.GREEN);
                addStat(lines, stack, SkyblockStatsApi.StatType.SPEED, "Speed", Formatting.WHITE);
                addStat(lines, stack, SkyblockStatsApi.StatType.INTELLIGENCE, "Intelligence", Formatting.AQUA);
                addStat(lines, stack, SkyblockStatsApi.StatType.MAGIC_FIND, "Magic Find", Formatting.AQUA);

                // --- Lore ---
                String lore = SkyblockStatsApi.getString(stack, "Lore");
                if (!lore.isEmpty()) {
                    lines.add(Text.literal(""));
                    lines.add(Text.literal(lore).formatted(Formatting.GRAY, Formatting.ITALIC));
                }

                // --- Abilities ---
                String abName = SkyblockStatsApi.getString(stack, "AbilityName");
                if (!abName.isEmpty()) {
                    lines.add(Text.literal(""));
                    lines.add(Text.literal("Ability: " + abName + "  ").formatted(Formatting.GOLD, Formatting.BOLD)
                            .append(Text.literal("RIGHT CLICK").formatted(Formatting.YELLOW, Formatting.BOLD)));
                    
                    String abDesc = SkyblockStatsApi.getString(stack, "AbilityDesc");
                    lines.add(Text.literal(abDesc).formatted(Formatting.GRAY));
                }

                // --- Rarity ---
                String rarity = SkyblockStatsApi.getString(stack, "Rarity");
                if (!rarity.isEmpty()) {
                    lines.add(Text.literal(""));
                    Formatting c = ModPackets.getRarityColor(rarity);
                    lines.add(Text.literal(rarity.toUpperCase() + " ITEM").formatted(c, Formatting.BOLD));
                }
            }
        });
    }

    // Helper for Tooltips
    private void addStat(java.util.List<Text> lines, net.minecraft.item.ItemStack s, SkyblockStatsApi.StatType type, String name, Formatting color) {
        addStat(lines, s, type, name, "", color);
    }
    private void addStat(java.util.List<Text> lines, net.minecraft.item.ItemStack s, SkyblockStatsApi.StatType type, String name, String suffix, Formatting color) {
        double val = SkyblockStatsApi.getStat(s, type);
        if (val != 0) {
            String sign = val > 0 ? "+" : "";
            lines.add(Text.literal(name + ": " + sign + (int)val + suffix).formatted(color));
        }
    }
}
