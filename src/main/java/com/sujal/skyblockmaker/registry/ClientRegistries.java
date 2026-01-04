package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import com.sujal.skyblockmaker.client.SkyblockHudOverlay;
import com.sujal.skyblockmaker.client.gui.CategorySelectionScreen; // New Entry Screen
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
        
        // 1. LOAD CUSTOM ITEMS (Hyperion, Terminator, etc.)
        ModItems.registerItems();

        // 2. HUD
        HudRenderCallback.EVENT.register(new SkyblockHudOverlay());

        // 3. SYNC PACKET (XP & Level Sync)
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SKILL_SYNC_PACKET, (client, handler, buf, responseSender) -> {
            NbtCompound skillsData = buf.readNbt();
            client.execute(() -> {
                if (client.player != null) {
                    ((IEntityDataSaver) client.player).getPersistentData().put("SB_Skills", skillsData);
                }
            });
        });

        // 4. COMMANDS
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            
            // /sbbuilder -> Opens Category Menu
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

        // 5. TICK EVENT (Safe GUI Opening)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shouldOpenBuilder) {
                shouldOpenBuilder = false;
                // Opens Step 1: Select Category (Weapon/Armor/Material)
                client.setScreen(new CategorySelectionScreen()); 
            }
        });

        // 6. TOOLTIPS (Hypixel Style)
        registerTooltips();
    }

    private static void registerTooltips() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.hasNbt() && stack.getNbt().contains(SkyblockStatsApi.NBT_KEY)) {
                
                // Colors & Symbols
                Formatting RED = Formatting.RED;
                Formatting GREEN = Formatting.GREEN;
                Formatting BLUE = Formatting.BLUE;
                Formatting AQUA = Formatting.AQUA;
                Formatting YELLOW = Formatting.YELLOW;
                Formatting GRAY = Formatting.GRAY;
                Formatting LIGHT_PURPLE = Formatting.LIGHT_PURPLE;

                // Clear vanilla tooltips (keep name)
                if(lines.size() > 0) {
                    Text name = lines.get(0);
                    lines.clear();
                    lines.add(name);
                }

                // --- HEADER ---
                // Gear Score
                double gs = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.GEAR_SCORE);
                if(gs > 0) lines.add(Text.literal("Gear Score: " + (int)gs).formatted(LIGHT_PURPLE));

                // --- MAIN STATS ---
                addStat(lines, stack, SkyblockStatsApi.StatType.DAMAGE, "Damage", RED);
                addStat(lines, stack, SkyblockStatsApi.StatType.STRENGTH, "Strength", RED);
                addStat(lines, stack, SkyblockStatsApi.StatType.CRIT_CHANCE, "Crit Chance", "%", BLUE);
                addStat(lines, stack, SkyblockStatsApi.StatType.CRIT_DAMAGE, "Crit Damage", "%", BLUE);
                addStat(lines, stack, SkyblockStatsApi.StatType.ATTACK_SPEED, "Bonus Attack Speed", "%", YELLOW);

                // --- DEFENSIVE / MISC ---
                boolean hasDefense = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.HEALTH) > 0 || SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.DEFENSE) > 0;
                if (hasDefense) lines.add(Text.literal(""));
                
                addStat(lines, stack, SkyblockStatsApi.StatType.HEALTH, "Health", GREEN);
                addStat(lines, stack, SkyblockStatsApi.StatType.DEFENSE, "Defense", GREEN);
                addStat(lines, stack, SkyblockStatsApi.StatType.SPEED, "Speed", Formatting.WHITE);
                addStat(lines, stack, SkyblockStatsApi.StatType.INTELLIGENCE, "Intelligence", AQUA);
                addStat(lines, stack, SkyblockStatsApi.StatType.MAGIC_FIND, "Magic Find", AQUA);
                addStat(lines, stack, SkyblockStatsApi.StatType.FEROCITY, "Ferocity", RED);

                // --- LORE ---
                String lore = SkyblockStatsApi.getString(stack, "Lore");
                if (!lore.isEmpty()) {
                    lines.add(Text.literal(""));
                    lines.add(Text.literal(lore).formatted(GRAY, Formatting.ITALIC));
                }

                // --- ABILITY ---
                String abName = SkyblockStatsApi.getString(stack, "AbilityName");
                if (!abName.isEmpty()) {
                    lines.add(Text.literal(""));
                    lines.add(Text.literal("Item Ability: " + abName + "  ").formatted(Formatting.GOLD, Formatting.BOLD)
                            .append(Text.literal("RIGHT CLICK").formatted(YELLOW, Formatting.BOLD)));
                    
                    String abDesc = SkyblockStatsApi.getString(stack, "AbilityDesc");
                    lines.add(Text.literal(abDesc).formatted(GRAY));
                    
                    double cost = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.MANA_COST);
                    if(cost > 0) {
                        lines.add(Text.literal("Mana Cost: " + (int)cost).formatted(Formatting.DARK_GRAY));
                    }
                }

                // --- FOOTER (RARITY) ---
                String rarity = SkyblockStatsApi.getString(stack, "Rarity");
                if (!rarity.isEmpty()) {
                    lines.add(Text.literal(""));
                    Formatting c = ModPackets.getRarityColor(rarity);
                    
                    boolean isDungeon = SkyblockStatsApi.getString(stack, "IsDungeon").equals("true");
                    String type = "ITEM";
                    if(stack.getItem() instanceof net.minecraft.item.SwordItem) type = isDungeon ? "DUNGEON SWORD" : "SWORD";
                    else if(stack.getItem() instanceof net.minecraft.item.BowItem) type = isDungeon ? "DUNGEON BOW" : "BOW";
                    else if(stack.getItem() instanceof net.minecraft.item.ArmorItem) type = isDungeon ? "DUNGEON ARMOR" : "ARMOR";
                    
                    lines.add(Text.literal(rarity.toUpperCase() + " " + type).formatted(c, Formatting.BOLD));
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
