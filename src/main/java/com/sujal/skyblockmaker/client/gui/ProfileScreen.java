package com.sujal.skyblockmaker.client.gui;

import com.sujal.skyblockmaker.api.SkyblockProfileApi;
import com.sujal.skyblockmaker.api.SkyblockSkillsApi; // Import
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

        // Player Model
        InventoryScreen.drawEntity(context, centerX - 100, centerY + 75, 70, (float)(centerX - 100) - mouseX, (float)(centerY) - mouseY, this.player);

        // Title
        context.drawCenteredTextWithShadow(textRenderer, player.getName().getString() + "'s Profile", centerX, centerY - 100, 0xFFD700);

        // Stats (Left Side)
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

        // Skills (Right Side of Stats)
        int skillX = startX + 120;
        int skillY = startY;

        context.drawTextWithShadow(textRenderer, "§6§lSKILLS", skillX, skillY - 15, 0xFFFFFF);
        
        drawSkill(context, "Farming", SkyblockSkillsApi.Skill.FARMING, skillX, skillY);
        drawSkill(context, "Mining", SkyblockSkillsApi.Skill.MINING, skillX, skillY + gap);
        drawSkill(context, "Combat", SkyblockSkillsApi.Skill.COMBAT, skillX, skillY + gap * 2);
        drawSkill(context, "Foraging", SkyblockSkillsApi.Skill.FORAGING, skillX, skillY + gap * 3);
        drawSkill(context, "Fishing", SkyblockSkillsApi.Skill.FISHING, skillX, skillY + gap * 4);
        drawSkill(context, "Enchanting", SkyblockSkillsApi.Skill.ENCHANTING, skillX, skillY + gap * 5);
        drawSkill(context, "Alchemy", SkyblockSkillsApi.Skill.ALCHEMY, skillX, skillY + gap * 6);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawStat(DrawContext ctx, String label, SkyblockStatsApi.StatType type, Formatting color, int x, int y) {
        double val = SkyblockProfileApi.getBaseStat(player, type); 
        // For accurate client display, syncing packets are ideal, but for now showing Base Stat
        ctx.drawTextWithShadow(textRenderer, label + ": " + (int)val, x, y, color.getColorValue());
    }

    private void drawSkill(DrawContext ctx, String name, SkyblockSkillsApi.Skill skill, int x, int y) {
        double xp = SkyblockSkillsApi.getXp(player, skill);
        int level = SkyblockSkillsApi.getLevelFromXp(xp);
        String text = name + " " + level;
        ctx.drawTextWithShadow(textRenderer, text, x, y, Formatting.YELLOW.getColorValue());
    }
}
