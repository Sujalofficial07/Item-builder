package com.sujal.skyblockmaker.client.gui;

import com.sujal.skyblockmaker.registry.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public class ItemBuilderScreen extends Screen {

    private TextFieldWidget nameField, rarityField, loreField;
    private TextFieldWidget strField, defField, hpField, intelField, ccField, cdField;

    public ItemBuilderScreen() { super(Text.literal("Advanced Builder")); }

    @Override
    protected void init() {
        int x = this.width / 2 - 100; 
        int y = 10;

        addInput(nameField = new TextFieldWidget(textRenderer, x, y, 200, 16, Text.literal("Name")), "Item Name");
        addInput(rarityField = new TextFieldWidget(textRenderer, x, y + 20, 200, 16, Text.literal("Rarity")), "Rarity");
        
        // Lore Field (Description)
        addInput(loreField = new TextFieldWidget(textRenderer, x, y + 40, 200, 16, Text.literal("Lore")), "Description (Lore)");

        y += 65; // Spacing increase ki

        addInput(strField = new TextFieldWidget(textRenderer, x, y, 95, 16, Text.literal("Str")), "Strength");
        addInput(defField = new TextFieldWidget(textRenderer, x + 105, y, 95, 16, Text.literal("Def")), "Defense");

        addInput(hpField = new TextFieldWidget(textRenderer, x, y + 20, 95, 16, Text.literal("HP")), "Health");
        addInput(intelField = new TextFieldWidget(textRenderer, x + 105, y + 20, 95, 16, Text.literal("Intel")), "Intelligence");
        
        addInput(ccField = new TextFieldWidget(textRenderer, x, y + 40, 95, 16, Text.literal("CC")), "Crit %");
        addInput(cdField = new TextFieldWidget(textRenderer, x + 105, y + 40, 95, 16, Text.literal("CD")), "Crit Dmg");

        addDrawableChild(ButtonWidget.builder(Text.literal("Create Item"), b -> sendPacket())
                .dimensions(x + 50, y + 70, 100, 20).build());
    }

    private void addInput(TextFieldWidget widget, String placeholder) {
        widget.setPlaceholder(Text.literal(placeholder));
        this.addDrawableChild(widget);
    }

    private void sendPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(nameField.getText());
        buf.writeString(rarityField.getText());
        buf.writeString(loreField.getText()); // Lore bhejo
        
        buf.writeDouble(parse(strField));
        buf.writeDouble(parse(defField));
        buf.writeDouble(parse(hpField));
        buf.writeDouble(parse(intelField));
        buf.writeDouble(parse(ccField));
        buf.writeDouble(parse(cdField));

        ClientPlayNetworking.send(ModPackets.ITEM_CREATE_PACKET, buf);
        this.close();
    }
    
    private double parse(TextFieldWidget f) { try { return Double.parseDouble(f.getText()); } catch(Exception e){ return 0; } }

    @Override
    public void render(DrawContext c, int mx, int my, float d) { renderBackground(c); super.render(c, mx, my, d); }
}
