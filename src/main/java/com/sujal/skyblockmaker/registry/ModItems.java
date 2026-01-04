package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockItem;
import com.sujal.skyblockmaker.items.Hyperion;
import com.sujal.skyblockmaker.items.Terminator;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModItems {
    
    private static final Map<String, SkyblockItem> REGISTRY = new HashMap<>();

    public static void registerItems() {
        // Register Complex Items
        register(new Hyperion());
        register(new Terminator());

        // Register Simple Items (Now Works!)
        SkyblockItem enchCobble = new SkyblockItem("ENCHANTED_COBBLESTONE", "Enchanted Cobblestone", Items.COBBLESTONE, "COMMON", "MATERIAL");
        enchCobble.isEnchanted = true;
        register(enchCobble);
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
