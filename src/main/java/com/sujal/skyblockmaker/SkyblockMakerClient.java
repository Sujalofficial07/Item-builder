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

    @Override
    public void onInitializeClient() {
        System.out.println("SkyblockMaker Client Loaded (Advanced Features Enabled)...");

        // 1. HUD REGISTER (Health/Mana Bar)
        HudRenderCallback.EVENT.register(new SkyblockHudOverlay());

        // 2. COMMAND REGISTER (/sbbuilder)
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("sbbuilder")
                .executes(context -> {
                    System.out.println("Opening Advanced Builder...");
                    shouldOpenGui = true; 
                    return 1;
                }));
        });

        // 3. TICK EVENT (GUI Safe Open)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shouldOpenGui) {
                shouldOpenGui = false;
                client.setScreen(new ItemBuilderScreen());
            }
        });

        // 4. ADVANCED TOOLTIP RENDERING (Hypixel Style)
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            // Check agar item hamara custom item hai
            if (stack.hasNbt() && stack.getNbt().contains(SkyblockStatsApi.NBT_KEY)) {
                
                // === A. STATS SECTION ===
                
                // 1. Damage & Strength (RED)
                double dmg = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.DAMAGE);
                double str = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.STRENGTH);
                
                if (dmg > 0) lines.add(Text.literal("Damage: +" + (int)dmg).formatted(Formatting.RED));
                if (str > 0) lines.add(Text.literal("Strength: +" + (int)str).formatted(Formatting.RED));

                // 2. Crit Stats (BLUE)
                double cc = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.CRIT_CHANCE);
                double cd = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.CRIT_DAMAGE);
                
                if (cc > 0) lines.add(Text.literal("Crit Chance: " + (int)cc + "%").formatted(Formatting.BLUE));
                if (cd > 0) lines.add(Text.literal("Crit Damage: " + (int)cd + "%").formatted(Formatting.BLUE));

                // 3. Health & Defense (GREEN)
                double hp = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.HEALTH);
                double def = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.DEFENSE);

                if (hp > 0) lines.add(Text.literal("Health: +" + (int)hp).formatted(Formatting.GREEN));
                if (def > 0) lines.add(Text.literal("Defense: +" + (int)def).formatted(Formatting.GREEN));

                // 4. Intelligence (AQUA)
                double intel = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.INTELLIGENCE);
                if (intel > 0) lines.add(Text.literal("Intelligence: +" + (int)intel).formatted(Formatting.AQUA));

                // === B. LORE SECTION (Description) ===
                String lore = stack.getNbt().getCompound(SkyblockStatsApi.NBT_KEY).getString("Lore");
                if (!lore.isEmpty()) {
                    lines.add(Text.literal("")); // Empty Line Space
                    // Lore ko Grey aur Italic mein dikhayenge
                    lines.add(Text.literal(lore).formatted(Formatting.GRAY, Formatting.ITALIC));
                }

                // === C. RARITY SECTION (Bottom) ===
                String rarity = stack.getNbt().getCompound(SkyblockStatsApi.NBT_KEY).getString("Rarity");
                if (!rarity.isEmpty()) {
                    lines.add(Text.literal("")); // Empty Line Space
                    
                    // Rarity Color Logic
                    Formatting color = switch (rarity.toUpperCase()) {
                        case "LEGENDARY" -> Formatting.GOLD;
                        case "MYTHIC" -> Formatting.LIGHT_PURPLE; // Pinkish
                        case "EPIC" -> Formatting.DARK_PURPLE;
                        case "RARE" -> Formatting.BLUE;
                        case "UNCOMMON" -> Formatting.GREEN;
                        case "COMMON" -> Formatting.WHITE;
                        case "SPECIAL" -> Formatting.RED;
                        case "DIVINE" -> Formatting.AQUA;
                        default -> Formatting.GRAY;
                    };
                    
                    // BOLD Rarity Text
                    lines.add(Text.literal(rarity.toUpperCase() + " ITEM").formatted(color, Formatting.BOLD));
                }
            }
        });
    }
}
