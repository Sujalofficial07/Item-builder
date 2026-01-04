package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import com.sujal.skyblockmaker.client.SkyblockHudOverlay;
import com.sujal.skyblockmaker.client.gui.CategorySelectionScreen;
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
        ModItems.registerItems(); // Load Database
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
                client.setScreen(new CategorySelectionScreen()); 
            }
        });

        registerTooltips();
    }

    private static void registerTooltips() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.hasNbt() && stack.getNbt().contains(SkyblockStatsApi.NBT_KEY)) {
                
                Text originalName = lines.get(0);
                lines.clear();
                
                String reforge = SkyblockStatsApi.getString(stack, "Reforge");
                String stars = SkyblockStatsApi.getString(stack, "Stars");
                String rarity = SkyblockStatsApi.getString(stack, "Rarity");
                Formatting nameColor = ModPackets.getRarityColor(rarity);
                
                // Name: "Heroic Hyperion ✪✪✪✪✪"
                String fullName = (reforge.isEmpty() ? "" : reforge + " ") + stack.getName().getString();
                lines.add(Text.literal(fullName + (stars.isEmpty() ? "" : " " + stars)).formatted(nameColor));

                double gs = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.GEAR_SCORE);
                if(gs > 0) lines.add(Text.literal("Gear Score: " + (int)gs).formatted(Formatting.LIGHT_PURPLE));

                // Stats with Reforge visualization
                addStat(lines, stack, SkyblockStatsApi.StatType.DAMAGE, "Damage", "❁", Formatting.RED);
                addStat(lines, stack, SkyblockStatsApi.StatType.STRENGTH, "Strength", "⚔", Formatting.RED);
                addStat(lines, stack, SkyblockStatsApi.StatType.CRIT_CHANCE, "Crit Chance", "☣", Formatting.BLUE, "%");
                addStat(lines, stack, SkyblockStatsApi.StatType.CRIT_DAMAGE, "Crit Damage", "☠", Formatting.BLUE, "%");
                addStat(lines, stack, SkyblockStatsApi.StatType.ATTACK_SPEED, "Bonus Attack Speed", "⚔", Formatting.YELLOW, "%");
                
                lines.add(Text.literal(""));

                addStat(lines, stack, SkyblockStatsApi.StatType.HEALTH, "Health", "❤", Formatting.GREEN);
                addStat(lines, stack, SkyblockStatsApi.StatType.DEFENSE, "Defense", "❈", Formatting.GREEN);
                addStat(lines, stack, SkyblockStatsApi.StatType.SPEED, "Speed", "✦", Formatting.WHITE);
                addStat(lines, stack, SkyblockStatsApi.StatType.INTELLIGENCE, "Intelligence", "✎", Formatting.GREEN);
                addStat(lines, stack, SkyblockStatsApi.StatType.FEROCITY, "Ferocity", "⫽", Formatting.GREEN);
                addStat(lines, stack, SkyblockStatsApi.StatType.MAGIC_FIND, "Magic Find", "✯", Formatting.AQUA);

                lines.add(Text.literal(""));

                // Enchantments (Blue Block)
                String enchants = SkyblockStatsApi.getString(stack, "Enchants");
                if(!enchants.isEmpty()) {
                    lines.add(Text.literal(enchants).formatted(Formatting.BLUE));
                    lines.add(Text.literal(""));
                }

                // Ability
                String abName = SkyblockStatsApi.getString(stack, "AbilityName");
                if (!abName.isEmpty()) {
                    lines.add(Text.literal("Item Ability: " + abName + "  ").formatted(Formatting.GOLD)
                            .append(Text.literal("RIGHT CLICK").formatted(Formatting.YELLOW, Formatting.BOLD)));
                    
                    String abDesc = SkyblockStatsApi.getString(stack, "AbilityDesc");
                    lines.add(Text.literal(abDesc).formatted(Formatting.GRAY));
                    
                    double cost = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.MANA_COST);
                    if(cost > 0) lines.add(Text.literal("Mana Cost: " + (int)cost).formatted(Formatting.DARK_GRAY));
                    lines.add(Text.literal(""));
                }

                // Footer
                if (!rarity.isEmpty()) {
                    Formatting c = ModPackets.getRarityColor(rarity);
                    boolean isDungeon = SkyblockStatsApi.getString(stack, "IsDungeon").equals("true");
                    String type = isDungeon ? "DUNGEON " + getItemType(stack) : getItemType(stack);
                    lines.add(Text.literal(rarity.toUpperCase() + " " + type).formatted(c, Formatting.BOLD));
                }
            }
        });
    }

    private static String getItemType(net.minecraft.item.ItemStack stack) {
        if(stack.getItem() == net.minecraft.item.Items.BOW) return "BOW";
        if(stack.getItem().toString().contains("helmet")) return "HELMET";
        if(stack.getItem().toString().contains("chestplate")) return "CHESTPLATE";
        if(stack.getItem().toString().contains("leggings")) return "LEGGINGS";
        if(stack.getItem().toString().contains("boots")) return "BOOTS";
        if(stack.getItem().toString().contains("_block") || stack.getItem().toString().contains("ingot")) return "MATERIAL";
        return "SWORD";
    }

    private static void addStat(java.util.List<Text> lines, net.minecraft.item.ItemStack s, SkyblockStatsApi.StatType type, String name, String symbol, Formatting color) {
        addStat(lines, s, type, name, symbol, color, "");
    }

    private static void addStat(java.util.List<Text> lines, net.minecraft.item.ItemStack s, SkyblockStatsApi.StatType type, String name, String symbol, Formatting color, String suffix) {
        double total = SkyblockStatsApi.getStat(s, type);
        double base = SkyblockStatsApi.getBaseStat(s, type);
        
        if (total != 0) {
            String sign = total > 0 ? "+" : "";
            Text text = Text.literal(name + ": " + sign + (int)total + suffix + " " + symbol).formatted(color);
            
            // Show Reforge Bonus: (+50) in Dark Gray or Blue? Hypixel uses Blue or Gray usually.
            if (total > base) {
                double diff = total - base;
                text.getSiblings().add(Text.literal(" (Reforge +" + (int)diff + ")").formatted(Formatting.BLUE));
            }
            
            lines.add(text);
        }
    }
}
