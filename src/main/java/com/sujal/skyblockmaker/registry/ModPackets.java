package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier ITEM_CREATE_PACKET = new Identifier("skyblockmaker", "create_item");

    public static void registerServerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(ITEM_CREATE_PACKET, (server, player, handler, buf, responseSender) -> {
            
            String itemName = buf.readString();
            String rarity = buf.readString();
            String lore = buf.readString(); // Read Lore
            
            double strength = buf.readDouble();
            double defense = buf.readDouble();
            double health = buf.readDouble();
            double intel = buf.readDouble();
            double cc = buf.readDouble();
            double cd = buf.readDouble();

            server.execute(() -> {
                ItemStack newItem = new ItemStack(Items.DIAMOND_SWORD); // Default base

                // Color Logic
                Formatting color = getRarityColor(rarity);
                newItem.setCustomName(Text.literal(itemName).formatted(color));
                newItem.addHideFlag(ItemStack.TooltipSection.MODIFIERS);
                newItem.addHideFlag(ItemStack.TooltipSection.ADDITIONAL);

                // Stats
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.STRENGTH, strength);
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.DEFENSE, defense);
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.HEALTH, health);
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.INTELLIGENCE, intel);
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.CRIT_CHANCE, cc);
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.CRIT_DAMAGE, cd);
                
                SkyblockStatsApi.setRarity(newItem, rarity);
                SkyblockStatsApi.setLore(newItem, lore); // Set Lore

                player.getInventory().insertStack(newItem);
            });
        });
    }

    private static Formatting getRarityColor(String rarity) {
        return switch (rarity.toUpperCase()) {
            case "COMMON" -> Formatting.WHITE;
            case "UNCOMMON" -> Formatting.GREEN;
            case "RARE" -> Formatting.BLUE;
            case "EPIC" -> Formatting.DARK_PURPLE;
            case "LEGENDARY" -> Formatting.GOLD;
            case "MYTHIC" -> Formatting.LIGHT_PURPLE;
            default -> Formatting.GRAY;
        };
    }
}
