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
            dispatcher.register(ClientCommandManager.literal("sbbuilder").executes(context -> { shouldOpenBuilder = true; return 1; }));
            dispatcher.register(ClientCommandManager.literal("profile").executes(context -> {
                MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new ProfileScreen(MinecraftClient.getInstance().player)));
                return 1;
            }));
            dispatcher.register(ClientCommandManager.literal("skills").executes(context -> {
                MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new SkillsScreen()));
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
                
                // Clear Vanilla Tooltips (except Name)
                Text name = lines.get(0);
                lines.clear();
                
                // 1. HEADER (Reforge + Name + Stars)
                String reforge = SkyblockStatsApi.getString(stack, "Reforge");
                String stars = SkyblockStatsApi.getString(stack, "Stars"); // e.g., "✪✪✪✪✪"
                String rarity = SkyblockStatsApi.getString(stack, "Rarity");
                Formatting nameColor = ModPackets.getRarityColor(rarity);
                
                String fullName = (reforge.isEmpty() ? "" : reforge + " ") + stack.getName().getString();
                lines.add(Text.literal(fullName + (stars.isEmpty() ? "" : " " + stars)).formatted(nameColor));

                // 2. GEAR SCORE
                double gs = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.GEAR_SCORE);
                if(gs > 0) lines.add(Text.literal("Gear Score: " + (int)gs + " §8(3572)").formatted(Formatting.LIGHT_PURPLE));

                // 3. STATS (With Symbols)
                addStat(lines, stack, SkyblockStatsApi.StatType.DAMAGE, "Damage", "❁", Formatting.RED);
                addStat(lines, stack, SkyblockStatsApi.StatType.STRENGTH, "Strength", "⚔", Formatting.RED);
                addStat(lines, stack, SkyblockStatsApi.StatType.CRIT_CHANCE, "Crit Chance", "☣", Formatting.BLUE, "%");
                addStat(lines, stack, SkyblockStatsApi.StatType.CRIT_DAMAGE, "Crit Damage", "☠", Formatting.BLUE, "%");
                addStat(lines, stack, SkyblockStatsApi.StatType.ATTACK_SPEED, "Bonus Attack Speed", "⚔", Formatting.YELLOW, "%");
                
                lines.add(Text.literal("")); // Spacer

                addStat(lines, stack, SkyblockStatsApi.StatType.INTELLIGENCE, "Intelligence", "✎", Formatting.GREEN); // Aqua/Green based on image
                addStat(lines, stack, SkyblockStatsApi.StatType.FEROCITY, "Ferocity", "⫽", Formatting.GREEN); // Matching your image (Green)
                addStat(lines, stack, SkyblockStatsApi.StatType.MAGIC_FIND, "Magic Find", "✯", Formatting.AQUA);

                lines.add(Text.literal("")); // Spacer

                // 4. ENCHANTMENTS (Blue Block)
                String enchants = SkyblockStatsApi.getString(stack, "Enchants");
                if(!enchants.isEmpty()) {
                    // Split by comma and format nicely
                    lines.add(Text.literal(enchants).formatted(Formatting.BLUE));
                    lines.add(Text.literal(""));
                }

                // 5. ABILITY
                String abName = SkyblockStatsApi.getString(stack, "AbilityName");
                if (!abName.isEmpty()) {
                    lines.add(Text.literal("Item Ability: " + abName + "  ").formatted(Formatting.GOLD)
                            .append(Text.literal("RIGHT CLICK").formatted(Formatting.YELLOW, Formatting.BOLD)));
                    
                    String abDesc = SkyblockStatsApi.getString(stack, "AbilityDesc");
                    // Split description for wrapping (Simple logic)
                    lines.add(Text.literal(abDesc).formatted(Formatting.GRAY));
                    
                    double cost = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.MANA_COST);
                    if(cost > 0) {
                        lines.add(Text.literal("Mana Cost: " + (int)cost).formatted(Formatting.DARK_GRAY));
                    }
                    lines.add(Text.literal(""));
                }

                // 6. FOOTER (MYTHIC DUNGEON SWORD)
                if (!rarity.isEmpty()) {
                    Formatting c = ModPackets.getRarityColor(rarity);
                    boolean isDungeon = SkyblockStatsApi.getString(stack, "IsDungeon").equals("true");
                    String type = isDungeon ? "DUNGEON SWORD" : "SWORD"; // Can vary based on item
                    if(stack.getItem() == net.minecraft.item.Items.BOW) type = isDungeon ? "DUNGEON BOW" : "BOW";
                    
                    lines.add(Text.literal(rarity.toUpperCase() + " " + type).formatted(c, Formatting.BOLD));
                }
            }
        });
    }

    private static void addStat(java.util.List<Text> lines, net.minecraft.item.ItemStack s, SkyblockStatsApi.StatType type, String name, String symbol, Formatting color) {
        addStat(lines, s, type, name, symbol, color, "");
    }

    private static void addStat(java.util.List<Text> lines, net.minecraft.item.ItemStack s, SkyblockStatsApi.StatType type, String name, String symbol, Formatting color, String suffix) {
        double val = SkyblockStatsApi.getStat(s, type);
        if (val != 0) {
            String sign = val > 0 ? "+" : "";
            // Format: "Damage: +260 ❁"
            lines.add(Text.literal(name + ": " + sign + (int)val + suffix + " " + symbol).formatted(color));
        }
    }
}
