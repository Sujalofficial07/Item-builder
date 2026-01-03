package com.sujal.skyblockmaker.mixin;

import com.sujal.skyblockmaker.api.SkyblockProfileApi;
import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import com.sujal.skyblockmaker.api.SkyblockSkillsApi; // Import
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class SkyblockCombatMixin {

    @Inject(method = "attack", at = @At("HEAD"))
    public void onAttack(Entity target, CallbackInfo ci) {
        if (target instanceof LivingEntity livingTarget && !target.getWorld().isClient) {
            PlayerEntity player = (PlayerEntity) (Object) this;

            double str = getTotalStat(player, SkyblockStatsApi.StatType.STRENGTH);
            double cc = getTotalStat(player, SkyblockStatsApi.StatType.CRIT_CHANCE);
            double cd = getTotalStat(player, SkyblockStatsApi.StatType.CRIT_DAMAGE);
            
            float baseDamage = (float) player.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE);
            
            boolean isCrit = Math.random() * 100 < cc;
            float finalDamage = baseDamage;
            
            if (isCrit) {
                finalDamage *= (1 + (cd / 100.0f));
            }

            if (isCrit) {
                float bonusCritDmg = finalDamage - baseDamage;
                if (bonusCritDmg > 0) {
                     livingTarget.damage(player.getDamageSources().playerAttack(player), bonusCritDmg);
                }
            }

            spawnDamageIndicator(target, (int) finalDamage, isCrit);
        }
    }

    private void spawnDamageIndicator(Entity target, int damage, boolean isCrit) {
        ArmorStandEntity indicator = new ArmorStandEntity(target.getWorld(), target.getX(), target.getY() + target.getHeight(), target.getZ());
        
        String text;
        if (isCrit) {
            text = "§f✨ §c" + damage + " §f✨"; 
        } else {
            text = "§7" + damage;
        }

        indicator.setCustomName(Text.literal(text));
        indicator.setCustomNameVisible(true);
        indicator.setInvisible(true);
        indicator.setNoGravity(true);
        
        NbtCompound nbt = new NbtCompound();
        indicator.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Marker", true);
        indicator.readCustomDataFromNbt(nbt);
        
        indicator.addCommandTag("damage_indicator");
        indicator.setVelocity(0, 0.1, 0);

        target.getWorld().spawnEntity(indicator);
    }

    private double getTotalStat(PlayerEntity p, SkyblockStatsApi.StatType type) {
        double val = SkyblockProfileApi.getBaseStat(p, type);
        val += SkyblockStatsApi.getStat(p.getMainHandStack(), type);
        for(net.minecraft.item.ItemStack armor : p.getInventory().armor) {
            val += SkyblockStatsApi.getStat(armor, type);
        }
        
        // NEW: Add Skill Bonus (e.g. Combat Level -> Crit Chance)
        val += SkyblockSkillsApi.getSkillStatBonus(p, type);
        
        return val;
    }
}
