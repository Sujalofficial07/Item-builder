package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import com.sujal.skyblockmaker.client.SkyblockHudOverlay;
import com.sujal.skyblockmaker.client.gui.ItemSelectorScreen; 
import com.sujal.skyblockmaker.client.gui.ProfileScreen;
import com.sujal.skyblockmaker.client.gui.SkillsScreen;
import com.sujal.skyblockmaker.util.IEntityDataSaver;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ClientRegistries {

    private static boolean shouldOpenBuilder = false;

    public static void registerClientStuff() {
        
        HudRenderCallback.EVENT.register(new SkyblockHudOverlay());

        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SKILL_SYNC_PACKET, (client, handler, buf, responseSender) -> {
            NbtCompound skillsData = buf.readNbt();
            client.execute(() -> {
                if (client.player != null) {
                    ((IEntityDataSaver) client.player).getPersistentData().put("SB_Skills", skillsData);
                }
            });
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            
            // Open Selector (Chest GUI)
            dispatcher.register(ClientCommandManager.literal("sbbuilder")
                .executes(context -> {
                    shouldOpenBuilder = true; 
                    return 1;
                }));

            dispatcher.register(ClientCommandManager.literal("profile")
                .executes(context -> {
                    MinecraftClient.getInstance().send(() -> {
                        MinecraftClient.getInstance().setScreen(new ProfileScreen(MinecraftClient.getInstance().player));
                    });
                    return 1;
                }));

            dispatcher.register(ClientCommandManager.literal("skills")
                .executes(context -> {
                    MinecraftClient.getInstance().send(() -> {
                        MinecraftClient.getInstance().setScreen(new SkillsScreen());
                    });
                    return 1;
                }));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shouldOpenBuilder) {
                shouldOpenBuilder = false;
                client.setScreen(new ItemSelectorScreen()); 
            }
        });

        registerTooltips();
    }

    private static void registerTooltips() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.hasNbt() && stack.getNbt().contains(SkyblockStatsApi.NBT_KEY)) {
                
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

                // Defensive / Misc
                if (SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.HEALTH) > 0 || SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.DEFENSE) > 0) {
                    lines.add(Text.literal(""));
                }
                addStat(lines, stack, SkyblockStatsApi.StatType.HEALTH, "Health", GREEN);
                addStat(lines, stack, SkyblockStatsApi.StatType.DEFENSE, "Defense", GREEN);
                addStat(lines, stack, SkyblockStatsApi.StatType.SPEED, "Speed", Formatting.WHITE);
                addStat(lines, stack, SkyblockStatsApi.StatType.INTELLIGENCE, "Intelligence", AQUA);
                addStat(lines, stack, SkyblockStatsApi.StatType.MAGIC_FIND, "Magic Find", AQUA);
                addStat(lines, stack, SkyblockStatsApi.StatType.FEROCITY, "Ferocity", RED);

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
                    lines.add(Text.literal("Item Ability: " + abName + "  ").formatted(Formatting.GOLD, Formatting.BOLD)
                            .append(Text.literal("RIGHT CLICK").formatted(Formatting.YELLOW, Formatting.BOLD)));
                    
                    String abDesc = SkyblockStatsApi.getString(stack, "AbilityDesc");
                    lines.add(Text.literal(abDesc).formatted(Formatting.GRAY));
                    
                    double cost = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.MANA_COST);
                    if(cost > 0) {
                        lines.add(Text.literal("Mana Cost: " + (int)cost).formatted(Formatting.DARK_GRAY));
                    }
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
