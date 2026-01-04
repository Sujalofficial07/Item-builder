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
    
    // Inputs
    private TextFieldWidget nameF, reforgeF, starsF, enchantsF;
    private TextFieldWidget dmgF, strF, ccF, cdF, atksF; 
    private TextFieldWidget hpF, defF, spdF, intelF;
    private TextFieldWidget feroF, magicF, gearScoreF; 
    private TextFieldWidget abNameF, abDescF, abCostF;

    private String currentRarity = "LEGENDARY";
    private boolean isDungeon = true;
    private final String[] rarities = {"COMMON", "UNCOMMON", "RARE", "EPIC", "LEGENDARY", "MYTHIC", "DIVINE", "SPECIAL"};
    private ButtonWidget rarityBtn, dungeonBtn;

    public ItemBuilderScreen(String itemType, Item baseItem) {
        super(Text.literal("Item Architect"));
        this.itemType = itemType;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 160;
        int y = 10;

        // Rarity & Dungeon Toggle
        rarityBtn = ButtonWidget.builder(Text.literal(currentRarity), b -> {
            for(int i=0; i<rarities.length; i++) {
                if(rarities[i].equals(currentRarity)) {
                    currentRarity = rarities[(i+1)%rarities.length];
                    b.setMessage(Text.literal(currentRarity));
                    break;
                }
            }
        }).dimensions(x + 220, y, 80, 20).build();
        addDrawableChild(rarityBtn);

        dungeonBtn = ButtonWidget.builder(Text.literal("DUNGEON: YES"), b -> {
            isDungeon = !isDungeon;
            b.setMessage(Text.literal("DUNGEON: " + (isDungeon ? "YES" : "NO")));
        }).dimensions(x + 305, y, 80, 20).build();
        addDrawableChild(dungeonBtn);

        // Header
        addInput(reforgeF = createField(x, y, 70, "Reforge"));
        addInput(nameF = createField(x + 75, y, 100, "Name (Hyperion)"));
        addInput(starsF = createField(x + 180, y, 35, "Stars (✪)"));
        y += 25;

        // Stats Row 1
        addInput(dmgF = createField(x, y, 60, "Damage"));
        addInput(strF = createField(x + 65, y, 60, "Strength"));
        addInput(ccF = createField(x + 130, y, 60, "Crit %"));
        addInput(cdF = createField(x + 195, y, 60, "Crit Dmg"));
        addInput(atksF = createField(x + 260, y, 60, "Atk Spd"));
        y += 25;

        // Stats Row 2
        addInput(hpF = createField(x, y, 60, "Health"));
        addInput(defF = createField(x + 65, y, 60, "Defense"));
        addInput(spdF = createField(x + 130, y, 60, "Speed"));
        addInput(intelF = createField(x + 195, y, 60, "Intel"));
        addInput(feroF = createField(x + 260, y, 60, "Ferocity"));
        y += 25;

        // Stats Row 3
        addInput(magicF = createField(x, y, 70, "Magic Find"));
        addInput(gearScoreF = createField(x + 80, y, 70, "Gear Score"));
        y += 25;

        // Enchants
        addInput(enchantsF = createField(x, y, 320, "Enchants (Sharpness VII, Giant Killer VI...)"));
        y += 25;

        // Ability
        addInput(abNameF = createField(x, y, 150, "Ability Name"));
        addInput(abCostF = createField(x + 160, y, 80, "Mana Cost"));
        y += 25;
        addInput(abDescF = createField(x, y, 320, "Ability Description"));
        y += 30;

        // Create
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
        buf.writeString(reforgeF.getText()); // New
        buf.writeString(starsF.getText());   // New
        buf.writeBoolean(isDungeon);         // New
        buf.writeString(enchantsF.getText()); // New
        
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
        buf.writeDouble(parse(gearScoreF));

        ClientPlayNetworking.send(ModPackets.ITEM_CREATE_PACKET, buf);
        this.close();
    }

    private double parse(TextFieldWidget f) { try { return Double.parseDouble(f.getText()); } catch(Exception e){ return 0; } }

    @Override
    public void render(DrawContext c, int mx, int my, float d) { 
        renderBackground(c); 
        c.drawCenteredTextWithShadow(textRenderer, "Hyperion Architect", this.width/2, 5, 0xFFFFFF);
        super.render(c, mx, my, d); 
    }
}
