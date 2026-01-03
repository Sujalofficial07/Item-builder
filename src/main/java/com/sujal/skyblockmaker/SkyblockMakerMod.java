package com.sujal.skyblockmaker;

import com.sujal.skyblockmaker.registry.ModPackets;
import net.fabricmc.api.ModInitializer;

public class SkyblockMakerMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // Sirf Server packets register karo
        ModPackets.registerServerPackets();
        
        System.out.println("SkyblockMaker Mod Loaded Successfully!");
    }
}
