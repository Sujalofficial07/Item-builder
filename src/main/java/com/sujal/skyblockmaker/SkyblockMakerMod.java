package com.sujal.skyblockmaker;

import com.sujal.skyblockmaker.api.SkyblockStatHandler;
import com.sujal.skyblockmaker.registry.ModPackets;
import com.sujal.skyblockmaker.registry.ProfileCommands; // Import
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class SkyblockMakerMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ModPackets.registerServerPackets();

        // Register Admin Commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ProfileCommands.register(dispatcher);
        });
        
        // Stats Engine
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            world.getPlayers().forEach(SkyblockStatHandler::updatePlayerStats);
        });
    }
}
