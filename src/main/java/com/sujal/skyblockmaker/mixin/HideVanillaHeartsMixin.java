package com.sujal.skyblockmaker.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InGameHud.class)
public class HideVanillaHeartsMixin {
    // Maine yahan se saara code hata diya hai.
    // Ab Minecraft default behavior use karega (Hearts, Food, Armor sab dikhega).
    // Agar Health badhegi toh hearts stack honge (Vanilla behavior).
}
