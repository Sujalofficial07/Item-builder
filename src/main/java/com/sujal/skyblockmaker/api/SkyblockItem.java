package com.sujal.skyblockmaker.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class SkyblockItem {
    
    public String id;
    public String displayName;
    public Item material;
    public String rarity;
    public String type; // WEAPON, BOW, ARMOR, MATERIAL
    
    // Stats
    public double damage = 0, strength = 0, critChance = 0, critDamage = 0, attackSpeed = 0;
    public double health = 0, defense = 0, speed = 0, intelligence = 0, ferocity = 0, magicFind = 0;
    
    // Ability
    public String abilityName = "";
    public String abilityDesc = "";
    public double manaCost = 0;
    public int cooldownTicks = 0; // Cooldown support

    public boolean isDungeon = false;
    public boolean isEnchanted = false; // For glowing effect

    public SkyblockItem(String id, String name, Item mat, String rarity, String type) {
        this.id = id;
        this.displayName = name;
        this.material = mat;
        this.rarity = rarity;
        this.type = type;
    }

    // Har item apni ability yahan likhega
    public void onAbility(World world, PlayerEntity player, ItemStack stack) {
        // Default: No ability
    }

    public ItemStack createStack() {
        ItemStack stack = new ItemStack(material);
        
        SkyblockStatsApi.setString(stack, "SkyblockID", id);
        SkyblockStatsApi.setString(stack, "Rarity", rarity);
        
        SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.DAMAGE, damage);
        SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.STRENGTH, strength);
        SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.CRIT_CHANCE, critChance);
        SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.CRIT_DAMAGE, critDamage);
        SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.ATTACK_SPEED, attackSpeed);
        
        SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.HEALTH, health);
        SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.DEFENSE, defense);
        SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.INTELLIGENCE, intelligence);
        SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.FEROCITY, ferocity);
        SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.MAGIC_FIND, magicFind);

        if(!abilityName.isEmpty()) {
            SkyblockStatsApi.setString(stack, "AbilityName", abilityName);
            SkyblockStatsApi.setString(stack, "AbilityDesc", abilityDesc);
            if(manaCost > 0) SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.MANA_COST, manaCost);
        }

        if(isDungeon) SkyblockStatsApi.setString(stack, "IsDungeon", "true");
        if(isEnchanted) stack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        
        return stack;
    }
}
