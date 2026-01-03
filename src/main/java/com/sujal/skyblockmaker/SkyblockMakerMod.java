package com.sujal.skyblockmaker;

import com.sujal.skyblockmaker.api.SkyblockItemDefaults;
import com.sujal.skyblockmaker.registry.ModPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;

public class SkyblockMakerMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ModPackets.registerServerPackets();
        
        // === AUTO CONVERTER SYSTEM ===
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            // World ke saare players ko scan karo
            world.getPlayers().forEach(player -> {
                
                // Inventory ka har slot check karo
                for (int i = 0; i < player.getInventory().size(); i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    
                    if (!stack.isEmpty()) {
                        // Agar item plain vanilla hai, toh Skyblock stats de do
                        SkyblockItemDefaults.convertToSkyblockItem(stack);
                    }
                }
            });
        });

        System.out.println("SkyblockMaker: Auto-Converter & Server Loaded!");
    }
}
