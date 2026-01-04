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

        // Fill background with Gray Glass
        for(int i=0; i<54; i++) {
             if (!isSkillSlot(i) && i != 49) {
                 context.drawItem(new ItemStack(Items.GRAY_STAINED_GLASS_PANE), guiLeft + 8 + (i%9)*18, guiTop + 18 + (i/9)*18);
             }
        }

        // --- SKILL ICONS (Matches Video Layout) ---
        drawSkillItem(context, Items.STONE_SWORD, SkyblockSkillsApi.Skill.COMBAT, 19, mouseX, mouseY);
        drawSkillItem(context, Items.GOLDEN_HOE, SkyblockSkillsApi.Skill.FARMING, 20, mouseX, mouseY);
        drawSkillItem(context, Items.FISHING_ROD, SkyblockSkillsApi.Skill.FISHING, 21, mouseX, mouseY);
        drawSkillItem(context, Items.DIAMOND_PICKAXE, SkyblockSkillsApi.Skill.MINING, 22, mouseX, mouseY);
        drawSkillItem(context, Items.JUNGLE_SAPLING, SkyblockSkillsApi.Skill.FORAGING, 23, mouseX, mouseY);
        drawSkillItem(context, Items.ENCHANTING_TABLE, SkyblockSkillsApi.Skill.ENCHANTING, 24, mouseX, mouseY);
        drawSkillItem(context, Items.BREWING_STAND, SkyblockSkillsApi.Skill.ALCHEMY, 25, mouseX, mouseY);

        // Extra Cosmetic Skills
        drawItem(context, Items.CRAFTING_TABLE, 28); 
        drawItem(context, Items.MAGMA_CREAM, 29); 
        drawItem(context, Items.EMERALD, 30); 
        drawSkillItem(context, Items.CREEPER_SPAWN_EGG, SkyblockSkillsApi.Skill.TAMING, 31, mouseX, mouseY); 
        drawItem(context, Items.WITHER_SKELETON_SKULL, 32); 

        // Close Button
        drawItem(context, Items.BARRIER, 49);

        super.render(context, mouseX, mouseY, delta);
    }

    private boolean isSkillSlot(int i) {
        return (i >= 19 && i <= 25) || (i >= 28 && i <= 32);
    }

    private void drawSkillItem(DrawContext context, Item item, SkyblockSkillsApi.Skill skill, int slotIndex, int mx, int my) {
        int x = guiLeft + 8 + (slotIndex % 9) * 18;
        int y = guiTop + 18 + (slotIndex / 9) * 18;

        context.drawItem(new ItemStack(item), x, y);

        if (isHovering(x, y, mx, my)) {
            context.fill(x, y, x + 16, y + 16, 0x80FFFFFF);
            
            double xp = SkyblockSkillsApi.getXp(client.player, skill);
            int level = SkyblockSkillsApi.getLevelFromXp(xp);
            
            List<Text> tooltip = new ArrayList<>();
            tooltip.add(Text.literal("§a" + capitalize(skill.name()) + " Skill"));
            tooltip.add(Text.literal("§7Level: §e" + level));
            tooltip.add(Text.literal(""));
            tooltip.add(Text.literal("§eClick to view!"));
            
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
        if (checkClick(19, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.COMBAT);
        if (checkClick(20, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.FARMING);
        if (checkClick(21, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.FISHING);
        if (checkClick(22, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.MINING);
        if (checkClick(23, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.FORAGING);
        if (checkClick(24, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.ENCHANTING);
        if (checkClick(25, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.ALCHEMY);
        if (checkClick(31, mouseX, mouseY)) openDetail(SkyblockSkillsApi.Skill.TAMING);
        
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
