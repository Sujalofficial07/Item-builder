package com.sujal.skyblockmaker.api;

import com.sujal.skyblockmaker.util.IEntityDataSaver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.text.NumberFormat;
import java.util.Locale;

public class SkyblockEconomyApi {
    private static final String KEY = "SB_Economy";

    public static double getCoins(PlayerEntity player) {
        NbtCompound data = ((IEntityDataSaver) player).getPersistentData();
        if (!data.contains(KEY)) return 0;
        return data.getCompound(KEY).getDouble("Coins");
    }

    public static void setCoins(PlayerEntity player, double amount) {
        NbtCompound data = ((IEntityDataSaver) player).getPersistentData();
        NbtCompound economy = data.contains(KEY) ? data.getCompound(KEY) : new NbtCompound();
        
        economy.putDouble("Coins", amount);
        data.put(KEY, economy);
    }

    public static void addCoins(PlayerEntity player, double amount) {
        setCoins(player, getCoins(player) + amount);
    }

    public static void removeCoins(PlayerEntity player, double amount) {
        setCoins(player, Math.max(0, getCoins(player) - amount));
    }

    // Helper to format: 100000 -> 100,000
    public static String formatCoins(double amount) {
        return NumberFormat.getNumberInstance(Locale.US).format((long) amount);
    }
}
