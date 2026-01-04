package com.sujal.skyblockmaker.items;

import com.sujal.skyblockmaker.api.SkyblockItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AspectOfTheEnd extends SkyblockItem {

    public AspectOfTheEnd() {
        super("AOTE", "Aspect of the End", Items.DIAMOND_SWORD, "RARE", "WEAPON");
        this.damage = 100;
        this.strength = 100;
        this.abilityName = "Instant Transmission";
        this.abilityDesc = "Teleport 8 blocks ahead and gain +50 Speed for 3s.";
        this.manaCost = 50;
    }

    @Override
    public void onAbility(World world, PlayerEntity player, ItemStack stack) {
        Vec3d look = player.getRotationVector();
        player.teleport(player.getX() + look.x * 8, player.getY() + look.y * 8, player.getZ() + look.z * 8);
        player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        
        // Speed Boost Logic (Would typically apply potion effect here)
    }
}
