package com.sujal.skyblockmaker.items;

import com.sujal.skyblockmaker.api.SkyblockItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class Terminator extends SkyblockItem {

    public Terminator() {
        super("TERMINATOR", "Terminator", Items.BOW, "LEGENDARY", "BOW");
        
        this.damage = 310;
        this.strength = 50;
        this.critDamage = 250;
        this.attackSpeed = 40;
        this.isDungeon = true;
        
        this.abilityName = "Salvation";
        this.abilityDesc = "Shoots 3 arrows at once.";
    }

    @Override
    public void onAbility(World world, PlayerEntity player, ItemStack stack) {
        // Logic: Shoot 3 arrows (Center, Left, Right)
        if(!world.isClient) {
            shootArrow(world, player, 0.0f);
            shootArrow(world, player, 15.0f); // Right angle
            shootArrow(world, player, -15.0f); // Left angle
        }
    }

    private void shootArrow(World world, PlayerEntity player, float offset) {
        ArrowEntity arrow = new ArrowEntity(world, player);
        arrow.setVelocity(player, player.getPitch(), player.getYaw() + offset, 0.0F, 3.0F, 1.0F);
        world.spawnEntity(arrow);
    }
}
