package com.sujal.skyblockmaker;

import com.sujal.skyblockmaker.registry.ClientRegistries;
import net.fabricmc.api.ClientModInitializer;

public class SkyblockMakerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Sirf ek line ka call
        ClientRegistries.registerClientStuff();
        
        System.out.println("SkyblockMaker Client Initialized!");
    }
}
