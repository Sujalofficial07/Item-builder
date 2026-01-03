package com.sujal.skyblockmaker.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class HideVanillaHeartsMixin {

    // 1. Hide Hearts
    // Correct 1.20.1 Signature for renderHealthBar
    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void hideHealth(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        ci.cancel(); 
    }

    // 2. Hide Food
    // renderFood(DrawContext, PlayerEntity, int, int)
    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void hideFood(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
        ci.cancel(); 
    }
    
    // 3. Hide Armor
    // renderArmor(DrawContext, PlayerEntity, int, int, int, int)
    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private void hideArmor(DrawContext context, PlayerEntity player, int y, int lines, int m, int x, CallbackInfo ci) {
        ci.cancel();
    }
}
