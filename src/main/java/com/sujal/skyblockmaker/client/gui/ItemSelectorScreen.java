package com.sujal.skyblockmaker.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemSelectorScreen extends Screen {

    // Generic 54 slot chest texture
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private final int guiWidth = 176;
    private final int guiHeight = 222;
    private int guiLeft, guiTop;

    public ItemSelectorScreen() {
        super(Text.literal("Select Base Item"));
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
        context.drawText(textRenderer, "Select Base Item", guiLeft + 8, guiTop + 6, 0x404040, false);

        // --- DRAW PRESET ITEMS ---
        // Row 1: Weapons
        drawItem(context, Items.DIAMOND_SWORD, 10, "§bDiamond Sword", mouseX, mouseY);
        drawItem(context, Items.IRON_SWORD, 11, "§fIron Sword", mouseX, mouseY);
        drawItem(context, Items.BOW, 12, "§aBow", mouseX, mouseY);
        
        // Row 2: Armor
        drawItem(context, Items.DIAMOND_CHESTPLATE, 19, "§bDiamond Armor", mouseX, mouseY);
        drawItem(context, Items.LEATHER_BOOTS, 20, "§cLeather Armor", mouseX, mouseY);
        
        // Row 3: Tools & Misc
        drawItem(context, Items.DIAMOND_PICKAXE, 28, "§bPickaxe", mouseX, mouseY);
        drawItem(context, Items.GOLDEN_HOE, 29, "§6Hoe", mouseX, mouseY);
        drawItem(context, Items.FISHING_ROD, 30, "§bRod", mouseX, mouseY);

        // HYPERION PRESET (Special Item)
        // Using Iron Sword icon for now, builder will make it look like Hyperion
        drawItem(context, Items.IRON_SWORD, 16, "§d§lHYPERION PRESET", mouseX, mouseY);

        // Close Button
        drawItem(context, Items.BARRIER, 49, "§cClose", mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawItem(DrawContext context, Item item, int slotIndex, String name, int mx, int my) {
        int x = guiLeft + 8 + (slotIndex % 9) * 18;
        int y = guiTop + 18 + (slotIndex / 9) * 18;

        context.drawItem(new ItemStack(item), x, y);

        if (isHovering(x, y, mx, my)) {
            context.fill(x, y, x + 16, y + 16, 0x80FFFFFF);
            context.drawTooltip(textRenderer, Text.literal(name), mx, my);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check clicks and open Builder with specific type
        if (checkClick(10, mouseX, mouseY)) openBuilder("SWORD", Items.DIAMOND_SWORD);
        if (checkClick(11, mouseX, mouseY)) openBuilder("SWORD", Items.IRON_SWORD);
        if (checkClick(12, mouseX, mouseY)) openBuilder("BOW", Items.BOW);
        
        if (checkClick(19, mouseX, mouseY)) openBuilder("ARMOR", Items.DIAMOND_CHESTPLATE);
        if (checkClick(20, mouseX, mouseY)) openBuilder("ARMOR", Items.LEATHER_BOOTS);
        
        if (checkClick(28, mouseX, mouseY)) openBuilder("TOOL", Items.DIAMOND_PICKAXE);
        
        // Hyperion Preset (Sends special flag)
        if (checkClick(16, mouseX, mouseY)) openBuilder("HYPERION", Items.IRON_SWORD);

        if (checkClick(49, mouseX, mouseY)) this.close();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void openBuilder(String type, Item baseItem) {
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        // Next Step: Open the ItemBuilderScreen
        // I will provide ItemBuilderScreen in the NEXT reply.
        // For now, let's close to avoid crash until you have the next file.
        client.setScreen(new ItemBuilderScreen(type, baseItem)); 
        this.close(); // Placeholder
        client.player.sendMessage(Text.literal("Selected: " + type + ". Builder coming in next update!"), false);
    }

    private boolean checkClick(int slot, double mx, double my) {
        int x = guiLeft + 8 + (slot % 9) * 18;
        int y = guiTop + 18 + (slot / 9) * 18;
        return mx >= x && mx < x + 16 && my >= y && my < y + 16;
    }

    private boolean isHovering(int x, int y, int mx, int my) {
        return mx >= x && mx < x + 16 && my >= y && my < y + 16;
    }
}
