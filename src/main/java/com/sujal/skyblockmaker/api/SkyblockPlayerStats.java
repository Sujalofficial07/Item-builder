package com.sujal.skyblockmaker.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class SkyblockPlayerStats {

    private static final double BASE_HEALTH = 100.0;
    private static final double BASE_DEFENSE = 0.0;
    private static final double BASE_MANA = 100.0;

    public static double getTotalStat(PlayerEntity player, SkyblockStatsApi.StatType stat) {
        double total = 0;

        // Armor loop
        for (ItemStack stack : player.getInventory().armor) {
            total += SkyblockStatsApi.getStat(stack, stat);
        }

        // Main Hand
        total += SkyblockStatsApi.getStat(player.getMainHandStack(), stat);

        // Base Stats Addition
        if (stat == SkyblockStatsApi.StatType.HEALTH) return BASE_HEALTH + total;
        if (stat == SkyblockStatsApi.StatType.DEFENSE) return BASE_DEFENSE + total;
        if (stat == SkyblockStatsApi.StatType.INTELLIGENCE) return BASE_MANA + total;

        return total;
    }
}
