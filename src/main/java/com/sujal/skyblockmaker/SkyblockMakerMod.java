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
        
        // Auto Converter Loop
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            world.getPlayers().forEach(player -> {
                for (int i = 0; i < player.getInventory().size(); i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (!stack.isEmpty()) {
                        SkyblockItemDefaults.convertToSkyblockItem(stack);
                    }
                }
            });
        });
        System.out.println("Hypixel Skyblock Engine Started!");
    }
}
