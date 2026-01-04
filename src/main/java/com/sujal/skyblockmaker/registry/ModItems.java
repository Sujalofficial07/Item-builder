package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockItem;
import net.minecraft.item.Items;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModItems {
    public static final List<SkyblockItem> ITEMS = new ArrayList<>();

    public static void registerItems() {
        // --- DUNGEON WEAPONS ---
        SkyblockItem hyperion = new SkyblockItem("HYPERION", "Hyperion", Items.IRON_SWORD, "LEGENDARY", "SWORD");
        hyperion.damage = 260; hyperion.strength = 150; hyperion.intelligence = 350; hyperion.ferocity = 30;
        hyperion.isDungeon = true;
        hyperion.abilityName = "Wither Impact";
        hyperion.abilityDesc = "Teleports 10 blocks ahead and deals 10,000 damage.";
        hyperion.manaCost = 250;
        ITEMS.add(hyperion);

        SkyblockItem lividDagger = new SkyblockItem("LIVID_DAGGER", "Livid Dagger", Items.IRON_SWORD, "LEGENDARY", "SWORD");
        lividDagger.damage = 210; lividDagger.strength = 60; lividDagger.critChance = 100; lividDagger.attackSpeed = 100;
        lividDagger.isDungeon = true;
        lividDagger.abilityName = "Throw";
        lividDagger.abilityDesc = "Throw your dagger to deal damage!";
        lividDagger.manaCost = 150;
        ITEMS.add(lividDagger);

        SkyblockItem juju = new SkyblockItem("JUJU_SHORTBOW", "Juju Shortbow", Items.BOW, "EPIC", "BOW");
        juju.damage = 310; juju.strength = 40; juju.critChance = 10; juju.critDamage = 110;
        juju.isDungeon = true;
        juju.abilityDesc = "Hits 3 mobs instantly.";
        ITEMS.add(juju);

        // --- DUNGEON ARMOR (Necron) ---
        addArmorSet("NECRON", "Necron's", "LEGENDARY", 40, 250, 80, 30);
        addArmorSet("STORM", "Storm's", "LEGENDARY", 20, 150, 40, 250); // High Intel
        addArmorSet("SHADOW_ASSASSIN", "Shadow Assassin", "EPIC", 30, 200, 50, 10);

        // --- MATERIALS (Enchanted) ---
        addMaterial("ENCHANTED_COBBLESTONE", "Enchanted Cobblestone", Items.COBBLESTONE, "COMMON");
        addMaterial("ENCHANTED_OBSIDIAN", "Enchanted Obsidian", Items.OBSIDIAN, "UNCOMMON");
        addMaterial("ENCHANTED_DIAMOND_BLOCK", "Enchanted Diamond Block", Items.DIAMOND_BLOCK, "RARE");
        addMaterial("NECRON_HANDLE", "Necron's Handle", Items.STICK, "LEGENDARY");
    }

    private static void addArmorSet(String idBase, String nameBase, String rarity, double str, double def, double cd, double intel) {
        // Helmet
        SkyblockItem helm = new SkyblockItem(idBase + "_HELMET", nameBase + " Helmet", Items.PLAYER_HEAD, rarity, "ARMOR");
        helm.strength = str; helm.defense = def; helm.critDamage = cd; helm.intelligence = intel; helm.health = 100; helm.isDungeon = true;
        ITEMS.add(helm);
        
        // Chestplate
        SkyblockItem chest = new SkyblockItem(idBase + "_CHESTPLATE", nameBase + " Chestplate", Items.LEATHER_CHESTPLATE, rarity, "ARMOR");
        chest.strength = str+10; chest.defense = def+40; chest.critDamage = cd; chest.intelligence = intel; chest.health = 160; chest.isDungeon = true;
        ITEMS.add(chest);
    }

    private static void addMaterial(String id, String name, net.minecraft.item.Item mat, String rarity) {
        SkyblockItem item = new SkyblockItem(id, name, mat, rarity, "MATERIAL");
        item.isEnchanted = true;
        ITEMS.add(item);
    }

    public static List<SkyblockItem> getByCategory(String category) {
        return ITEMS.stream().filter(i -> i.type.equals(category)).collect(Collectors.toList());
    }
}
