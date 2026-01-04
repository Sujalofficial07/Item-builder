package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockItem;
import com.sujal.skyblockmaker.items.AspectOfTheEnd; // New
import com.sujal.skyblockmaker.items.GiantSword; // New
import com.sujal.skyblockmaker.items.Hyperion; // Custom Class
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModItems {
    
    private static final Map<String, SkyblockItem> REGISTRY = new HashMap<>();

    public static void registerItems() {
        // Clear previous to avoid dupes on reload
        REGISTRY.clear();

        // 1. Custom Ability Items (Inki alag files hain)
        register(new Hyperion());
        register(new AspectOfTheEnd());
        register(new GiantSword());

        // 2. Simple Items (Direct registration)
        SkyblockItem enchCobble = new SkyblockItem("ENCHANTED_COBBLESTONE", "Enchanted Cobblestone", Items.COBBLESTONE, "COMMON", "MATERIAL");
        enchCobble.isEnchanted = true;
        register(enchCobble);
        
        SkyblockItem enchDiamond = new SkyblockItem("ENCHANTED_DIAMOND", "Enchanted Diamond", Items.DIAMOND, "RARE", "MATERIAL");
        enchDiamond.isEnchanted = true;
        register(enchDiamond);
    }

    private static void register(SkyblockItem item) {
        REGISTRY.put(item.id, item);
    }

    public static SkyblockItem get(String id) {
        return REGISTRY.get(id);
    }

    public static List<SkyblockItem> getByCategory(String category) {
        return REGISTRY.values().stream()
                .filter(i -> i.type.equals(category))
                .collect(Collectors.toList());
    }
}
