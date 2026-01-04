package com.sujal.skyblockmaker.items;

import com.sujal.skyblockmaker.api.SkyblockItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class GiantSword extends SkyblockItem {

    public GiantSword() {
        super("GIANT_SWORD", "Giant's Sword", Items.IRON_SWORD, "LEGENDARY", "WEAPON");
        this.damage = 500;
        this.isDungeon = true;
        this.abilityName = "Giant's Slam";
        this.abilityDesc = "Slam your sword into the ground dealing 100,000 damage.";
        this.manaCost = 100;
    }

    @Override
    public void onAbility(World world, PlayerEntity player, ItemStack stack) {
        player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 0.5f);
        // Logic for slam animation/damage
        player.sendMessage(net.minecraft.text.Text.literal("§cUsed Giant's Slam!"), true);
    }
}
