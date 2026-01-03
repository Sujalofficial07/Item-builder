package com.sujal.skyblockmaker.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

public class SkyblockStatsApi {
    public static final String NBT_KEY = "SkyblockData";

    public enum StatType {
        STRENGTH, DAMAGE, HEALTH, DEFENSE, INTELLIGENCE, CRIT_CHANCE, CRIT_DAMAGE
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
        data.putString("Rarity", rarity.toUpperCase());
        nbt.put(NBT_KEY, data);
    }

    // === NEW: Set Lore (Description) ===
    public static void setLore(ItemStack stack, String lore) {
        if (lore == null || lore.isEmpty()) return;
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound data = nbt.contains(NBT_KEY) ? nbt.getCompound(NBT_KEY) : new NbtCompound();
        data.putString("Lore", lore);
        nbt.put(NBT_KEY, data);
    }
}
