package com.sujal.skyblockmaker.api;

import com.sujal.skyblockmaker.registry.ModPackets;
import com.sujal.skyblockmaker.util.IEntityDataSaver; // Updated Import
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.text.DecimalFormat;

public class SkyblockSkillsApi {
    private static final String KEY = "SB_Skills";

    public enum Skill {
        COMBAT, FARMING, MINING, FORAGING, FISHING, ENCHANTING, ALCHEMY, TAMING
    }

    // Exact Cumulative XP Table for Levels 1-50 (Hypixel Standard)
    private static final double[] XP_TABLE = new double[] {
        0, 50, 175, 375, 675, 1175, 1925, 2925, 4425, 6425, 
        9925, 14925, 22425, 32425, 47425, 67425, 97425, 147425, 222425, 322425, 
        522425, 822425, 1222425, 1722425, 2322425, 3022425, 3822425, 4722425, 5722425, 6822425, 
        8022425, 9322425, 10722425, 12222425, 13822425, 15522425, 17322425, 19222425, 21222425, 23322425, 
        25522425, 27822425, 30222425, 32722425, 35322425, 38072425, 40972425, 44072425, 47472425, 51172245, 
        55172245
    };

    public static double getXp(PlayerEntity player, Skill skill) {
        NbtCompound data = ((IEntityDataSaver) player).getPersistentData();
        if (!data.contains(KEY)) return 0;
        return data.getCompound(KEY).getDouble(skill.name());
    }

    public static void addXp(ServerPlayerEntity player, Skill skill, double amount) {
        NbtCompound data = ((IEntityDataSaver) player).getPersistentData();
        NbtCompound skills = data.contains(KEY) ? data.getCompound(KEY) : new NbtCompound();
        
        double currentXp = skills.getDouble(skill.name());
        double newXp = currentXp + amount;
        
        skills.putDouble(skill.name(), newXp);
        data.put(KEY, skills);

        syncSkills(player, skills);

        int oldLevel = getLevelFromXp(currentXp);
        int newLevel = getLevelFromXp(newXp);

        // Action Bar Notification
        player.sendMessage(Text.literal("§3+" + new DecimalFormat("#.##").format(amount) + " " + capitalize(skill.name()) + " XP"), true);

        if (newLevel > oldLevel) {
            handleLevelUp(player, skill, newLevel);
        }
    }

    public static void syncSkills(ServerPlayerEntity player, NbtCompound skillsData) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(skillsData);
        ServerPlayNetworking.send(player, ModPackets.SKILL_SYNC_PACKET, buf);
    }

    public static int getLevelFromXp(double xp) {
        for (int i = 0; i < XP_TABLE.length; i++) {
            if (xp < XP_TABLE[i]) return i;
        }
        return 50;
    }

    public static double getXpForLevel(int level) {
        if (level >= 50) return XP_TABLE[49];
        return XP_TABLE[level];
    }
    
    private static void handleLevelUp(ServerPlayerEntity player, Skill skill, int newLevel) {
        player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1f, 1f);
        String skillName = capitalize(skill.name());
        
        Text title = Text.literal("§6§lSKILL LEVEL UP!");
        Text subtitle = Text.literal("§7" + skillName + " " + (newLevel-1) + "➜ §6" + newLevel);
        
        player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.TitleS2CPacket(title));
        player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.SubtitleS2CPacket(subtitle));

        double coinReward = (newLevel < 10) ? 100 : (newLevel < 20 ? 500 : 2000);
        SkyblockEconomyApi.addCoins(player, coinReward);
        SkyblockStatHandler.updatePlayerStats(player);
        
        // Chat Message
        player.sendMessage(Text.literal("--------------------------------"), false);
        player.sendMessage(Text.literal("§6§lSKILL LEVEL UP! §3" + skillName + " " + (newLevel-1) + "➜§3" + newLevel), false);
        player.sendMessage(Text.literal("§aREWARDS"), false);
        player.sendMessage(Text.literal("  §e+" + (int)coinReward + " Coins"), false);
        player.sendMessage(Text.literal("--------------------------------"), false);
    }

    public static double getSkillStatBonus(PlayerEntity player, SkyblockStatsApi.StatType statType) {
        double bonus = 0;
        if (statType == SkyblockStatsApi.StatType.HEALTH) {
            bonus += getLevelFromXp(getXp(player, Skill.FARMING)) * 2;
            bonus += getLevelFromXp(getXp(player, Skill.FISHING)) * 2;
        }
        if (statType == SkyblockStatsApi.StatType.DEFENSE) bonus += getLevelFromXp(getXp(player, Skill.MINING)) * 1;
        if (statType == SkyblockStatsApi.StatType.CRIT_CHANCE) bonus += getLevelFromXp(getXp(player, Skill.COMBAT)) * 0.5;
        if (statType == SkyblockStatsApi.StatType.STRENGTH) bonus += getLevelFromXp(getXp(player, Skill.FORAGING)) * 1;
        if (statType == SkyblockStatsApi.StatType.INTELLIGENCE) {
            bonus += getLevelFromXp(getXp(player, Skill.ENCHANTING)) * 1;
            bonus += getLevelFromXp(getXp(player, Skill.ALCHEMY)) * 1;
            bonus += getLevelFromXp(getXp(player, Skill.TAMING)) * 1;
        }
        return bonus;
    }

    private static String capitalize(String str) {
        return str.charAt(0) + str.substring(1).toLowerCase();
    }
}
