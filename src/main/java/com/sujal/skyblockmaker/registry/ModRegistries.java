package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockItemDefaults;
import com.sujal.skyblockmaker.api.SkyblockStatHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModRegistries {

    public static void registerModStuff() {
        ModPackets.registerServerPackets();
        HubProtection.register();

        // Register Commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ProfileCommands.register(dispatcher);
            EconomyCommands.register(dispatcher); // NEW: Economy Cmds
        });

        // Server Loop (Ticks)
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            
            // 1. CLEANUP DAMAGE INDICATORS
            List<Entity> toRemove = new ArrayList<>();
            for (Entity entity : world.getEntitiesByType(net.minecraft.entity.EntityType.ARMOR_STAND, e -> true)) {
                if (entity.getCommandTags().contains("damage_indicator")) {
                    entity.setPosition(entity.getPos().add(0, 0.05, 0));
                    if (entity.age > 20) toRemove.add(entity);
                }
            }
            toRemove.forEach(Entity::discard);

            // 2. Player Updates (Stats & Scoreboard)
            world.getPlayers().forEach(player -> {
                // Auto Convert Logic
                for (int i = 0; i < player.getInventory().size(); i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (!stack.isEmpty()) {
                        SkyblockItemDefaults.convertToSkyblockItem(stack);
                    }
                }
                
                // Real Stats Update
                SkyblockStatHandler.updatePlayerStats(player);
                
                // NEW: Scoreboard Update
                // Performance Note: Har tick update karne se flickering ho sakti hai.
                // Ideal: Har 20 ticks (1 sec) update karo.
                if (world.getTime() % 20 == 0) {
                    SkyblockScoreboardHandler.updateScoreboard(player);
                }
            });
        });
    }
}
