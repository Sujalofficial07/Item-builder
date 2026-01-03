package com.sujal.skyblockmaker.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class SkyblockStatsApi {
    public static final String NBT_KEY = "SkyblockData";

    public enum StatType {
        DAMAGE, STRENGTH, CRIT_CHANCE, CRIT_DAMAGE, ATTACK_SPEED,
        HEALTH, DEFENSE, SPEED, INTELLIGENCE, FEROCITY, MAGIC_FIND
    }

    // Set Stat (Double)
    public static void setStat(ItemStack stack, StatType stat, double value) {
        if (value == 0) return; // Save storage
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound data = nbt.contains(NBT_KEY) ? nbt.getCompound(NBT_KEY) : new NbtCompound();
        data.putDouble(stat.name(), value);
        nbt.put(NBT_KEY, data);
    }

    // Get Stat
    public static double getStat(ItemStack stack, StatType stat) {
        if (stack.hasNbt() && stack.getNbt().contains(NBT_KEY)) {
            return stack.getNbt().getCompound(NBT_KEY).getDouble(stat.name());
        }
        return 0;
    }

    // Set String Data (Rarity, Ability, Reforge)
    public static void setString(ItemStack stack, String key, String value) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound data = nbt.contains(NBT_KEY) ? nbt.getCompound(NBT_KEY) : new NbtCompound();
        data.putString(key, value);
        nbt.put(NBT_KEY, data);
    }

    public static String getString(ItemStack stack, String key) {
        if (stack.hasNbt() && stack.getNbt().contains(NBT_KEY)) {
            return stack.getNbt().getCompound(NBT_KEY).getString(key);
        }
        return "";
    }
}
