package com.sujal.skyblockmaker.api;

import com.sujal.skyblockmaker.registry.ModPackets;
import com.sujal.skyblockmaker.util.IEntityDataSaver;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class SkyblockSkillsApi {
    private static final String KEY = "SB_Skills";

    public enum Skill {
        FARMING, MINING, COMBAT, FORAGING, FISHING, ENCHANTING, ALCHEMY
    }

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
        
        // Save
        skills.putDouble(skill.name(), newXp);
        data.put(KEY, skills);

        // SYNC TO CLIENT (Fixes Buggy GUI)
        syncSkills(player, skills);

        // Check Level Up
        int oldLevel = getLevelFromXp(currentXp);
        int newLevel = getLevelFromXp(newXp);

        // Action Bar Notification
        player.sendMessage(Text.literal("§3+" + (int)amount + " " + capitalize(skill.name()) + " XP"), true);

        if (newLevel > oldLevel) {
            handleLevelUp(player, skill, newLevel);
        }
    }

    // New Sync Method
    public static void syncSkills(ServerPlayerEntity player, NbtCompound skillsData) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(skillsData);
        ServerPlayNetworking.send(player, ModPackets.SKILL_SYNC_PACKET, buf);
    }

    public static int getLevelFromXp(double xp) {
        int level = 0;
        // Simple Scaling: Level 1=50xp, Level 2=150xp, etc.
        double xpRequired = 50;
        while (xp >= xpRequired && level < 50) {
            xp -= xpRequired;
            level++;
            xpRequired += (level * 100); 
        }
        return level;
    }

    private static void handleLevelUp(ServerPlayerEntity player, Skill skill, int newLevel) {
        player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1f, 1f);
        
        String skillName = capitalize(skill.name());
        Text title = Text.literal("§6§lSKILL LEVEL UP!");
        Text subtitle = Text.literal("§7" + skillName + " " + (newLevel-1) + "➜ §6" + newLevel);
        
        player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.TitleS2CPacket(title));
        player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.SubtitleS2CPacket(subtitle));

        double coinReward = newLevel * 100;
        SkyblockEconomyApi.addCoins(player, coinReward);

        player.sendMessage(Text.literal("--------------------------------"), false);
        player.sendMessage(Text.literal("§6§lSKILL LEVEL UP! §3" + skillName + " " + (newLevel-1) + "➜§3" + newLevel), false);
        player.sendMessage(Text.literal("§aREWARDS"), false);
        player.sendMessage(Text.literal("  §e+" + (int)coinReward + " Coins"), false);
        
        String statReward = getStatRewardDescription(skill);
        if (!statReward.isEmpty()) {
            player.sendMessage(Text.literal("  " + statReward), false);
        }
        player.sendMessage(Text.literal("--------------------------------"), false);

        SkyblockStatHandler.updatePlayerStats(player);
    }

    // --- STAT BONUSES ---
    public static double getSkillStatBonus(PlayerEntity player, SkyblockStatsApi.StatType statType) {
        double bonus = 0;
        if (statType == SkyblockStatsApi.StatType.HEALTH) {
            bonus += getLevelFromXp(getXp(player, Skill.FARMING)) * 2;
            bonus += getLevelFromXp(getXp(player, Skill.FISHING)) * 2;
        }
        if (statType == SkyblockStatsApi.StatType.DEFENSE) {
            bonus += getLevelFromXp(getXp(player, Skill.MINING)) * 1;
        }
        if (statType == SkyblockStatsApi.StatType.CRIT_CHANCE) {
            bonus += getLevelFromXp(getXp(player, Skill.COMBAT)) * 0.5;
        }
        if (statType == SkyblockStatsApi.StatType.STRENGTH) {
            bonus += getLevelFromXp(getXp(player, Skill.FORAGING)) * 1;
        }
        if (statType == SkyblockStatsApi.StatType.INTELLIGENCE) {
            bonus += getLevelFromXp(getXp(player, Skill.ENCHANTING)) * 1;
            bonus += getLevelFromXp(getXp(player, Skill.ALCHEMY)) * 1;
        }
        return bonus;
    }

    private static String getStatRewardDescription(Skill skill) {
        return switch (skill) {
            case FARMING, FISHING -> "§c+2 Health";
            case MINING -> "§a+1 Defense";
            case COMBAT -> "§9+0.5% Crit Chance";
            case FORAGING -> "§c+1 Strength";
            case ENCHANTING, ALCHEMY -> "§b+1 Intelligence";
        };
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.charAt(0) + str.substring(1).toLowerCase();
    }
}
