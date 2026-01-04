package com.sujal.skyblockmaker.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SkyblockItem {
    public String id;
    public String displayName;
    public Item material;
    public String rarity;
    public String type; // "SWORD", "BOW", "MATERIAL", "DUNGEON_ITEM"
    
    // Stats
    public double damage, strength, critChance, critDamage, attackSpeed;
    public double health, defense, speed, intelligence, ferocity, magicFind;
    
    // Ability
    public String abilityName = "";
    public String abilityDesc = "";
    public double manaCost = 0;
    
    // Misc
    public boolean isDungeon = false;
    public boolean isEnchanted = false; // For Enchanted Cobblestone glow

    public SkyblockItem(String id, String name, Item mat, String rarity, String type) {
        this.id = id;
        this.displayName = name;
        this.material = mat;
        this.rarity = rarity;
        this.type = type;
    }

    public ItemStack createStack() {
        ItemStack stack = new ItemStack(material);
        
        // Basic NBT
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

        if(isEnchanted) stack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS); 
        // Note: Real glow needs an enchantment added, managed in ModItems logic usually
        
        return stack;
    }
}
