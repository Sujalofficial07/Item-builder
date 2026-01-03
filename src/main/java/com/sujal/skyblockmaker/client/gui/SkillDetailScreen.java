package com.sujal.skyblockmaker.client.gui;

import com.sujal.skyblockmaker.api.SkyblockSkillsApi;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class SkillDetailScreen extends Screen {

    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private final SkyblockSkillsApi.Skill skill;
    private final int guiWidth = 176;
    private final int guiHeight = 222;
    private int guiLeft, guiTop;

    public SkillDetailScreen(SkyblockSkillsApi.Skill skill) {
        super(Text.literal(skill.name() + " Skill"));
        this.skill = skill;
    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - guiWidth) / 2;
        this.guiTop = (this.height - guiHeight) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawTexture(TEXTURE, guiLeft, guiTop, 0, 0, guiWidth, guiHeight);
        context.drawText(textRenderer, capitalize(skill.name()) + " Levels", guiLeft + 8, guiTop + 6, 0x404040, false);

        double currentXp = SkyblockSkillsApi.getXp(client.player, skill);
        int currentLevel = SkyblockSkillsApi.getLevelFromXp(currentXp);

        // Draw 3 Rows of levels (Levels 1-27)
        for (int i = 0; i < 27; i++) {
            int level = i + 1;
            int x = guiLeft + 8 + (i % 9) * 18;
            int y = guiTop + 18 + (i / 9) * 18;

            ItemStack stack;
            if (level <= currentLevel) {
                stack = new ItemStack(Items.LIME_STAINED_GLASS_PANE); // Unlocked
            } else {
                stack = new ItemStack(Items.RED_STAINED_GLASS_PANE); // Locked
            }

            context.drawItem(stack, x, y);

            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                context.fill(x, y, x + 16, y + 16, 0x80FFFFFF);
                
                List<Text> tooltip = new ArrayList<>();
                tooltip.add(Text.literal((level <= currentLevel ? "§a" : "§c") + "Level " + level));
                tooltip.add(Text.literal(""));
                tooltip.add(Text.literal("§7Rewards:"));
                tooltip.add(Text.literal("§e+" + (level * 100) + " Coins"));
                tooltip.add(Text.literal("§bStat Bonuses")); 
                tooltip.add(Text.literal(""));
                tooltip.add(Text.literal(level <= currentLevel ? "§aUNLOCKED" : "§cLOCKED"));
                
                context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
            }
        }

        // Back Button (Row 6, Col 5 - Center)
        // Slot 49 like main screen
        int backX = guiLeft + 8 + 4 * 18; // Col 4 (5th slot)
        int backY = guiTop + 18 + 5 * 18; // Row 5 (6th row)
        context.drawItem(new ItemStack(Items.ARROW), backX, backY);
        
        // Manual check for hover on Arrow
        if (mouseX >= backX && mouseX < backX + 16 && mouseY >= backY && mouseY < backY + 16) {
             context.fill(backX, backY, backX + 16, backY + 16, 0x80FFFFFF);
             context.drawTooltip(textRenderer, Text.literal("§eGo Back"), mouseX, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int backX = guiLeft + 8 + 4 * 18; 
        int backY = guiTop + 18 + 5 * 18; 
        
        if (mouseX >= backX && mouseX < backX + 16 && mouseY >= backY && mouseY < backY + 16) {
             client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
             client.setScreen(new SkillsScreen()); 
             return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private String capitalize(String str) {
        return str.charAt(0) + str.substring(1).toLowerCase();
    }
}
