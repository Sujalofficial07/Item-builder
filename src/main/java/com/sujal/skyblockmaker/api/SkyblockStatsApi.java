package com.sujal.skyblockmaker.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class SkyblockStatsApi {
    public static final String NBT_KEY = "SkyblockData";

    public enum StatType {
        STRENGTH, 
        DAMAGE, 
        CRIT_CHANCE, 
        HEALTH, 
        DEFENSE,
        INTELLIGENCE // HUD ke liye zaruri hai
    }

    public static void setStat(ItemStack stack, StatType stat, double value) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound data = nbt.contains(NBT_KEY) ? nbt.getCompound(NBT_KEY) : new NbtCompound();
        data.putDouble(stat.name(), value);
        nbt.put(NBT_KEY, data);
    }

    public static double getStat(ItemStack stack, StatType stat) {
        if (stack.hasNbt() && stack.getNbt().contains(NBT_KEY)) {
            return stack.getNbt().getCompound(NBT_KEY).getDouble(stat.name());
        }
        return 0;
    }

    public static void setRarity(ItemStack stack, String rarity) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound data = nbt.contains(NBT_KEY) ? nbt.getCompound(NBT_KEY) : new NbtCompound();
        data.putString("Rarity", rarity);
        nbt.put(NBT_KEY, data);
    }
}
