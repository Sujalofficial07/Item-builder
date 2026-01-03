package com.sujal.skyblockmaker;

import com.sujal.skyblockmaker.registry.ModRegistries;
import net.fabricmc.api.ModInitializer;

public class SkyblockMakerMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Sirf ek line ka call, baaki sab registry file mein
        ModRegistries.registerModStuff();
        
        System.out.println("SkyblockMaker Server Initialized!");
    }
}
