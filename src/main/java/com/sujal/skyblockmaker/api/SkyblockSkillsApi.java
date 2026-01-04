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

import java.text.DecimalFormat;

public class SkyblockSkillsApi {
    private static final String KEY = "SB_Skills";

    public enum Skill {
        FARMING, MINING, COMBAT, FORAGING, FISHING, ENCHANTING, ALCHEMY
    }

    // --- HYPIXEL EXACT XP TABLE (Cumulative XP required to REACH Level X) ---
    // Source: Your provided chart
    private static final double[] XP_TABLE = new double[] {
        0,          // Lvl 0
        50,         // Lvl 1
        175,        // Lvl 2 (+125)
        375,        // Lvl 3 (+200)
        675,        // Lvl 4 (+300)
        1175,       // Lvl 5 (+500)
        1925,       // Lvl 6 (+750)
        2925,       // Lvl 7 (+1000)
        4425,       // Lvl 8 (+1500)
        6425,       // Lvl 9 (+2000)
        9925,       // Lvl 10 (+3500)
        14925,      // Lvl 11 (+5000)
        22425,      // Lvl 12 (+7500)
        32425,      // Lvl 13 (+10k)
        47425,      // Lvl 14 (+15k)
        67425,      // Lvl 15 (+20k)
        97425,      // Lvl 16 (+30k)
        147425,     // Lvl 17 (+50k)
        222425,     // Lvl 18 (+75k)
        322425,     // Lvl 19 (+100k)
        522425,     // Lvl 20 (+200k)
        822425,     // Lvl 21 (+300k)
        1222425,    // Lvl 22 (+400k)
        1722425,    // Lvl 23 (+500k)
        2322425,    // Lvl 24 (+600k)
        3022425,    // Lvl 25 (+700k)
        3822425,    // Lvl 26 (+800k)
        4722425,    // Lvl 27 (+900k)
        5722425,    // Lvl 28 (+1M)
        6822425,    // Lvl 29 (+1.1M)
        8022425,    // Lvl 30 (+1.2M)
        9322425,    // Lvl 31 (+1.3M)
        10722425,   // Lvl 32 (+1.4M)
        12222425,   // Lvl 33 (+1.5M)
        13822425,   // Lvl 34 (+1.6M)
        15522425,   // Lvl 35 (+1.7M)
        17322425,   // Lvl 36 (+1.8M)
        19222425,   // Lvl 37 (+1.9M)
        21222425,   // Lvl 38 (+2M)
        23322425,   // Lvl 39 (+2.1M)
        25522425,   // Lvl 40 (+2.2M)
        27822425,   // Lvl 41 (+2.3M)
        30222425,   // Lvl 42 (+2.4M)
        32722425,   // Lvl 43 (+2.5M)
        35322425,   // Lvl 44 (+2.6M)
        38072425,   // Lvl 45 (+2.75M)
        40972425,   // Lvl 46 (+2.9M)
        44072425,   // Lvl 47 (+3.1M)
        47472425,   // Lvl 48 (+3.4M)
        51172245,   // Lvl 49 (+3.7M)
        55172245    // Lvl 50 (+4M)
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

        // Action Bar (+10 Combat XP)
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

    // --- LEVEL CALCULATION ---
    public static int getLevelFromXp(double xp) {
        for (int i = 0; i < XP_TABLE.length; i++) {
            if (xp < XP_TABLE[i]) {
                return i; // Return previous level
            }
        }
        return 50; // Max Level
    }

    public static double getXpForNextLevel(int currentLevel) {
        if (currentLevel >= 50) return 0;
        return XP_TABLE[currentLevel + 1];
    }

    public static double getXpForCurrentLevel(int currentLevel) {
        if (currentLevel == 0) return 0;
        return XP_TABLE[currentLevel];
    }

    // --- REWARDS ---
    private static void handleLevelUp(ServerPlayerEntity player, Skill skill, int newLevel) {
        player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1f, 1f);
        
        String skillName = capitalize(skill.name());
        
        // Title
        Text title = Text.literal("§6§lSKILL LEVEL UP!");
        Text subtitle = Text.literal("§7" + skillName + " " + (newLevel-1) + "➜ §6" + newLevel);
        
        player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.TitleS2CPacket(title));
        player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.SubtitleS2CPacket(subtitle));

        // Coins Reward (Scaling)
        double coinReward = 0;
        if(newLevel < 10) coinReward = 100;
        else if(newLevel < 20) coinReward = 500;
        else coinReward = 2000;
        
        SkyblockEconomyApi.addCoins(player, coinReward);

        // Chat
        player.sendMessage(Text.literal("--------------------------------"), false);
        player.sendMessage(Text.literal("§6§lSKILL LEVEL UP! §3" + skillName + " " + (newLevel-1) + "➜§3" + newLevel), false);
        player.sendMessage(Text.literal("§aREWARDS"), false);
        player.sendMessage(Text.literal("  §e+" + (int)coinReward + " Coins"), false);
        player.sendMessage(Text.literal("  " + getStatRewardDescription(skill)), false);
        player.sendMessage(Text.literal("--------------------------------"), false);

        SkyblockStatHandler.updatePlayerStats(player);
    }

    public static double getSkillStatBonus(PlayerEntity player, SkyblockStatsApi.StatType statType) {
        double bonus = 0;
        if (statType == SkyblockStatsApi.StatType.HEALTH) {
            bonus += getLevelFromXp(getXp(player, Skill.FARMING)) * 2;
            bonus += getLevelFromXp(getXp(player, Skill.FISHING)) * 2;
        }
        if (statType == SkyblockStatsApi.StatType.DEFENSE) {
            bonus += getLevelFromXp(getXp(player, Skill.MINING)) * 1; // Mining gives Defense
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
        return str.charAt(0) + str.substring(1).toLowerCase();
    }
}
