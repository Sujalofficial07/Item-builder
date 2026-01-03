package com.sujal.skyblockmaker;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SkyblockMakerMod implements ModInitializer {
    
    // Packet ki ID
    public static final Identifier ITEM_CREATE_PACKET = new Identifier("skyblockmaker", "create_item");

    @Override
    public void onInitialize() {
        // Networking: Client se data receive karna
        ServerPlayNetworking.registerGlobalReceiver(ITEM_CREATE_PACKET, (server, player, handler, buf, responseSender) -> {
            
            // Buffer se data padho jo client ne bheja
            String itemName = buf.readString();
            double strength = buf.readDouble();
            String rarity = buf.readString();

            // Server thread par item create karo (Safety ke liye execute)
            server.execute(() -> {
                ItemStack newItem = new ItemStack(Items.DIAMOND_SWORD); // Default base item
                
                // Name Set karo
                newItem.setCustomName(Text.literal(itemName));
                
                // API use karke stats lagao
                SkyblockStatsApi.setStat(newItem, SkyblockStatsApi.StatType.STRENGTH, strength);
                SkyblockStatsApi.setRarity(newItem, rarity);

                // Player ko item do
                player.getInventory().insertStack(newItem);
            });
        });
    }
}
