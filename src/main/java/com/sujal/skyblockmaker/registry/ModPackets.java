package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier ITEM_CREATE_PACKET = new Identifier("skyblockmaker", "create_item");

    public static void registerServerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(ITEM_CREATE_PACKET, (server, player, handler, buf, responseSender) -> {
            String itemName = buf.readString();
            double strength = buf.readDouble();
            double defense = buf.readDouble(); // Defense bhi read karega
            String rarity = buf.readString();

            server.execute(() -> {
                // By default Diamond Chestplate de raha hu test ke liye
                // (Tum baad mein selection add kar sakte ho)
                ItemStack newItem = new ItemStack(Items.DIAMOND_CHESTPLATE); 
                
                newItem.setCustomName(Text.literal(itemName));
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.STRENGTH, strength);
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.DEFENSE, defense); // Defense Set
                SkyblockStatsApi.setRarity(newItem, rarity);

                player.getInventory().insertStack(newItem);
            });
        });
    }
}
