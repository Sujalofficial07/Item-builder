package com.sujal.skyblockmaker.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class HideVanillaHeartsMixin {

    // Hearts aur Food bar ko render hone se roko
    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void hideHealth(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        ci.cancel(); // Cancel Vanilla Hearts
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void hideFood(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
        ci.cancel(); // Cancel Vanilla Food Bar (Optional, agar skyblock feel chahiye)
    }
    
    // Armor bar bhi hide karna hai toh:
    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private void hideArmor(DrawContext context, PlayerEntity player, int y, int lines, int m, int x, CallbackInfo ci) {
        ci.cancel();
    }
}
