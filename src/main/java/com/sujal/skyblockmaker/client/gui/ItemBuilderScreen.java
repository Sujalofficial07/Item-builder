package com.sujal.skyblockmaker.client.gui;

import com.sujal.skyblockmaker.registry.ModPackets; // Import new registry
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public class ItemBuilderScreen extends Screen {

    private TextFieldWidget nameField;
    private TextFieldWidget strengthField;
    private TextFieldWidget rarityField;

    public ItemBuilderScreen() {
        super(Text.literal("Item Builder"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 50;

        addDrawableChild(nameField = new TextFieldWidget(textRenderer, centerX - 100, y, 200, 20, Text.literal("Name")));
        nameField.setPlaceholder(Text.literal("Item Name"));

        addDrawableChild(strengthField = new TextFieldWidget(textRenderer, centerX - 100, y + 30, 200, 20, Text.literal("Strength")));
        strengthField.setPlaceholder(Text.literal("Strength"));

        addDrawableChild(rarityField = new TextFieldWidget(textRenderer, centerX - 100, y + 60, 200, 20, Text.literal("Rarity")));
        rarityField.setPlaceholder(Text.literal("Rarity"));

        addDrawableChild(ButtonWidget.builder(Text.literal("Create"), button -> sendCreatePacket())
                .dimensions(centerX - 50, y + 100, 100, 20)
                .build());
    }

    private void sendCreatePacket() {
        String name = nameField.getText();
        String rarity = rarityField.getText();
        double strength = 0;
        try {
            strength = Double.parseDouble(strengthField.getText());
        } catch (Exception ignored) {}

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(name);
        buf.writeDouble(strength);
        buf.writeString(rarity);

        // Uses ModPackets ID
        ClientPlayNetworking.send(ModPackets.ITEM_CREATE_PACKET, buf);
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(textRenderer, this.title, width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
