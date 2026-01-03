package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModPackets {
    
    // Packet ID
    public static final Identifier ITEM_CREATE_PACKET = new Identifier("skyblockmaker", "create_item");

    // Server Side Packet Receiver Register karna
    public static void registerServerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(ITEM_CREATE_PACKET, (server, player, handler, buf, responseSender) -> {
            
            // Data read karo
            String itemName = buf.readString();
            double strength = buf.readDouble();
            String rarity = buf.readString();

            // Main logic execute karo
            server.execute(() -> {
                ItemStack newItem = new ItemStack(Items.DIAMOND_SWORD); 
                
                newItem.setCustomName(Text.literal(itemName));
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.STRENGTH, strength);
                SkyblockStatsApi.setRarity(newItem, rarity);

                // Player ko item do
                player.getInventory().insertStack(newItem);
            });
        });
    }
}
