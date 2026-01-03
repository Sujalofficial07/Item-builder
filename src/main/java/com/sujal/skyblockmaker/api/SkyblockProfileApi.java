package com.sujal.skyblockmaker.api;

import com.sujal.skyblockmaker.util.IEntityDataSaver; // New Import
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class SkyblockProfileApi {
    private static final String KEY = "SB_Profile";

    public static double getBaseStat(PlayerEntity player, SkyblockStatsApi.StatType stat) {
        // Cast to the new interface location
        NbtCompound data = ((IEntityDataSaver) player).getPersistentData();
        if (!data.contains(KEY)) return getDefault(stat);
        
        NbtCompound profile = data.getCompound(KEY);
        return profile.contains(stat.name()) ? profile.getDouble(stat.name()) : getDefault(stat);
    }

    public static void setBaseStat(PlayerEntity player, SkyblockStatsApi.StatType stat, double value) {
        NbtCompound data = ((IEntityDataSaver) player).getPersistentData();
        NbtCompound profile = data.contains(KEY) ? data.getCompound(KEY) : new NbtCompound();
        
        profile.putDouble(stat.name(), value);
        data.put(KEY, profile);
    }
    
    private static double getDefault(SkyblockStatsApi.StatType stat) {
        return switch (stat) {
            case HEALTH, SPEED, INTELLIGENCE -> 100;
            default -> 0;
        };
    }
}
