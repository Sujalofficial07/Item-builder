package com.sujal.skyblockmaker.client.gui;

import com.sujal.skyblockmaker.api.SkyblockSkillsApi;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class SkillsScreen extends Screen {
    
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private final int guiWidth = 176;
    private final int guiHeight = 222;
    private int guiLeft, guiTop;

    public SkillsScreen() { super(Text.literal("Your Skills")); }

    @Override
    protected void init() {
        this.guiLeft = (this.width - guiWidth) / 2;
        this.guiTop = (this.height - guiHeight) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawTexture(TEXTURE, guiLeft, guiTop, 0, 0, guiWidth, guiHeight);
        context.drawText(textRenderer, "Your Skills", guiLeft + 8, guiTop + 6, 0x404040, false);

        // --- DRAW ICONS BASED ON IMAGE LAYOUT ---
        // Row 2 (Index 10-16 for main skills)
        drawSkillItem(context, Items.STONE_SWORD, SkyblockSkillsApi.Skill.COMBAT, 10, mouseX, mouseY);
        drawSkillItem(context, Items.GOLDEN_HOE, SkyblockSkillsApi.Skill.FARMING, 11, mouseX, mouseY);
        drawSkillItem(context, Items.FISHING_ROD, SkyblockSkillsApi.Skill.FISHING, 12, mouseX, mouseY);
        drawSkillItem(context, Items.DIAMOND_PICKAXE, SkyblockSkillsApi.Skill.MINING, 13, mouseX, mouseY);
        drawSkillItem(context, Items.JUNGLE_SAPLING, SkyblockSkillsApi.Skill.FORAGING, 14, mouseX, mouseY);
        drawSkillItem(context, Items.ENCHANTING_TABLE, SkyblockSkillsApi.Skill.ENCHANTING, 15, mouseX, mouseY);
        drawSkillItem(context, Items.BREWING_STAND, SkyblockSkillsApi.Skill.ALCHEMY, 16, mouseX, mouseY);

        // Close Button (Barrier) at Bottom Center (Slot 49)
        drawItem(context, Items.BARRIER, 49);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawSkillItem(DrawContext context, Item item, SkyblockSkillsApi.Skill skill, int slotIndex, int mx, int my) {
        int x = guiLeft + 8 + (slotIndex % 9) * 18;
        int y = guiTop + 18 + (slotIndex / 9) * 18;

        context.drawItem(new ItemStack(item), x, y);

        if (isHovering(x, y, mx, my)) {
            context.fill(x, y, x + 16, y + 16, 0x80FFFFFF); 
            
            double xp = SkyblockSkillsApi.getXp(client.player, skill);
            int level = SkyblockSkillsApi.getLevelFromXp(xp);
            
            // Progress Calculation
            double xpForCurr = SkyblockSkillsApi.getXpForCurrentLevel(level);
            double xpForNext = SkyblockSkillsApi.getXpForNextLevel(level);
            double progress = (xp - xpForCurr);
            double needed = (xpForNext - xpForCurr);
            int percent = (needed > 0) ? (int)((progress / needed) * 100) : 100;

            List<Text> tooltip = new ArrayList<>();
            tooltip.add(Text.literal("§a" + capitalize(skill.name()) + " Skill"));
            tooltip.add(Text.literal(""));
            tooltip.add(Text.literal("§7Level: §e" + level));
            tooltip.add(Text.literal("§7Progress: §e" + percent + "%"));
            tooltip.add(Text.literal("§7XP: §e" + (int)xp + " / " + (int)xpForNext));
            tooltip.add(Text.literal(""));
            tooltip.add(Text.literal("§eClick to view details!"));
            
            context.drawTooltip(textRenderer, tooltip, mx, my);
        }
    }

    private void drawItem(DrawContext context, Item item, int slotIndex) {
        int x = guiLeft + 8 + (slotIndex % 9) * 18;
        int y = guiTop + 18 + (slotIndex / 9) * 18;
        context.drawItem(new ItemStack(item), x, y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (checkClick(10, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.COMBAT);
        if (checkClick(11, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.FARMING);
        if (checkClick(12, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.FISHING);
        if (checkClick(13, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.MINING);
        if (checkClick(14, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.FORAGING);
        if (checkClick(15, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.ENCHANTING);
        if (checkClick(16, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.ALCHEMY);
        
        if (checkClick(49, mouseX, mouseY)) this.close();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean checkClick(int slot, double mx, double my) {
        int x = guiLeft + 8 + (slot % 9) * 18;
        int y = guiTop + 18 + (slot / 9) * 18;
        return mx >= x && mx < x + 16 && my >= y && my < y + 16;
    }

    private void openDetail(SkyblockSkillsApi.Skill skill) {
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        client.setScreen(new SkillDetailScreen(skill));
    }

    private boolean isHovering(int x, int y, int mx, int my) {
        return mx >= x && mx < x + 16 && my >= y && my < y + 16;
    }
    
    private String capitalize(String str) {
        return str.charAt(0) + str.substring(1).toLowerCase();
    }
}
