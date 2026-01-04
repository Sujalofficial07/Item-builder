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
        super(Text.literal(skill.name()));
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
        context.drawText(textRenderer, capitalize(skill.name()) + " Skill", guiLeft + 8, guiTop + 6, 0x404040, false);

        double currentXp = SkyblockSkillsApi.getXp(client.player, skill);
        int currentLevel = SkyblockSkillsApi.getLevelFromXp(currentXp);

        // Grid Logic: Hypixel uses a 5-row pattern usually, we will fill rows 0-4
        int totalSlots = 45; // 9x5
        
        for (int i = 0; i < totalSlots; i++) {
            int level = i + 1; // Level 1 to 45 displayed
            int x = guiLeft + 8 + (i % 9) * 18;
            int y = guiTop + 18 + (i / 9) * 18;

            ItemStack stack;
            boolean unlocked = level <= currentLevel;

            if (unlocked) {
                stack = new ItemStack(Items.LIME_STAINED_GLASS_PANE); // Green (Unlocked)
                stack.setCustomName(Text.literal("§aLevel " + level));
            } else {
                stack = new ItemStack(Items.RED_STAINED_GLASS_PANE); // Red (Locked)
                stack.setCustomName(Text.literal("§cLevel " + level));
            }
            
            // Special Item for milestones (e.g. Diamond every 5 levels)
            if (level % 5 == 0) {
                 // You can override stack here if you want items like Diamond for Lvl 5
                 // stack = new ItemStack(Items.DIAMOND); 
            }

            context.drawItem(stack, x, y);

            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                context.fill(x, y, x + 16, y + 16, 0x80FFFFFF);
                renderHypixelTooltip(context, level, unlocked, mouseX, mouseY);
            }
        }

        // Back Button (Row 6, Center)
        int backX = guiLeft + 8 + 4 * 18; 
        int backY = guiTop + 18 + 5 * 18; 
        context.drawItem(new ItemStack(Items.ARROW), backX, backY);

        if (mouseX >= backX && mouseX < backX + 16 && mouseY >= backY && mouseY < backY + 16) {
             context.drawTooltip(textRenderer, Text.literal("§eGo Back"), mouseX, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderHypixelTooltip(DrawContext context, int level, boolean unlocked, int mx, int my) {
        List<Text> tooltip = new ArrayList<>();
        
        // Header
        tooltip.add(Text.literal((unlocked ? "§a" : "§c") + capitalize(skill.name()) + " Level " + toRoman(level)));
        tooltip.add(Text.literal(""));
        
        // Rewards
        tooltip.add(Text.literal("§7Rewards:"));
        tooltip.add(Text.literal("  §e" + capitalize(skill.name()) + " " + toRoman(level)));
        
        // Dynamic Stat Text
        String statName = switch(skill) {
            case COMBAT -> "Crit Chance";
            case MINING -> "Defense";
            case FARMING -> "Health";
            default -> "Stats";
        };
        double statVal = switch(skill) {
            case COMBAT -> 0.5;
            case MINING -> 1;
            case FARMING -> 2;
            default -> 1;
        };
        
        tooltip.add(Text.literal("  §8+" + statVal + " " + statName));
        tooltip.add(Text.literal("  §e+" + (level < 20 ? 100 : 1000) + " Coins"));
        tooltip.add(Text.literal(""));
        
        // Progress / Status
        if (unlocked) {
            tooltip.add(Text.literal("§aUNLOCKED"));
        } else {
            tooltip.add(Text.literal("§cLOCKED"));
            // Calculate XP needed for this specific level
            double needed = SkyblockSkillsApi.getXpForNextLevel(level-1);
            tooltip.add(Text.literal("§8Requires " + (int)needed + " XP"));
        }

        context.drawTooltip(textRenderer, tooltip, mx, my);
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
    
    // Simple Roman Numeral Converter for that Hypixel feel
    private String toRoman(int num) {
        if (num <= 0) return "";
        String[] roman = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", 
                          "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX"};
        if (num <= 20) return roman[num-1];
        return String.valueOf(num);
    }
}
