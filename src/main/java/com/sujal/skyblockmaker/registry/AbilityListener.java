package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockEconomyApi;
import com.sujal.skyblockmaker.api.SkyblockItem;
import com.sujal.skyblockmaker.api.SkyblockProfileApi;
import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;

public class AbilityListener {

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            
            // Check if it's a Skyblock Item
            String id = SkyblockStatsApi.getString(stack, "SkyblockID");
            if (!id.isEmpty()) {
                
                SkyblockItem itemLogic = ModItems.get(id);
                if (itemLogic != null) {
                    
                    // 1. Mana Check
                    double currentMana = SkyblockProfileApi.getBaseStat(player, SkyblockStatsApi.StatType.INTELLIGENCE); // Simplified Mana check
                    // Note: Real mana system needs "Current Mana" variable, using Base Int for now.
                    
                    // 2. Execute Ability
                    itemLogic.onAbility(world, player, stack);
                    
                    return TypedActionResult.success(stack);
                }
            }
            return TypedActionResult.pass(stack);
        });
    }
}
