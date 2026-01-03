package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockItemDefaults;
import com.sujal.skyblockmaker.api.SkyblockStatHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class ModRegistries {

    public static void registerModStuff() {
        ModPackets.registerServerPackets();
        HubProtection.register(); // Enable Protection

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ProfileCommands.register(dispatcher);
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            
            // 1. DAMAGE INDICATOR ANIMATION & CLEANUP
            List<Entity> toRemove = new ArrayList<>();
            for (Entity entity : world.getEntitiesByType(net.minecraft.entity.EntityType.TEXT_DISPLAY, e -> true)) {
                // Check if it's our indicator (Glowing logic used as marker)
                if (entity.isGlowing()) {
                    // Float Up
                    entity.setPosition(entity.getPos().add(0, 0.05, 0));
                    
                    // Age logic (Simple timestamp check or manual tick count)
                    if (entity.age > 20) { // 1 second baad gayab
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
