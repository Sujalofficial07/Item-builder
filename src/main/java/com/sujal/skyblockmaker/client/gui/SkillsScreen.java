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
    
    // Generic 54 slot texture
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

        // Slots Logic: 
        // Row 0: 0-8
        // Row 1: 9-17
        // Row 2: 18-26 (We want 19, 20, 21, 22, 23, 24, 25)
        
        // Row 2 (Index 19 starts at Col 1)
        drawSkillItem(context, Items.STONE_SWORD, SkyblockSkillsApi.Skill.COMBAT, 19, mouseX, mouseY);
        drawSkillItem(context, Items.DIAMOND_PICKAXE, SkyblockSkillsApi.Skill.MINING, 20, mouseX, mouseY);
        drawSkillItem(context, Items.GOLDEN_HOE, SkyblockSkillsApi.Skill.FARMING, 21, mouseX, mouseY);
        drawSkillItem(context, Items.JUNGLE_SAPLING, SkyblockSkillsApi.Skill.FORAGING, 22, mouseX, mouseY);
        drawSkillItem(context, Items.FISHING_ROD, SkyblockSkillsApi.Skill.FISHING, 23, mouseX, mouseY);
        drawSkillItem(context, Items.ENCHANTING_TABLE, SkyblockSkillsApi.Skill.ENCHANTING, 24, mouseX, mouseY);
        drawSkillItem(context, Items.BREWING_STAND, SkyblockSkillsApi.Skill.ALCHEMY, 25, mouseX, mouseY);

        // Close Button (Slot 49 = Row 5, Col 4)
        drawItem(context, Items.BARRIER, 49);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawSkillItem(DrawContext context, Item item, SkyblockSkillsApi.Skill skill, int slotIndex, int mx, int my) {
        int col = slotIndex % 9;
        int row = slotIndex / 9;
        int x = guiLeft + 8 + col * 18;
        int y = guiTop + 18 + row * 18;

        context.drawItem(new ItemStack(item), x, y);

        if (isHovering(x, y, mx, my)) {
            context.fill(x, y, x + 16, y + 16, 0x80FFFFFF); 
            
            List<Text> tooltip = new ArrayList<>();
            double xp = SkyblockSkillsApi.getXp(client.player, skill);
            int level = SkyblockSkillsApi.getLevelFromXp(xp);
            
            tooltip.add(Text.literal("§a" + capitalize(skill.name()) + " Skill"));
            tooltip.add(Text.literal("§7Level: §e" + level));
            tooltip.add(Text.literal("§7XP: §e" + (int)xp));
            tooltip.add(Text.literal(""));
            tooltip.add(Text.literal("§eClick to view rewards!"));
            
            context.drawTooltip(textRenderer, tooltip, mx, my);
        }
    }
    
    private void drawItem(DrawContext context, Item item, int slotIndex) {
        int col = slotIndex % 9;
        int row = slotIndex / 9;
        int x = guiLeft + 8 + col * 18;
        int y = guiTop + 18 + row * 18;
        context.drawItem(new ItemStack(item), x, y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (checkClick(19, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.COMBAT);
        if (checkClick(20, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.MINING);
        if (checkClick(21, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.FARMING);
        if (checkClick(22, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.FORAGING);
        if (checkClick(23, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.FISHING);
        if (checkClick(24, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.ENCHANTING);
        if (checkClick(25, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.ALCHEMY);
        
        if (checkClick(49, mouseX, mouseY)) this.close();

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean checkClick(int slot, double mx, double my) {
        int col = slot % 9;
        int row = slot / 9;
        int x = guiLeft + 8 + col * 18;
        int y = guiTop + 18 + row * 18;
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
