package com.sujal.skyblockmaker.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class SkyblockProfileApi {
    private static final String PROFILE_KEY = "SkyblockProfile";

    // Base Stats get karna
    public static double getBaseStat(PlayerEntity player, SkyblockStatsApi.StatType stat) {
        NbtCompound nbt = player.getCommandTags().contains("sb_data") ? ((IEntityDataSaver) player).getPersistentData() : new NbtCompound();
        // Note: Simple Vanilla NBT access ke liye hum player ki persistent data access karenge
        // Fabric mein accessor mixin use hota hai usually, par hum simple NBT read/write karenge tag ke through
        
        NbtCompound data = player.getnbt(); // Pseudo code logic, niche real logic dekho
        // Real logic: NBT player object par direct accessible nahi hota bina Mixin ke.
        // Isliye hum Scoreboard Tags ya fir Simple Memory Map use karenge filhal ke liye.
        
        // BETTER APPROACH: Scoreboard Objectives se data store karna (Vanilla friendly & Safe)
        String objName = "sb_" + stat.name().toLowerCase();
        return player.getScoreboard().getNullableObjective(objName) != null ? 
               player.getScoreboard().getPlayerScore(player.getEntityName(), player.getScoreboard().getObjective(objName)).getScore() : 0;
    }

    // Is easy approach ke liye hum ek custom HashMap use karenge jo NBT read/write karega
    // Lekin best aur simplest tareeka 'ServerPlayerEntity' ka persistent data use karna hai.
    
    // Simplest Implementation for Mod:
    public static double getProfileStat(PlayerEntity player, SkyblockStatsApi.StatType stat) {
        NbtCompound nbt = ((ServerPlayerEntityAccess) player).getPersistentData();
        if (nbt.contains(PROFILE_KEY)) {
            return nbt.getCompound(PROFILE_KEY).getDouble(stat.name());
        }
        // Defaults:
        if (stat == SkyblockStatsApi.StatType.HEALTH) return 100;
        if (stat == SkyblockStatsApi.StatType.SPEED) return 100;
        if (stat == SkyblockStatsApi.StatType.INTELLIGENCE) return 100;
        return 0;
    }

    public static void setProfileStat(PlayerEntity player, SkyblockStatsApi.StatType stat, double value) {
        NbtCompound nbt = ((ServerPlayerEntityAccess) player).getPersistentData();
        NbtCompound profile = nbt.contains(PROFILE_KEY) ? nbt.getCompound(PROFILE_KEY) : new NbtCompound();
        
        profile.putDouble(stat.name(), value);
        nbt.put(PROFILE_KEY, profile);
    }
}
