package com.sujal.skyblockmaker.client;

import com.sujal.skyblockmaker.api.SkyblockPlayerStats;
import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

public class SkyblockHudOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        if (client.options.debugEnabled) return; // F3 menu check

        PlayerEntity player = client.player;
        TextRenderer textRenderer = client.textRenderer;

        // Stats Calculation
        int currentHp = (int) player.getHealth() * 5 + (int) SkyblockPlayerStats.getTotalStat(player, SkyblockStatsApi.StatType.HEALTH);
        int maxHp = (int) SkyblockPlayerStats.getTotalStat(player, SkyblockStatsApi.StatType.HEALTH);
        if (currentHp > maxHp) currentHp = maxHp;

        int defense = (int) SkyblockPlayerStats.getTotalStat(player, SkyblockStatsApi.StatType.DEFENSE);
        int maxMana = (int) SkyblockPlayerStats.getTotalStat(player, SkyblockStatsApi.StatType.INTELLIGENCE);
        int currentMana = maxMana; 

        // Text Creation
        // Positioning
        // ... imports same ...

// Inside onHudRender:
        
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        
        // POSITION UPDATE: Height - 60 (Thoda upar taki hotbar se na takraye)
        int yPos = height - 60; 

        // Text Creation (Colors are already handled in drawing)
        String healthText = "❤ " + currentHp + "/" + maxHp;
        String defenseText = "❈ " + defense + " Defense";
        String manaText = "✎ " + currentMana + "/" + maxMana + " Mana";

        // Draw Health (RED)
        context.drawTextWithShadow(textRenderer, healthText, width / 2 - 120, yPos, 0xFF5555);

        // Draw Defense (GREEN)
        int defenseWidth = textRenderer.getWidth(defenseText);
        context.drawTextWithShadow(textRenderer, defenseText, width / 2 - (defenseWidth / 2), yPos, 0x55FF55);

        // Draw Mana (AQUA)
        context.drawTextWithShadow(textRenderer, manaText, width / 2 + 60, yPos, 0x55FFFF);
    }
}
