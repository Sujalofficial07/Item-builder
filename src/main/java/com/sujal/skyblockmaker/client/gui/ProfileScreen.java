package com.sujal.skyblockmaker.client.gui;

import com.sujal.skyblockmaker.api.SkyblockProfileApi;
import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ProfileScreen extends Screen {
    private final PlayerEntity player;

    public ProfileScreen(PlayerEntity player) {
        super(Text.literal("Skyblock Profile"));
        this.player = player;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // 1. Draw Player Model (Left Side)
        InventoryScreen.drawEntity(context, centerX - 100, centerY + 75, 70, (float)(centerX - 100) - mouseX, (float)(centerY) - mouseY, this.player);

        // 2. Draw Title
        context.drawCenteredTextWithShadow(textRenderer, player.getName().getString() + "'s Profile", centerX, centerY - 100, 0xFFD700);

        // 3. Draw Stats List (Right Side)
        int startX = centerX + 20;
        int startY = centerY - 80;
        int gap = 15;

        drawStat(context, "❤ Health", SkyblockStatsApi.StatType.HEALTH, Formatting.RED, startX, startY);
        drawStat(context, "❈ Defense", SkyblockStatsApi.StatType.DEFENSE, Formatting.GREEN, startX, startY + gap);
        drawStat(context, "❁ Strength", SkyblockStatsApi.StatType.STRENGTH, Formatting.RED, startX, startY + gap * 2);
        drawStat(context, "✦ Speed", SkyblockStatsApi.StatType.SPEED, Formatting.WHITE, startX, startY + gap * 3);
        drawStat(context, "☣ Crit Chance", SkyblockStatsApi.StatType.CRIT_CHANCE, Formatting.BLUE, startX, startY + gap * 4);
        drawStat(context, "☠ Crit Damage", SkyblockStatsApi.StatType.CRIT_DAMAGE, Formatting.BLUE, startX, startY + gap * 5);
        drawStat(context, "✎ Intelligence", SkyblockStatsApi.StatType.INTELLIGENCE, Formatting.AQUA, startX, startY + gap * 6);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawStat(DrawContext ctx, String label, SkyblockStatsApi.StatType type, Formatting color, int x, int y) {
        // Note: Client side pe stats sync karne ke liye packets chahiye hote hain. 
        // Abhi ke liye hum approximation use karenge (Items + Base). 
        // Real implementation mein Server -> Client packet sync hota hai.
        // For simplicity, hum assume kar rahe hain client side calculation match karegi.
        
        // Is example mein hum direct calculate kar rahe hain, par ideal way packets hai.
        double val = SkyblockProfileApi.getBaseStat(player, type); // + Item stats logic needed here for display
        // Displaying BASE stat for now to keep it simple
        ctx.drawTextWithShadow(textRenderer, label + ": " + (int)val, x, y, color.getColorValue());
    }
}
