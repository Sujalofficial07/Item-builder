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
import java.util.Set;

public class SkyblockMakerClient implements ClientModInitializer {

    private static boolean shouldOpenGui = false;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new SkyblockHudOverlay());

        // Command logic (No Lock for now)
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("sbbuilder").executes(context -> {
                shouldOpenGui = true; 
                return 1;
            }));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shouldOpenGui) {
                shouldOpenGui = false;
                client.setScreen(new ItemBuilderScreen());
            }
        });

        // === PROFESSIONAL TOOLTIP LOGIC ===
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.hasNbt() && stack.getNbt().contains(SkyblockStatsApi.NBT_KEY)) {
                
                // 1. DAMAGE & STRENGTH (Red)
                double dmg = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.DAMAGE);
                double str = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.STRENGTH);
                
                if (dmg > 0) lines.add(Text.literal("Damage: +" + (int)dmg).formatted(Formatting.RED));
                if (str > 0) lines.add(Text.literal("Strength: +" + (int)str).formatted(Formatting.RED));

                // 2. CRIT STATS (Blue)
                double cc = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.CRIT_CHANCE);
                double cd = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.CRIT_DAMAGE);
                
                if (cc > 0) lines.add(Text.literal("Crit Chance: " + (int)cc + "%").formatted(Formatting.BLUE));
                if (cd > 0) lines.add(Text.literal("Crit Damage: " + (int)cd + "%").formatted(Formatting.BLUE));

                // 3. DEFENSE & HEALTH (Green)
                double hp = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.HEALTH);
                double def = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.DEFENSE);

                if (hp > 0) lines.add(Text.literal("Health: +" + (int)hp).formatted(Formatting.GREEN));
                if (def > 0) lines.add(Text.literal("Defense: +" + (int)def).formatted(Formatting.GREEN));

                // 4. INTELLIGENCE (Aqua)
                double intel = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.INTELLIGENCE);
                if (intel > 0) lines.add(Text.literal("Intelligence: +" + (int)intel).formatted(Formatting.AQUA));

                // 5. RARITY (Bold at Bottom)
                String rarity = stack.getNbt().getCompound(SkyblockStatsApi.NBT_KEY).getString("Rarity");
                if (!rarity.isEmpty()) {
                    lines.add(Text.literal("")); // Empty Line
                    
                    Formatting color = switch (rarity.toUpperCase()) {
                        case "LEGENDARY" -> Formatting.GOLD;
                        case "RARE" -> Formatting.BLUE;
                        case "EPIC" -> Formatting.DARK_PURPLE;
                        case "UNCOMMON" -> Formatting.GREEN;
                        case "MYTHIC" -> Formatting.LIGHT_PURPLE;
                        default -> Formatting.WHITE;
                    };
                    
                    lines.add(Text.literal(rarity.toUpperCase() + " ITEM").formatted(color, Formatting.BOLD));
                }
            }
        });
    }
}
