package com.sujal.skyblockmaker.client.gui;

import com.sujal.skyblockmaker.api.SkyblockItem;
import com.sujal.skyblockmaker.registry.ModItems;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ItemListScreen extends Screen {

    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private final String category;
    private List<SkyblockItem> items;
    private int guiLeft, guiTop;

    public ItemListScreen(String category) {
        super(Text.literal("Select Item"));
        this.category = category;
        this.items = ModItems.getByCategory(category);
    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - 176) / 2;
        this.guiTop = (this.height - 222) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        context.drawTexture(TEXTURE, guiLeft, guiTop, 0, 0, 176, 222);
        context.drawText(textRenderer, category + " Items", guiLeft + 8, guiTop + 6, 0x404040, false);

        for(int i=0; i < items.size() && i < 45; i++) {
            SkyblockItem item = items.get(i);
            int x = guiLeft + 8 + (i % 9) * 18;
            int y = guiTop + 18 + (i / 9) * 18;
            
            // Just display the base material for icon
            context.drawItem(new ItemStack(item.material), x, y);

            if(mouseX >= x && mouseX < x+16 && mouseY >= y && mouseY < y+16) {
                context.fill(x, y, x+16, y+16, 0x80FFFFFF);
                context.drawTooltip(textRenderer, Text.literal(item.displayName), mouseX, mouseY);
            }
        }

        // Back Button
        context.drawItem(new ItemStack(Items.ARROW), guiLeft + 8, guiTop + 200);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check Item Clicks
        for(int i=0; i < items.size() && i < 45; i++) {
            int x = guiLeft + 8 + (i % 9) * 18;
            int y = guiTop + 18 + (i / 9) * 18;
            
            if(mouseX >= x && mouseX < x+16 && mouseY >= y && mouseY < y+16) {
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                SkyblockItem selected = items.get(i);
                // Open Builder with this item's data
                client.setScreen(new ItemBuilderScreen(selected));
                return true;
            }
        }

        // Back Button
        if(mouseX >= guiLeft+8 && mouseX < guiLeft+24 && mouseY >= guiTop+200 && mouseY < guiTop+216) {
            client.setScreen(new CategorySelectionScreen());
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
