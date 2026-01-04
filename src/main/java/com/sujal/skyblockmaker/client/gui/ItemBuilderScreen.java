package com.sujal.skyblockmaker.client.gui;

import com.sujal.skyblockmaker.registry.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public class ItemBuilderScreen extends Screen {

    private final String itemType;
    private final Item baseItem;
    
    private TextFieldWidget nameF, loreF;
    private TextFieldWidget dmgF, strF, ccF, cdF, atksF; 
    private TextFieldWidget hpF, defF, spdF, intelF;
    private TextFieldWidget feroF, magicF;
    private TextFieldWidget abNameF, abDescF, abCostF;

    private String currentRarity = "LEGENDARY";
    private final String[] rarities = {"COMMON", "UNCOMMON", "RARE", "EPIC", "LEGENDARY", "MYTHIC", "DIVINE", "SPECIAL"};
    private ButtonWidget rarityBtn;

    public ItemBuilderScreen(String itemType, Item baseItem) {
        super(Text.literal("Item Architect"));
        this.itemType = itemType;
        this.baseItem = baseItem;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 160;
        int y = 20;

        // Rarity Cycle Button
        rarityBtn = ButtonWidget.builder(Text.literal(currentRarity), b -> {
            for(int i=0; i<rarities.length; i++) {
                if(rarities[i].equals(currentRarity)) {
                    currentRarity = rarities[(i+1)%rarities.length];
                    b.setMessage(Text.literal(currentRarity));
                    break;
                }
            }
        }).dimensions(x + 220, y, 100, 20).build();
        addDrawableChild(rarityBtn);

        addInput(nameF = createField(x, y, 210, "Item Name (e.g. Hyperion)"));
        y += 25;

        // Row 1: Offensive
        addInput(dmgF = createField(x, y, 60, "Damage"));
        addInput(strF = createField(x + 65, y, 60, "Strength"));
        addInput(ccF = createField(x + 130, y, 60, "Crit %"));
        addInput(cdF = createField(x + 195, y, 60, "Crit Dmg"));
        addInput(atksF = createField(x + 260, y, 60, "Atk Spd"));
        y += 25;

        // Row 2: Defensive & Misc
        addInput(hpF = createField(x, y, 60, "Health"));
        addInput(defF = createField(x + 65, y, 60, "Defense"));
        addInput(spdF = createField(x + 130, y, 60, "Speed"));
        addInput(intelF = createField(x + 195, y, 60, "Intel"));
        addInput(feroF = createField(x + 260, y, 60, "Ferocity"));
        y += 25;

        // Row 3: Ability
        addInput(abNameF = createField(x, y, 150, "Ability Name (e.g. Wither Impact)"));
        addInput(abCostF = createField(x + 160, y, 80, "Mana Cost"));
        addInput(magicF = createField(x + 250, y, 70, "Magic Find"));
        y += 25;
        
        addInput(abDescF = createField(x, y, 320, "Ability Description"));
        y += 25;

        // Row 4: Lore
        addInput(loreF = createField(x, y, 320, "Extra Lore / Text"));
        y += 30;

        // Create Button
        addDrawableChild(ButtonWidget.builder(Text.literal("CONSTRUCT ITEM"), b -> sendPacket())
                .dimensions(this.width / 2 - 60, y, 120, 20).build());
    }

    private TextFieldWidget createField(int x, int y, int w, String placeholder) {
        TextFieldWidget f = new TextFieldWidget(textRenderer, x, y, w, 18, Text.of(""));
        f.setPlaceholder(Text.literal("§7" + placeholder));
        return f;
    }
    private void addInput(TextFieldWidget w) { this.addDrawableChild(w); }

    private void sendPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        
        buf.writeString(itemType);
        buf.writeString(nameF.getText());
        buf.writeString(currentRarity);
        buf.writeString(loreF.getText());
        
        buf.writeString(abNameF.getText());
        buf.writeString(abDescF.getText());
        buf.writeDouble(parse(abCostF));

        buf.writeDouble(parse(dmgF));
        buf.writeDouble(parse(strF));
        buf.writeDouble(parse(ccF));
        buf.writeDouble(parse(cdF));
        buf.writeDouble(parse(atksF));
        
        buf.writeDouble(parse(hpF));
        buf.writeDouble(parse(defF));
        buf.writeDouble(parse(spdF));
        buf.writeDouble(parse(intelF));
        
        buf.writeDouble(parse(feroF));
        buf.writeDouble(parse(magicF));

        ClientPlayNetworking.send(ModPackets.ITEM_CREATE_PACKET, buf);
        this.close();
    }

    private double parse(TextFieldWidget f) { 
        try { return Double.parseDouble(f.getText()); } catch(Exception e){ return 0; } 
    }

    @Override
    public void render(DrawContext c, int mx, int my, float d) { 
        renderBackground(c); 
        c.drawCenteredTextWithShadow(textRenderer, "Item Architect", this.width/2, 10, 0xFFFFFF);
        super.render(c, mx, my, d); 
    }
}
