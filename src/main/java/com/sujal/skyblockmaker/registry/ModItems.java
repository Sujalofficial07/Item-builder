package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockItem;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModItems {
    public static final List<SkyblockItem> ITEMS = new ArrayList<>();

    public static void registerItems() {
        // --- SWORDS ---
        SkyblockItem hyperion = new SkyblockItem("HYPERION", "Hyperion", Items.IRON_SWORD, "LEGENDARY", "SWORD");
        hyperion.damage = 260;
        hyperion.strength = 150;
        hyperion.intelligence = 350;
        hyperion.ferocity = 30;
        hyperion.isDungeon = true;
        hyperion.abilityName = "Wither Impact";
        hyperion.abilityDesc = "Teleports 10 blocks ahead and deals damage to nearby enemies.";
        hyperion.manaCost = 250;
        ITEMS.add(hyperion);

        SkyblockItem aotd = new SkyblockItem("AOTD", "Aspect of the Dragons", Items.DIAMOND_SWORD, "LEGENDARY", "SWORD");
        aotd.damage = 225;
        aotd.strength = 100;
        aotd.abilityName = "Dragon Rage";
        aotd.abilityDesc = "Deals 12,000 damage to mobs in front of you.";
        aotd.manaCost = 100;
        ITEMS.add(aotd);

        // --- BOWS ---
        SkyblockItem terminator = new SkyblockItem("TERMINATOR", "Terminator", Items.BOW, "LEGENDARY", "BOW");
        terminator.damage = 310;
        terminator.strength = 50;
        terminator.critDamage = 250;
        terminator.attackSpeed = 40;
        terminator.isDungeon = true;
        terminator.abilityName = "Salvation";
        terminator.abilityDesc = "Shoots 3 arrows at once. Can hit endermen.";
        ITEMS.add(terminator);

        // --- MATERIALS ---
        SkyblockItem enchCobble = new SkyblockItem("ENCHANTED_COBBLESTONE", "Enchanted Cobblestone", Items.COBBLESTONE, "COMMON", "MATERIAL");
        enchCobble.isEnchanted = true; // Needs glow logic (usually adding Unbreaking I + hide flags)
        ITEMS.add(enchCobble);
        
        SkyblockItem enchDiamond = new SkyblockItem("ENCHANTED_DIAMOND", "Enchanted Diamond", Items.DIAMOND, "RARE", "MATERIAL");
        enchDiamond.isEnchanted = true;
        ITEMS.add(enchDiamond);
    }

    public static List<SkyblockItem> getByCategory(String category) {
        return ITEMS.stream().filter(i -> i.type.equals(category)).collect(Collectors.toList());
    }
}
