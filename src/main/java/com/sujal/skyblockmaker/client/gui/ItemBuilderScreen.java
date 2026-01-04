package com.sujal.skyblockmaker.client.gui;

import com.sujal.skyblockmaker.api.SkyblockItem;
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

    private String itemType = "SWORD";
    private final SkyblockItem presetItem;
    
    // Inputs
    private TextFieldWidget nameF, reforgeF, starsF, enchantsF;
    private TextFieldWidget dmgF, strF, ccF, cdF, atksF; 
    private TextFieldWidget hpF, defF, spdF, intelF;
    private TextFieldWidget feroF, magicF, gearScoreF; 
    private TextFieldWidget abNameF, abDescF, abCostF;
    private TextFieldWidget loreF;

    private String currentRarity = "LEGENDARY";
    private boolean isDungeon = false;
    private final String[] rarities = {"COMMON", "UNCOMMON", "RARE", "EPIC", "LEGENDARY", "MYTHIC", "DIVINE", "SPECIAL"};
    private ButtonWidget rarityBtn, dungeonBtn;

    // Constructor creates a blank or preset builder
    public ItemBuilderScreen(SkyblockItem preset) {
        super(Text.literal("Item Architect"));
        this.presetItem = preset;
        if(preset != null) {
            this.itemType = preset.type;
            this.currentRarity = preset.rarity;
            this.isDungeon = preset.isDungeon;
        }
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 160;
        int y = 10;

        // Rarity & Dungeon Buttons
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

        dungeonBtn = ButtonWidget.builder(Text.literal("DUNGEON: " + (isDungeon ? "YES" : "NO")), b -> {
            isDungeon = !isDungeon;
            b.setMessage(Text.literal("DUNGEON: " + (isDungeon ? "YES" : "NO")));
        }).dimensions(x + 305, y, 80, 20).build();
        addDrawableChild(dungeonBtn);

        // Header
        addInput(reforgeF = createField(x, y, 70, "Reforge"));
        addInput(nameF = createField(x + 75, y, 100, "Name"));
        addInput(starsF = createField(x + 180, y, 35, "Stars"));
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
        addInput(feroF = createField(x + 260, y, 60, "Fero"));
        y += 25;

        // Stats Row 3
        addInput(magicF = createField(x, y, 70, "Magic Find"));
        addInput(gearScoreF = createField(x + 80, y, 70, "Gear Score"));
        y += 25;

        // Enchants
        addInput(enchantsF = createField(x, y, 320, "Enchants (Sharpness VII...)"));
        y += 25;

        // Ability
        addInput(abNameF = createField(x, y, 150, "Ability Name"));
        addInput(abCostF = createField(x + 160, y, 80, "Mana Cost"));
        y += 25;
        addInput(abDescF = createField(x, y, 320, "Ability Description"));
        y += 30;

        // Lore
        addInput(loreF = createField(x, y, 320, "Extra Lore"));
        y += 30;

        // Create Button
        addDrawableChild(ButtonWidget.builder(Text.literal("CONSTRUCT ITEM"), b -> sendPacket())
                .dimensions(this.width / 2 - 60, y, 120, 20).build());

        // --- AUTO FILL DATA ---
        if(presetItem != null) {
            nameF.setText(presetItem.displayName);
            if(presetItem.damage > 0) dmgF.setText(fmt(presetItem.damage));
            if(presetItem.strength > 0) strF.setText(fmt(presetItem.strength));
            if(presetItem.critChance > 0) ccF.setText(fmt(presetItem.critChance));
            if(presetItem.critDamage > 0) cdF.setText(fmt(presetItem.critDamage));
            if(presetItem.attackSpeed > 0) atksF.setText(fmt(presetItem.attackSpeed));
            
            if(presetItem.health > 0) hpF.setText(fmt(presetItem.health));
            if(presetItem.defense > 0) defF.setText(fmt(presetItem.defense));
            if(presetItem.intelligence > 0) intelF.setText(fmt(presetItem.intelligence));
            if(presetItem.ferocity > 0) feroF.setText(fmt(presetItem.ferocity));
            if(presetItem.magicFind > 0) magicF.setText(fmt(presetItem.magicFind));
            
            abNameF.setText(presetItem.abilityName);
            abDescF.setText(presetItem.abilityDesc);
            if(presetItem.manaCost > 0) abCostF.setText(fmt(presetItem.manaCost));
        }
    }

    private String fmt(double val) { return String.valueOf((int)val); }

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
        buf.writeString(reforgeF.getText()); 
        buf.writeString(starsF.getText());
        buf.writeBoolean(isDungeon);
        buf.writeString(enchantsF.getText());
        
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

    private double parse(TextFieldWidget f) { 
        try { return Double.parseDouble(f.getText()); } catch(Exception e){ return 0; } 
    }

    @Override
    public void render(DrawContext c, int mx, int my, float d) { 
        renderBackground(c); 
        c.drawCenteredTextWithShadow(textRenderer, "Item Architect", this.width/2, 5, 0xFFFFFF);
        super.render(c, mx, my, d); 
    }
}
