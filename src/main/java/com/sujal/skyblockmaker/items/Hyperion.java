package com.sujal.skyblockmaker.items;

import com.sujal.skyblockmaker.api.SkyblockItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Hyperion extends SkyblockItem {

    public Hyperion() {
        super("HYPERION", "Hyperion", Items.IRON_SWORD, "LEGENDARY", "WEAPON");
        
        this.damage = 260;
        this.strength = 150;
        this.intelligence = 350;
        this.ferocity = 30;
        this.isDungeon = true;
        
        this.abilityName = "Wither Impact";
        this.abilityDesc = "Teleports 10 blocks ahead and implodes dealing massive damage.";
        this.manaCost = 250;
    }

    @Override
    public void onAbility(World world, PlayerEntity player, ItemStack stack) {
        // Teleport Logic
        Vec3d look = player.getRotationVector();
        double dist = 10.0;
        double targetX = player.getX() + look.x * dist;
        double targetY = player.getY() + look.y * dist;
        double targetZ = player.getZ() + look.z * dist;
        
        // Basic Air Check
        if(world.getBlockState(net.minecraft.util.math.BlockPos.ofFloored(targetX, targetY, targetZ)).isAir()) {
            player.teleport(targetX, targetY, targetZ);
        }

        // Explosion Visuals
        world.addParticle(ParticleTypes.EXPLOSION, player.getX(), player.getY(), player.getZ(), 0, 0, 0);
        player.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

        // Damage Logic (AOE)
        world.getEntitiesByClass(net.minecraft.entity.LivingEntity.class, player.getBoundingBox().expand(6.0), e -> e != player)
             .forEach(e -> e.damage(world.getDamageSources().playerAttack(player), 10000));
    }
}
