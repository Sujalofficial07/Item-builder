package com.sujal.skyblockmaker.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CategorySelectionScreen extends Screen {

    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private int guiLeft, guiTop;

    public CategorySelectionScreen() { super(Text.literal("Select Category")); }

    @Override
    protected void init() {
        this.guiLeft = (this.width - 176) / 2;
        this.guiTop = (this.height - 222) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        context.drawTexture(TEXTURE, guiLeft, guiTop, 0, 0, 176, 222);
        context.drawText(textRenderer, "Skyblock Item Menu", guiLeft + 8, guiTop + 6, 0x404040, false);

        drawCategory(context, Items.DIAMOND_SWORD, "Weapons", 20, mouseX, mouseY);
        drawCategory(context, Items.BOW, "Bows", 22, mouseX, mouseY);
        drawCategory(context, Items.DIAMOND_CHESTPLATE, "Armor", 24, mouseX, mouseY);
        drawCategory(context, Items.COBBLESTONE, "Materials", 31, mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawCategory(DrawContext ctx, net.minecraft.item.Item item, String name, int slot, int mx, int my) {
        int x = guiLeft + 8 + (slot % 9) * 18;
        int y = guiTop + 18 + (slot / 9) * 18;
        ctx.drawItem(new ItemStack(item), x, y);
        
        if (mx >= x && mx < x + 16 && my >= y && my < y + 16) {
            ctx.fill(x, y, x + 16, y + 16, 0x80FFFFFF);
            ctx.drawTooltip(textRenderer, Text.literal(name), mx, my);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (checkClick(20, mouseX, mouseY)) openList("WEAPON");
        if (checkClick(22, mouseX, mouseY)) openList("BOW");
        if (checkClick(24, mouseX, mouseY)) openList("ARMOR");
        if (checkClick(31, mouseX, mouseY)) openList("MATERIAL");
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void openList(String category) {
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        client.setScreen(new ItemListScreen(category));
    }

    private boolean checkClick(int slot, double mx, double my) {
        int x = guiLeft + 8 + (slot % 9) * 18;
        int y = guiTop + 18 + (slot / 9) * 18;
        return mx >= x && mx < x + 16 && my >= y && my < y + 16;
    }
}
