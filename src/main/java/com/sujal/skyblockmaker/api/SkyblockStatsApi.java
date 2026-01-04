package com.sujal.skyblockmaker.api;

import com.sujal.skyblockmaker.registry.ReforgeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class SkyblockStatsApi {
    public static final String NBT_KEY = "SB_Stats";

    public enum StatType {
        DAMAGE, HEALTH, DEFENSE, STRENGTH, SPEED, CRIT_CHANCE, CRIT_DAMAGE, INTELLIGENCE,
        ATTACK_SPEED, FEROCITY, MAGIC_FIND, ABILITY_DAMAGE, SEA_CREATURE_CHANCE, MANA_COST,
        MINING_SPEED, MINING_FORTUNE, FARMING_FORTUNE, FORAGING_FORTUNE, GEAR_SCORE
    }

    // Get Total Stat (Base + Reforge)
    public static double getStat(ItemStack stack, StatType stat) {
        double base = getBaseStat(stack, stat);
        String reforge = getString(stack, "Reforge");
        
        // Add Reforge Bonus
        if (!reforge.isEmpty()) {
            base += ReforgeRegistry.getBonus(reforge, stat);
        }
        return base;
    }

    // Get ONLY Base Stat (Stored in NBT)
    public static double getBaseStat(ItemStack stack, StatType stat) {
        if (stack == null || stack.isEmpty() || !stack.hasNbt()) return 0;
        NbtCompound nbt = stack.getNbt();
        if (nbt.contains(NBT_KEY)) {
            return nbt.getCompound(NBT_KEY).getDouble(stat.name());
        }
        return 0;
    }

    public static void setStat(ItemStack stack, StatType stat, double value) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound stats = nbt.contains(NBT_KEY) ? nbt.getCompound(NBT_KEY) : new NbtCompound();
        stats.putDouble(stat.name(), value);
        nbt.put(NBT_KEY, stats);
    }

    public static String getString(ItemStack stack, String key) {
        if (stack == null || !stack.hasNbt()) return "";
        NbtCompound nbt = stack.getNbt();
        return nbt.contains(NBT_KEY) && nbt.getCompound(NBT_KEY).contains(key) 
               ? nbt.getCompound(NBT_KEY).getString(key) : "";
    }

    public static void setString(ItemStack stack, String key, String value) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound stats = nbt.contains(NBT_KEY) ? nbt.getCompound(NBT_KEY) : new NbtCompound();
        stats.putString(key, value);
        nbt.put(NBT_KEY, stats);
    }
}
