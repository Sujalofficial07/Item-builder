package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockItemDefaults;
import com.sujal.skyblockmaker.api.SkyblockStatHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;

public class ModRegistries {

    public static void registerModStuff() {
        // 1. Packets Register karo
        ModPackets.registerServerPackets();

        // 2. Commands Register karo
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ProfileCommands.register(dispatcher);
        });

        // 3. Server Ticks (Auto-Convert & Stats Engine)
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            world.getPlayers().forEach(player -> {
                
                // A. Auto Convert Vanilla Items to Skyblock Items
                for (int i = 0; i < player.getInventory().size(); i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (!stack.isEmpty()) {
                        SkyblockItemDefaults.convertToSkyblockItem(stack);
                    }
                }

                // B. Update Real Player Stats (Health/Damage)
                SkyblockStatHandler.updatePlayerStats(player);
            });
        });
    }
}
