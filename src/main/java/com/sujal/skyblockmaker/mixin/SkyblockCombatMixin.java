package com.sujal.skyblockmaker.mixin;

import com.sujal.skyblockmaker.api.SkyblockProfileApi;
import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity; // SWITCHED TO ARMOR STAND
import net.minecraft.entity.player.PlayerEntity;
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

            // 1. Get Stats
            double str = getTotalStat(player, SkyblockStatsApi.StatType.STRENGTH);
            double cc = getTotalStat(player, SkyblockStatsApi.StatType.CRIT_CHANCE);
            double cd = getTotalStat(player, SkyblockStatsApi.StatType.CRIT_DAMAGE);
            
            // 2. Damage Logic
            float baseDamage = (float) player.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE);
            
            boolean isCrit = Math.random() * 100 < cc;
            float finalDamage = baseDamage;
            
            if (isCrit) {
                finalDamage *= (1 + (cd / 100.0f));
            }

            // 3. Apply Extra Damage
            if (isCrit) {
                float bonusCritDmg = finalDamage - baseDamage;
                if (bonusCritDmg > 0) {
                     livingTarget.damage(player.getDamageSources().playerAttack(player), bonusCritDmg);
                }
            }

            // 4. SPAWN INDICATOR (Using Armor Stand - 100% Safe)
            spawnDamageIndicator(target, (int) finalDamage, isCrit);
        }
    }

    private void spawnDamageIndicator(Entity target, int damage, boolean isCrit) {
        // ArmorStand is reliable for all versions
        ArmorStandEntity indicator = new ArmorStandEntity(target.getWorld(), target.getX(), target.getY() + target.getHeight(), target.getZ());
        
        String text;
        if (isCrit) {
            text = "§f✨ §c" + damage + " §f✨"; // Colored Crit
        } else {
            text = "§7" + damage; // Grey Normal
        }

        indicator.setCustomName(Text.literal(text));
        indicator.setCustomNameVisible(true);
        indicator.setInvisible(true); // Body invisible
        indicator.setNoGravity(true); // Don't fall
        indicator.setMarker(true); // Tiny hitbox (prevents interfering with hits)
        
        // Add a tag so we can delete it later in ModRegistries
        indicator.addScoreboardTag("damage_indicator");
        
        // Push it up slightly randomly
        indicator.setVelocity(0, 0.1, 0);

        target.getWorld().spawnEntity(indicator);
    }

    private double getTotalStat(PlayerEntity p, SkyblockStatsApi.StatType type) {
        double val = SkyblockProfileApi.getBaseStat(p, type);
        val += SkyblockStatsApi.getStat(p.getMainHandStack(), type);
        for(net.minecraft.item.ItemStack armor : p.getInventory().armor) {
            val += SkyblockStatsApi.getStat(armor, type);
        }
        return val;
    }
}
