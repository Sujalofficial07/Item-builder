package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockItemDefaults;
import com.sujal.skyblockmaker.api.SkyblockStatHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity; // Changed Import
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModRegistries {

    public static void registerModStuff() {
        ModPackets.registerServerPackets();
        HubProtection.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ProfileCommands.register(dispatcher);
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            
            // 1. CLEANUP DAMAGE INDICATORS (Armor Stands)
            List<Entity> toRemove = new ArrayList<>();
            // Loop through Armor Stands
            for (Entity entity : world.getEntitiesByType(net.minecraft.entity.EntityType.ARMOR_STAND, e -> true)) {
                // Check for our specific tag
                if (entity.getScoreboardTags().contains("damage_indicator")) {
                    
                    // Float Up
                    entity.setPosition(entity.getPos().add(0, 0.05, 0));
                    
                    // Kill after 20 ticks (1 second)
                    if (entity.age > 20) { 
                        toRemove.add(entity);
                    }
                }
            }
            toRemove.forEach(Entity::discard);

            // 2. Player Stats Update
            world.getPlayers().forEach(player -> {
                for (int i = 0; i < player.getInventory().size(); i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (!stack.isEmpty()) {
                        SkyblockItemDefaults.convertToSkyblockItem(stack);
                    }
                }
                SkyblockStatHandler.updatePlayerStats(player);
            });
        });
    }
}
