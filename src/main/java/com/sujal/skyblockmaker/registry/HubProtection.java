package com.sujal.skyblockmaker.registry;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

public class HubProtection {
    
    // Toggle variable (Admin command se change kar sakte ho future mein)
    public static boolean protectionEnabled = true;

    public static void register() {
        
        // 1. Prevent Breaking Blocks
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (protectionEnabled && !player.hasPermissionLevel(2)) { // OP players tod sakte hain
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // 2. Prevent Placing Blocks / Interacting
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (protectionEnabled && !player.hasPermissionLevel(2)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }
}
