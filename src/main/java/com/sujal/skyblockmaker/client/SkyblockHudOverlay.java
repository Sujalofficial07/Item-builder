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
        
        // Agar F3 menu khula hai toh mat dikhao
        if (client.options.debugEnabled) return;

        PlayerEntity player = client.player;
        TextRenderer textRenderer = client.textRenderer;

        // === 1. Stats Calculate Karo ===
        // Health
        int currentHp = (int) player.getHealth() * 5 + (int) SkyblockPlayerStats.getTotalStat(player, SkyblockStatsApi.StatType.HEALTH); 
        // Note: Vanilla health (20) ko 100 scale karne ke liye *5 kiya logic adjust kar sakte ho
        int maxHp = (int) SkyblockPlayerStats.getTotalStat(player, SkyblockStatsApi.StatType.HEALTH);
        if (currentHp > maxHp) currentHp = maxHp; // Cap logic

        // Defense
        int defense = (int) SkyblockPlayerStats.getTotalStat(player, SkyblockStatsApi.StatType.DEFENSE);

        // Mana
        int maxMana = (int) SkyblockPlayerStats.getTotalStat(player, SkyblockStatsApi.StatType.INTELLIGENCE);
        int currentMana = maxMana; // Abhi ke liye full mana

        // === 2. Strings (Text) Banao ===
        String healthText = "❤ " + currentHp + "/" + maxHp;
        String defenseText = "❈ " + defense;
        String manaText = "✎ " + currentMana + "/" + maxMana;

        // === 3. Positioning (Center of Screen, just above Hotbar) ===
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        
        // Hotbar ke thoda upar (Y coordinate)
        int yPos = height - 40; 

        // === 4. Draw Text ===
        
        // --- Draw Health (RED) ---
        // Left side thoda shift karke
        context.drawTextWithShadow(textRenderer, healthText, width / 2 - 90, yPos, Formatting.RED.getColorValue());

        // --- Draw Defense (GREEN) ---
        // Bilkul Center mein
        int defenseWidth = textRenderer.getWidth(defenseText);
        context.drawTextWithShadow(textRenderer, defenseText, width / 2 - (defenseWidth / 2), yPos, Formatting.GREEN.getColorValue());

        // --- Draw Mana (AQUA/BLUE) ---
        // Right side thoda shift karke
        context.drawTextWithShadow(textRenderer, manaText, width / 2 + 50, yPos, Formatting.AQUA.getColorValue());
    }
}
