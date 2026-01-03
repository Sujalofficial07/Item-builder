package com.sujal.skyblockmaker.api;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

public class SkyblockStatHandler {

    public static void updatePlayerStats(ServerPlayerEntity player) {
        // Defaults
        double health = SkyblockProfileApi.getBaseStat(player, SkyblockStatsApi.StatType.HEALTH);
        if(health <= 0) health = 100;

        // SPEED FIX: Default MUST start at 100, not 0
        double speed = SkyblockProfileApi.getBaseStat(player, SkyblockStatsApi.StatType.SPEED);
        if(speed <= 0) speed = 100; 

        double defense = SkyblockProfileApi.getBaseStat(player, SkyblockStatsApi.StatType.DEFENSE);
        double str = SkyblockProfileApi.getBaseStat(player, SkyblockStatsApi.StatType.STRENGTH);
        double damage = SkyblockProfileApi.getBaseStat(player, SkyblockStatsApi.StatType.DAMAGE);

        // Add Item Stats
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
        speed += SkyblockStatsApi.getStat(hand, SkyblockStatsApi.StatType.SPEED);

        // Apply Vanilla Attributes
        
        // 1. Health
        EntityAttributeInstance hpAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        double vanillaHp = health / 5.0;
        if (hpAttr.getBaseValue() != vanillaHp) {
            hpAttr.setBaseValue(vanillaHp);
            if (player.getHealth() > vanillaHp) player.setHealth((float) vanillaHp);
        }

        // 2. Speed (FIXED)
        // Formula: 100 Skyblock Speed = 0.1 Vanilla Speed.
        EntityAttributeInstance spdAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        double vanillaSpeed = speed / 1000.0; 
        if (spdAttr.getBaseValue() != vanillaSpeed) {
            spdAttr.setBaseValue(vanillaSpeed);
        }

        // 3. Damage
        EntityAttributeInstance dmgAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        double finalDmg = (5 + damage) * (1 + (str / 100.0));
        dmgAttr.setBaseValue(finalDmg);

        // 4. Armor
        player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(defense / 10.0);
    }
}
