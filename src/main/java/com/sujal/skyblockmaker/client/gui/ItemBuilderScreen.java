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

    // Inputs
    private TextFieldWidget nameF, reforgeF, rarityF, loreF, abNameF, abDescF;
    private TextFieldWidget dmgF, strF, ccF, cdF, hpF, defF, spdF, intelF;

    public ItemBuilderScreen() { super(Text.literal("Hypixel Item Architect")); }

    @Override
    protected void init() {
        int x = this.width / 2 - 150; // Wider area
        int y = 20;

        // --- Row 1: Identity ---
        addInput(reforgeF = createField(x, y, 60, "Prefix (Spicy)"));
        addInput(nameF = createField(x + 65, y, 140, "Item Name"));
        addInput(rarityF = createField(x + 210, y, 90, "Rarity"));
        y += 25;

        // --- Row 2: Offensive ---
        addInput(dmgF = createField(x, y, 70, "Dmg"));
        addInput(strF = createField(x + 75, y, 70, "Str"));
        addInput(ccF = createField(x + 150, y, 70, "CC %"));
        addInput(cdF = createField(x + 225, y, 70, "CD %"));
        y += 25;

        // --- Row 3: Defensive/Misc ---
        addInput(hpF = createField(x, y, 70, "HP"));
        addInput(defF = createField(x + 75, y, 70, "Def"));
        addInput(spdF = createField(x + 150, y, 70, "Speed"));
        addInput(intelF = createField(x + 225, y, 70, "Intel"));
        y += 30;

        // --- Row 4: Description ---
        addInput(loreF = createField(x, y, 300, "Flavor Text (Gray Lore)"));
        y += 25;

        // --- Row 5: Ability ---
        addInput(abNameF = createField(x, y, 300, "Ability Name (e.g. Wither Impact)"));
        y += 25;
        addInput(abDescF = createField(x, y, 300, "Ability Description"));
        y += 30;

        // --- Button ---
        addDrawableChild(ButtonWidget.builder(Text.literal("CONSTRUCT ITEM"), b -> sendPacket())
                .dimensions(this.width / 2 - 50, y, 100, 20).build());
    }

    private TextFieldWidget createField(int x, int y, int w, String placeholder) {
        TextFieldWidget f = new TextFieldWidget(textRenderer, x, y, w, 18, Text.of(""));
        f.setPlaceholder(Text.literal(placeholder));
        return f;
    }

    private void addInput(TextFieldWidget w) { this.addDrawableChild(w); }

    private void sendPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(nameF.getText());
        buf.writeString(reforgeF.getText());
        buf.writeString(rarityF.getText());
        buf.writeString(loreF.getText());
        buf.writeString(abNameF.getText());
        buf.writeString(abDescF.getText());

        buf.writeDouble(parse(dmgF));
        buf.writeDouble(parse(strF));
        buf.writeDouble(parse(ccF));
        buf.writeDouble(parse(cdF));
        buf.writeDouble(parse(hpF));
        buf.writeDouble(parse(defF));
        buf.writeDouble(parse(spdF));
        buf.writeDouble(parse(intelF));

        ClientPlayNetworking.send(ModPackets.ITEM_CREATE_PACKET, buf);
        this.close();
    }

    private double parse(TextFieldWidget f) { try { return Double.parseDouble(f.getText()); } catch(Exception e){ return 0; } }

    @Override
    public void render(DrawContext c, int mx, int my, float d) { renderBackground(c); super.render(c, mx, my, d); }
}
