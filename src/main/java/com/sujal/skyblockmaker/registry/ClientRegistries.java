package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import com.sujal.skyblockmaker.client.SkyblockHudOverlay;
import com.sujal.skyblockmaker.client.gui.ItemBuilderScreen;
import com.sujal.skyblockmaker.client.gui.ProfileScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ClientRegistries {

    // Flag for Builder only. Profile opens instantly on Main Thread.
    private static boolean shouldOpenBuilder = false;

    public static void registerClientStuff() {
        
        // 1. HUD
        HudRenderCallback.EVENT.register(new SkyblockHudOverlay());

        // 2. Commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            
            // A. Builder
            dispatcher.register(ClientCommandManager.literal("sbbuilder")
                .executes(context -> {
                    shouldOpenBuilder = true; 
                    return 1;
                }));

            // B. Profile (FIXED)
            dispatcher.register(ClientCommandManager.literal("profile")
                .executes(context -> {
                    // Force run on Render Thread to ensure Screen opens
                    MinecraftClient.getInstance().send(() -> {
                        MinecraftClient.getInstance().setScreen(new ProfileScreen(MinecraftClient.getInstance().player));
                    });
                    return 1;
                }));
        });

        // 3. Tick Event (Builder Open Delay)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shouldOpenBuilder) {
                shouldOpenBuilder = false;
                client.setScreen(new ItemBuilderScreen());
            }
        });

        // 4. Tooltips
        registerTooltips();
    }

    private static void registerTooltips() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.hasNbt() && stack.getNbt().contains(SkyblockStatsApi.NBT_KEY)) {
                
                // Colors
                Formatting RED = Formatting.RED;
                Formatting GREEN = Formatting.GREEN;
                Formatting BLUE = Formatting.BLUE;
                Formatting AQUA = Formatting.AQUA;
                Formatting YELLOW = Formatting.YELLOW;

                // Main Stats
                addStat(lines, stack, SkyblockStatsApi.StatType.DAMAGE, "Damage", RED);
                addStat(lines, stack, SkyblockStatsApi.StatType.STRENGTH, "Strength", RED);
                addStat(lines, stack, SkyblockStatsApi.StatType.CRIT_CHANCE, "Crit Chance", "%", BLUE);
                addStat(lines, stack, SkyblockStatsApi.StatType.CRIT_DAMAGE, "Crit Damage", "%", BLUE);
                addStat(lines, stack, SkyblockStatsApi.StatType.ATTACK_SPEED, "Bonus Attack Speed", "%", YELLOW);

                if (SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.HEALTH) > 0 || SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.DEFENSE) > 0) {
                    lines.add(Text.literal(""));
                }
                addStat(lines, stack, SkyblockStatsApi.StatType.HEALTH, "Health", GREEN);
                addStat(lines, stack, SkyblockStatsApi.StatType.DEFENSE, "Defense", GREEN);
                addStat(lines, stack, SkyblockStatsApi.StatType.SPEED, "Speed", Formatting.WHITE);
                addStat(lines, stack, SkyblockStatsApi.StatType.INTELLIGENCE, "Intelligence", AQUA);

                // Lore
                String lore = SkyblockStatsApi.getString(stack, "Lore");
                if (!lore.isEmpty()) {
                    lines.add(Text.literal(""));
                    lines.add(Text.literal(lore).formatted(Formatting.GRAY, Formatting.ITALIC));
                }

                // Ability
                String abName = SkyblockStatsApi.getString(stack, "AbilityName");
                if (!abName.isEmpty()) {
                    lines.add(Text.literal(""));
                    lines.add(Text.literal("Ability: " + abName + "  ").formatted(Formatting.GOLD, Formatting.BOLD)
                            .append(Text.literal("RIGHT CLICK").formatted(Formatting.YELLOW, Formatting.BOLD)));
                    
                    String abDesc = SkyblockStatsApi.getString(stack, "AbilityDesc");
                    lines.add(Text.literal(abDesc).formatted(Formatting.GRAY));
                }

                // Rarity
                String rarity = SkyblockStatsApi.getString(stack, "Rarity");
                if (!rarity.isEmpty()) {
                    lines.add(Text.literal(""));
                    Formatting c = ModPackets.getRarityColor(rarity);
                    lines.add(Text.literal(rarity.toUpperCase() + " ITEM").formatted(c, Formatting.BOLD));
                }
            }
        });
    }

    private static void addStat(java.util.List<Text> lines, net.minecraft.item.ItemStack s, SkyblockStatsApi.StatType type, String name, Formatting color) {
        addStat(lines, s, type, name, "", color);
    }
    private static void addStat(java.util.List<Text> lines, net.minecraft.item.ItemStack s, SkyblockStatsApi.StatType type, String name, String suffix, Formatting color) {
        double val = SkyblockStatsApi.getStat(s, type);
        if (val != 0) {
            String sign = val > 0 ? "+" : "";
            lines.add(Text.literal(name + ": " + sign + (int)val + suffix).formatted(color));
        }
    }
}
