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
        context.drawText(textRenderer, capitalize(skill.name()) + " Progression", guiLeft + 8, guiTop + 6, 0x404040, false);

        double currentXp = SkyblockSkillsApi.getXp(client.player, skill);
        int currentLevel = SkyblockSkillsApi.getLevelFromXp(currentXp);

        // Draw Levels 1 to 25 (Simplification: Hypixel has pages, we fit 25 in one grid)
        for (int i = 0; i < 27; i++) { // First 3 rows
            int level = i + 1;
            
            // Determine Item (Green = Unlocked, Red = Locked)
            ItemStack stack;
            boolean unlocked = level <= currentLevel;
            
            if (unlocked) {
                stack = new ItemStack(Items.LIME_STAINED_GLASS_PANE); // Unlocked
            } else {
                stack = new ItemStack(Items.RED_STAINED_GLASS_PANE); // Locked
            }
            
            if (i == 26) { // Level 50 placeholder (Gold Block)
                 // Just keeping it simple loop for now
            }

            // Draw Item
            int x = guiLeft + 8 + (i % 9) * 18;
            int y = guiTop + 18 + (i / 9) * 18;
            context.drawItem(stack, x, y);

            // Hover Tooltip
            if (mx >= x && mx < x + 16 && my >= y && my < y + 16) {
                context.fill(x, y, x + 16, y + 16, 0x80FFFFFF);
                
                List<Text> tooltip = new ArrayList<>();
                tooltip.add(Text.literal((unlocked ? "§a" : "§c") + "Level " + level));
                tooltip.add(Text.literal(""));
                tooltip.add(Text.literal("§7Rewards:"));
                tooltip.add(Text.literal("§e+" + (level * 100) + " Coins"));
                // Stat Reward logic generic
                tooltip.add(Text.literal("§b+Stat Bonus")); 
                tooltip.add(Text.literal(""));
                tooltip.add(Text.literal(unlocked ? "§aUNLOCKED" : "§cLOCKED"));
                
                context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
            }
        }

        // Back Button (Arrow at bottom left)
        int backX = guiLeft + 8;
        int backY = guiTop + guiHeight - 26; // Approx
        context.drawItem(new ItemStack(Items.ARROW), backX, backY); // Slot 45
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    // Variables for mouse hover check inside loop
    private double mx, my;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Update mouse pos for generic checks
        this.mx = mouseX; 
        this.my = mouseY;

        // Check Back Button (Slot 45 approx coords)
        // Slot 45 is at: col 0, row 5 (index starts 0) -> x=8, y=18 + 5*18 = 108
        // Wait, generic_54 has 6 rows.
        // Let's use simpler logic: 
        int backX = guiLeft + 8;
        int backY = guiTop + 18 + 5 * 18; // Row 6, Col 1
        
        if (mouseX >= backX && mouseX < backX + 16 && mouseY >= backY && mouseY < backY + 16) {
             client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
             client.setScreen(new SkillsScreen()); // Go Back
             return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private String capitalize(String str) {
        return str.charAt(0) + str.substring(1).toLowerCase();
    }
}
