package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockItemDefaults;
import com.sujal.skyblockmaker.api.SkyblockStatHandler;
import com.sujal.skyblockmaker.api.SkyblockScoreboardHandler; // Assuming this exists from previous steps
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModRegistries {

    public static void registerModStuff() {
        ModPackets.registerServerPackets();
        
        // Register Items and Listeners
        ModItems.registerItems(); 
        AbilityListener.register();
        SkillListener.register();
        HubProtection.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ProfileCommands.register(dispatcher);
            EconomyCommands.register(dispatcher);
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            
            // Cleanup Damage Indicators
            List<Entity> toRemove = new ArrayList<>();
            for (Entity entity : world.getEntitiesByType(net.minecraft.entity.EntityType.ARMOR_STAND, e -> true)) {
                if (entity.getCommandTags().contains("damage_indicator")) {
                    entity.setPosition(entity.getPos().add(0, 0.05, 0));
                    if (entity.age > 20) toRemove.add(entity);
                }
            }
            toRemove.forEach(Entity::discard);

            // Player Updates
            world.getPlayers().forEach(player -> {
                for (int i = 0; i < player.getInventory().size(); i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (!stack.isEmpty()) {
                        SkyblockItemDefaults.convertToSkyblockItem(stack);
                    }
                }
                
                SkyblockStatHandler.updatePlayerStats(player);
                
                if (world.getTime() % 20 == 0) {
                    SkyblockScoreboardHandler.updateScoreboard(player);
                }
            });
        });
    }
}
