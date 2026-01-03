package com.sujal.skyblockmaker.api;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

public class SkyblockStatHandler {

    public static void updatePlayerStats(ServerPlayerEntity player) {
        // 1. Get Base Stats from Profile
        double health = SkyblockProfileApi.getBaseStat(player, SkyblockStatsApi.StatType.HEALTH);
        double defense = SkyblockProfileApi.getBaseStat(player, SkyblockStatsApi.StatType.DEFENSE);
        double str = SkyblockProfileApi.getBaseStat(player, SkyblockStatsApi.StatType.STRENGTH);
        double speed = SkyblockProfileApi.getBaseStat(player, SkyblockStatsApi.StatType.SPEED);
        double critDmg = SkyblockProfileApi.getBaseStat(player, SkyblockStatsApi.StatType.CRIT_DAMAGE);
        double damage = SkyblockProfileApi.getBaseStat(player, SkyblockStatsApi.StatType.DAMAGE);

        // 2. Add Item Stats
        for (ItemStack stack : player.getInventory().armor) {
            health += SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.HEALTH);
            defense += SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.DEFENSE);
            str += SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.STRENGTH);
            speed += SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.SPEED);
            damage += SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.DAMAGE);
        }
        
        ItemStack hand = player.getMainHandStack();
        damage += SkyblockStatsApi.getStat(hand, SkyblockStatsApi.StatType.DAMAGE);
        str += SkyblockStatsApi.getStat(hand, SkyblockStatsApi.StatType.STRENGTH);
        health += SkyblockStatsApi.getStat(hand, SkyblockStatsApi.StatType.HEALTH);

        // 3. Apply to Vanilla
        // Health
        EntityAttributeInstance hpAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        double vanillaHp = health / 5.0;
        if (hpAttr.getBaseValue() != vanillaHp) {
            hpAttr.setBaseValue(vanillaHp);
            if (player.getHealth() > vanillaHp) player.setHealth((float) vanillaHp);
        }

        // Speed
        EntityAttributeInstance spdAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        spdAttr.setBaseValue(speed / 1000.0);

        // Damage Formula: (5 + Damage) * (1 + Strength/100)
        EntityAttributeInstance dmgAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        double finalDmg = (5 + damage) * (1 + (str / 100.0));
        dmgAttr.setBaseValue(finalDmg);

        // Armor
        player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(defense / 10.0);
    }
}
