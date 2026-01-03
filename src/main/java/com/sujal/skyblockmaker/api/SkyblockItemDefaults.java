package com.sujal.skyblockmaker.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class SkyblockItemDefaults {

    // Ye function check karega ki item Vanilla hai ya nahi, aur stats lagayega
    public static void convertToSkyblockItem(ItemStack stack) {
        // Agar item pehle se Skyblock item hai, toh kuch mat karo
        if (stack.hasNbt() && stack.getNbt().contains(SkyblockStatsApi.NBT_KEY)) {
            return;
        }

        Item item = stack.getItem();

        // === SWORDS ===
        if (item == Items.DIAMOND_SWORD) {
            applyStats(stack, "Diamond Sword", "UNCOMMON", 30, 0, 0, 0);
        } else if (item == Items.IRON_SWORD) {
            applyStats(stack, "Iron Sword", "COMMON", 20, 0, 0, 0);
        } else if (item == Items.GOLDEN_SWORD) {
            applyStats(stack, "Golden Sword", "COMMON", 15, 0, 0, 50); // Gold has Intelligence
        } else if (item == Items.NETHERITE_SWORD) {
            applyStats(stack, "Netherite Blade", "RARE", 50, 20, 0, 0);
        }

        // === ARMOR ===
        else if (item == Items.DIAMOND_CHESTPLATE) {
            applyStats(stack, "Diamond Chestplate", "UNCOMMON", 0, 0, 100, 50); // 100 HP, 50 Def
        } else if (item == Items.IRON_CHESTPLATE) {
            applyStats(stack, "Iron Chestplate", "COMMON", 0, 0, 50, 30);
        }
        
        // Tum yahan aur items add kar sakte ho...
    }

    private static void applyStats(ItemStack stack, String name, String rarity, double dmg, double str, double hp, double def) {
        // 1. Basic Setup
        SkyblockStatsApi.setRarity(stack, rarity);
        
        // 2. Stats
        if (dmg > 0) SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.DAMAGE, dmg);
        if (str > 0) SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.STRENGTH, str);
        if (hp > 0) SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.HEALTH, hp);
        if (def > 0) SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.DEFENSE, def);

        // 3. Hide Vanilla Text
        stack.addHideFlag(ItemStack.TooltipSection.MODIFIERS);
        stack.addHideFlag(ItemStack.TooltipSection.ADDITIONAL);
    }
}
