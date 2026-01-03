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

    private TextFieldWidget nameField, rarityField;
    private TextFieldWidget strField, defField, hpField, intelField, ccField, cdField;

    public ItemBuilderScreen() {
        super(Text.literal("Advanced Builder"));
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 100; // Center X
        int y = 20; // Start Y

        // Name & Rarity
        addInput(nameField = new TextFieldWidget(textRenderer, x, y, 200, 16, Text.literal("Name")), "Item Name");
        addInput(rarityField = new TextFieldWidget(textRenderer, x, y + 20, 200, 16, Text.literal("Rarity")), "Rarity (COMMON, RARE, LEGENDARY)");
        
        y += 45; // Spacing

        // Stats Row 1
        addInput(strField = new TextFieldWidget(textRenderer, x, y, 95, 16, Text.literal("Str")), "Strength");
        addInput(defField = new TextFieldWidget(textRenderer, x + 105, y, 95, 16, Text.literal("Def")), "Defense");

        // Stats Row 2
        addInput(hpField = new TextFieldWidget(textRenderer, x, y + 20, 95, 16, Text.literal("HP")), "Health");
        addInput(intelField = new TextFieldWidget(textRenderer, x + 105, y + 20, 95, 16, Text.literal("Intel")), "Intelligence");

        // Stats Row 3
        addInput(ccField = new TextFieldWidget(textRenderer, x, y + 40, 95, 16, Text.literal("CC")), "Crit Chance %");
        addInput(cdField = new TextFieldWidget(textRenderer, x + 105, y + 40, 95, 16, Text.literal("CD")), "Crit Dmg %");

        // Create Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Create Advanced Item"), button -> sendPacket())
                .dimensions(x + 50, y + 70, 100, 20).build());
    }

    private void addInput(TextFieldWidget widget, String placeholder) {
        widget.setPlaceholder(Text.literal(placeholder));
        this.addDrawableChild(widget);
    }

    private void sendPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        
        // Strings
        buf.writeString(nameField.getText());
        buf.writeString(rarityField.getText());

        // Doubles (Safe Parsing)
        buf.writeDouble(parse(strField));
        buf.writeDouble(parse(defField));
        buf.writeDouble(parse(hpField));
        buf.writeDouble(parse(intelField));
        buf.writeDouble(parse(ccField));
        buf.writeDouble(parse(cdField));

        ClientPlayNetworking.send(ModPackets.ITEM_CREATE_PACKET, buf);
        this.close();
    }

    private double parse(TextFieldWidget field) {
        try { return Double.parseDouble(field.getText()); } catch (Exception e) { return 0; }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(textRenderer, "Skyblock Item Creator", width / 2, 5, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
