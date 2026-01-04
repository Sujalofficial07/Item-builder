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

import java.text.DecimalFormat;
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

        // Draw 5 Rows (Levels 1-45)
        for (int i = 0; i < 45; i++) {
            int level = i + 1;
            int x = guiLeft + 8 + (i % 9) * 18;
            int y = guiTop + 18 + (i / 9) * 18;

            ItemStack stack;
            boolean unlocked = level <= currentLevel;
            
            // Hypixel Colors: Green (Unlocked), Yellow (In Progress), Red (Locked)
            if (unlocked) {
                stack = new ItemStack(Items.LIME_STAINED_GLASS_PANE);
            } else if (level == currentLevel + 1) {
                stack = new ItemStack(Items.YELLOW_STAINED_GLASS_PANE);
            } else {
                stack = new ItemStack(Items.RED_STAINED_GLASS_PANE);
            }

            context.drawItem(stack, x, y);

            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                context.fill(x, y, x + 16, y + 16, 0x80FFFFFF);
                renderHypixelTooltip(context, level, currentLevel, currentXp, mouseX, mouseY);
            }
        }

        // Back Button
        int backX = guiLeft + 8 + 4 * 18; 
        int backY = guiTop + 18 + 5 * 18; 
        context.drawItem(new ItemStack(Items.ARROW), backX, backY);

        if (mouseX >= backX && mouseX < backX + 16 && mouseY >= backY && mouseY < backY + 16) {
             context.drawTooltip(textRenderer, Text.literal("§eGo Back"), mouseX, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderHypixelTooltip(DrawContext context, int level, int currentLevel, double totalXp, int mx, int my) {
        List<Text> tooltip = new ArrayList<>();
        boolean unlocked = level <= currentLevel;
        boolean inProgress = level == currentLevel + 1;

        // Header
        tooltip.add(Text.literal((unlocked ? "§a" : (inProgress ? "§e" : "§c")) + capitalize(skill.name()) + " Level " + toRoman(level)));
        tooltip.add(Text.literal(""));
        
        // Rewards
        tooltip.add(Text.literal("§7Rewards:"));
        
        // Dynamic Stat Text
        String statName = switch(skill) {
            case COMBAT -> "Crit Chance";
            case MINING -> "Defense";
            case FARMING -> "Health";
            case FORAGING -> "Strength";
            default -> "Intelligence";
        };
        double statVal = switch(skill) {
            case COMBAT -> 0.5;
            case MINING -> 1;
            case FARMING -> 2;
            case FORAGING -> 1;
            default -> 1;
        };
        tooltip.add(Text.literal("  §b+" + statVal + " " + statName));
        tooltip.add(Text.literal("  §e+" + (level < 20 ? 100 : 1000) + " Coins"));
        tooltip.add(Text.literal(""));

        // Progress Bar (Matches 3rd Photo)
        if (inProgress) {
            double xpPrev = SkyblockSkillsApi.getXpForLevel(level-1);
            double xpTarget = SkyblockSkillsApi.getXpForLevel(level);
            double neededForLevel = xpTarget - xpPrev;
            double currentInLevel = totalXp - xpPrev;
            
            double percent = (currentInLevel / neededForLevel) * 100.0;
            percent = Math.min(100, Math.max(0, percent));
            
            DecimalFormat df = new DecimalFormat("#.1");
            tooltip.add(Text.literal("§7Progress to Level " + level + ": §e" + df.format(percent) + "%"));
            
            // Draw Bar: "--------------------"
            StringBuilder bar = new StringBuilder();
            int greenDashes = (int)(percent / 5); 
            bar.append("§2");
            for(int k=0; k<greenDashes; k++) bar.append("-");
            bar.append("§f");
            for(int k=greenDashes; k<20; k++) bar.append("-");
            
            tooltip.add(Text.literal(bar.toString() + " §e" + (int)currentInLevel + " / " + formatK(neededForLevel)));
        } 
        else if (unlocked) {
            tooltip.add(Text.literal("§aUNLOCKED"));
        } else {
            tooltip.add(Text.literal("§cLOCKED"));
        }

        context.drawTooltip(textRenderer, tooltip, mx, my);
    }
    
    private String formatK(double val) {
        if (val >= 1000) return new DecimalFormat("#.1").format(val/1000) + "k";
        return String.valueOf((int)val);
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
    
    private String toRoman(int num) {
        if (num <= 0) return "";
        String[] roman = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", 
                          "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX",
                          "XXI", "XXII", "XXIII", "XXIV", "XXV", "XXVI", "XXVII", "XXVIII", "XXIX", "XXX",
                          "XXXI", "XXXII", "XXXIII", "XXXIV", "XXXV", "XXXVI", "XXXVII", "XXXVIII", "XXXIX", "XL",
                          "XLI", "XLII", "XLIII", "XLIV", "XLV"};
        if (num <= 45) return roman[num-1];
        return String.valueOf(num);
    }
}
