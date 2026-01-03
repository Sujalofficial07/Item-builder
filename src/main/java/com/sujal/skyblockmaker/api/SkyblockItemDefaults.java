package com.sujal.skyblockmaker.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class SkyblockItemDefaults {

    public static void convertToSkyblockItem(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains(SkyblockStatsApi.NBT_KEY)) return;

        Item item = stack.getItem();

        // Vanilla Items -> Skyblock God Items
        if (item == Items.DIAMOND_SWORD) {
            apply(stack, "UNCOMMON", 35, 0, 0, 0, 0, 0);
        } else if (item == Items.NETHERITE_SWORD) {
            apply(stack, "RARE", 60, 30, 0, 0, 0, 0);
        } else if (item == Items.GOLDEN_SWORD) {
            apply(stack, "COMMON", 20, 0, 0, 0, 0, 50); // Gold has Intel
            SkyblockStatsApi.setString(stack, "AbilityName", "Gold Rush");
            SkyblockStatsApi.setString(stack, "AbilityDesc", "Grants speed on use.");
        } else if (item == Items.DIAMOND_CHESTPLATE) {
            apply(stack, "UNCOMMON", 0, 0, 0, 120, 60, 0);
        } else if (item == Items.NETHERITE_CHESTPLATE) {
            apply(stack, "EPIC", 0, 10, 0, 180, 90, 0);
        }
    }

    private static void apply(ItemStack s, String r, double dmg, double str, double cc, double hp, double def, double intel) {
        SkyblockStatsApi.setString(s, "Rarity", r);
        if (dmg > 0) SkyblockStatsApi.setStat(s, SkyblockStatsApi.StatType.DAMAGE, dmg);
        if (str > 0) SkyblockStatsApi.setStat(s, SkyblockStatsApi.StatType.STRENGTH, str);
        if (cc > 0) SkyblockStatsApi.setStat(s, SkyblockStatsApi.StatType.CRIT_CHANCE, cc);
        if (hp > 0) SkyblockStatsApi.setStat(s, SkyblockStatsApi.StatType.HEALTH, hp);
        if (def > 0) SkyblockStatsApi.setStat(s, SkyblockStatsApi.StatType.DEFENSE, def);
        if (intel > 0) SkyblockStatsApi.setStat(s, SkyblockStatsApi.StatType.INTELLIGENCE, intel);
        
        // Hide vanilla
        s.addHideFlag(ItemStack.TooltipSection.MODIFIERS);
        s.addHideFlag(ItemStack.TooltipSection.ADDITIONAL);
    }
}
