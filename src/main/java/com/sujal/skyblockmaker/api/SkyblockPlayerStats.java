package com.sujal.skyblockmaker.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class SkyblockPlayerStats {

    // Base Stats (Bina armor ke kitna hoga)
    private static final double BASE_HEALTH = 100.0;
    private static final double BASE_DEFENSE = 0.0;
    private static final double BASE_MANA = 100.0;

    // Total Stat Calculate karne ka function
    public static double getTotalStat(PlayerEntity player, SkyblockStatsApi.StatType stat) {
        double total = 0;

        // 1. Armor Slots se stats add karo
        for (ItemStack stack : player.getInventory().armor) {
            total += SkyblockStatsApi.getStat(stack, stat);
        }

        // 2. Main Hand item se stats add karo
        total += SkyblockStatsApi.getStat(player.getMainHandStack(), stat);

        // 3. Base Stats add karo
        if (stat == SkyblockStatsApi.StatType.HEALTH) return BASE_HEALTH + total;
        if (stat == SkyblockStatsApi.StatType.DEFENSE) return BASE_DEFENSE + total;
        if (stat == SkyblockStatsApi.StatType.INTELLIGENCE) return BASE_MANA + total; // Mana = Intelligence usually

        return total;
    }
    
    public static int getCurrentMana(PlayerEntity player) {
        // Simplified: Abhi ke liye Max Mana hi dikhate hain
        // Future mein yahan variable store kar sakte ho jo spell use karne par kam ho
        return (int) getTotalStat(player, SkyblockStatsApi.StatType.INTELLIGENCE);
    }
}
