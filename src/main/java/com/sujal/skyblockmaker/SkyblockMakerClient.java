package com.sujal.skyblockmaker;

import com.sujal.skyblockmaker.client.SkyblockHudOverlay; // Import
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback; // Import

// ... baaki imports ...

public class SkyblockMakerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        System.out.println("SkyblockMaker Client Loading...");

        // ... Purana code (Commands wagera) ...

        // === NEW LINE: HUD REGISTER ===
        HudRenderCallback.EVENT.register(new SkyblockHudOverlay());
    }
}
