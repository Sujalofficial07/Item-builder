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
            
            // 1. Read All Data
            String itemName = buf.readString();
            String rarity = buf.readString();
            double strength = buf.readDouble();
            double defense = buf.readDouble();
            double health = buf.readDouble();
            double intel = buf.readDouble();
            double critChance = buf.readDouble();
            double critDmg = buf.readDouble();

            server.execute(() -> {
                // Item create (Default Chestplate for now, later dropdown)
                ItemStack newItem = new ItemStack(Items.DIAMOND_CHESTPLATE); 
                
                // 2. Set Custom Name with Rarity Color
                Formatting color = getRarityColor(rarity);
                newItem.setCustomName(Text.literal(itemName).formatted(color));

                // 3. CLEAN UP (Hide Vanilla "Armor +8" text)
                newItem.addHideFlag(ItemStack.TooltipSection.MODIFIERS);
                newItem.addHideFlag(ItemStack.TooltipSection.ADDITIONAL);
                
                // 4. Set All Stats
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.STRENGTH, strength);
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.DEFENSE, defense);
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.HEALTH, health);
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.INTELLIGENCE, intel);
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.CRIT_CHANCE, critChance);
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.CRIT_DAMAGE, critDmg);
                
                SkyblockStatsApi.setRarity(newItem, rarity);

                player.getInventory().insertStack(newItem);
            });
        });
    }

    // Helper: Rarity to Color Converter
    private static Formatting getRarityColor(String rarity) {
        switch (rarity.toUpperCase()) {
            case "COMMON": return Formatting.WHITE;
            case "UNCOMMON": return Formatting.GREEN;
            case "RARE": return Formatting.BLUE;
            case "EPIC": return Formatting.DARK_PURPLE;
            case "LEGENDARY": return Formatting.GOLD;
            case "MYTHIC": return Formatting.LIGHT_PURPLE;
            case "DIVINE": return Formatting.AQUA;
            case "SPECIAL": return Formatting.RED;
            default: return Formatting.GRAY;
        }
    }
}
