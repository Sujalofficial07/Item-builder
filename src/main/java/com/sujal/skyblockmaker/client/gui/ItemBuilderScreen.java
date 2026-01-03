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

import java.util.Arrays;
import java.util.List;

public class ItemBuilderScreen extends Screen {

    private TextFieldWidget nameF, reforgeF, rarityF, loreF, abNameF, abDescF;
    private TextFieldWidget dmgF, strF, ccF, cdF, hpF, defF, spdF, intelF;
    
    // New: Item Type Selector
    private String currentType = "SWORD";
    private final List<String> itemTypes = Arrays.asList("SWORD", "BOW", "HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS", "PICKAXE", "AXE", "SHOVEL", "HOE");
    private ButtonWidget typeButton;

    public ItemBuilderScreen() { super(Text.literal("Hypixel Item Architect")); }

    @Override
    protected void init() {
        int x = this.width / 2 - 160; // Thoda aur wide kiya
        int y = 20;

        // === ROW 1: Type | Rarity | Reforge ===
        // Item Type Cycle Button
        typeButton = ButtonWidget.builder(Text.literal("Type: " + currentType), button -> {
            int index = itemTypes.indexOf(currentType);
            currentType = itemTypes.get((index + 1) % itemTypes.size());
            button.setMessage(Text.literal("Type: " + currentType));
        }).dimensions(x, y, 100, 18).build();
        addDrawableChild(typeButton);

        addInput(rarityF = createField(x + 105, y, 100, "Rarity (LEGENDARY)"));
        addInput(reforgeF = createField(x + 210, y, 110, "Prefix (Spicy)"));
        y += 25;

        // === ROW 2: Identity ===
        addInput(nameF = createField(x, y, 320, "Item Name"));
        y += 25;

        // === ROW 3: Stats (Compact Grid) ===
        // Offensive
        addInput(dmgF = createField(x, y, 75, "Damage"));
        addInput(strF = createField(x + 80, y, 75, "Strength"));
        addInput(ccF = createField(x + 160, y, 75, "Crit %"));
        addInput(cdF = createField(x + 240, y, 80, "Crit Dmg %"));
        y += 25;

        // Defensive
        addInput(hpF = createField(x, y, 75, "Health"));
        addInput(defF = createField(x + 80, y, 75, "Defense"));
        addInput(spdF = createField(x + 160, y, 75, "Speed"));
        addInput(intelF = createField(x + 240, y, 80, "Intel"));
        y += 30;

        // === ROW 4: Description ===
        addInput(loreF = createField(x, y, 320, "Lore / Description"));
        y += 25;

        // === ROW 5: Ability ===
        addInput(abNameF = createField(x, y, 320, "Ability Name"));
        y += 25;
        addInput(abDescF = createField(x, y, 320, "Ability Description"));
        y += 30;

        // === CREATE BUTTON ===
        addDrawableChild(ButtonWidget.builder(Text.literal("CREATE ITEM"), b -> sendPacket())
                .dimensions(this.width / 2 - 60, y, 120, 20).build());
    }

    private TextFieldWidget createField(int x, int y, int w, String placeholder) {
        TextFieldWidget f = new TextFieldWidget(textRenderer, x, y, w, 18, Text.of(""));
        f.setPlaceholder(Text.literal(placeholder));
        return f;
    }
    private void addInput(TextFieldWidget w) { this.addDrawableChild(w); }

    private void sendPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(currentType); // Send Selected Type
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
