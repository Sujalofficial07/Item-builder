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
        String healthText = "❤ " + currentHp + "/" + maxHp;
        String defenseText = "❈ " + defense;
        String manaText = "✎ " + currentMana + "/" + maxMana;

        // Positioning
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        int yPos = height - 40; 

        // Draw
        context.drawTextWithShadow(textRenderer, healthText, width / 2 - 90, yPos, Formatting.RED.getColorValue());
        int defenseWidth = textRenderer.getWidth(defenseText);
        context.drawTextWithShadow(textRenderer, defenseText, width / 2 - (defenseWidth / 2), yPos, Formatting.GREEN.getColorValue());
        context.drawTextWithShadow(textRenderer, manaText, width / 2 + 50, yPos, Formatting.AQUA.getColorValue());
    }
}
