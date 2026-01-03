package com.sujal.skyblockmaker;

import com.sujal.skyblockmaker.registry.ModPackets;
import net.fabricmc.api.ModInitializer;

public class SkyblockMakerMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ModPackets.registerServerPackets(); // Packets load karna zaruri hai
        System.out.println("SkyblockMaker Server Side Loaded!");
    }
}
