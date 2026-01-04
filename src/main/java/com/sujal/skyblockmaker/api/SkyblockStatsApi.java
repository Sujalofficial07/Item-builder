package com.sujal.skyblockmaker.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class SkyblockStatsApi {
    public static final String NBT_KEY = "SB_Stats";

    public enum StatType {
        // Core Stats
        DAMAGE, HEALTH, DEFENSE, STRENGTH, SPEED, CRIT_CHANCE, CRIT_DAMAGE, INTELLIGENCE,
        // Advanced Stats
        ATTACK_SPEED, FEROCITY, MAGIC_FIND, ABILITY_DAMAGE, SEA_CREATURE_CHANCE, MANA_COST,
        // Mining/Misc
        MINING_SPEED, MINING_FORTUNE, FARMING_FORTUNE, FORAGING_FORTUNE,
        // Meta
        GEAR_SCORE
    }

    public static double getStat(ItemStack stack, StatType stat) {
        if (stack == null || stack.isEmpty() || !stack.hasNbt()) return 0;
        NbtCompound nbt = stack.getNbt();
        if (nbt.contains(NBT_KEY)) {
            NbtCompound stats = nbt.getCompound(NBT_KEY);
            return stats.getDouble(stat.name());
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
