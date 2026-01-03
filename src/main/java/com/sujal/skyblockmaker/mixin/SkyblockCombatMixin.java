package com.sujal.skyblockmaker.mixin;

import com.sujal.skyblockmaker.api.SkyblockProfileApi;
import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
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
            
            // 2. Calculate Damage (Hypixel Formula)
            // Note: Base damage attribute already set hai StatHandler se, hum sirf Crit multiplier lagayenge
            float baseDamage = (float) player.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE);
            
            boolean isCrit = Math.random() * 100 < cc;
            float finalDamage = baseDamage;
            
            if (isCrit) {
                finalDamage *= (1 + (cd / 100.0f));
            }

            // 3. Apply Damage Logic (Manually update target health to simulate crit)
            // Vanilla hit hone ke baad hum extra damage add kar rahe hain agar Crit hua
            if (isCrit) {
                // Vanilla hit already base damage dega, hume bas extra hissa dena hai
                float bonusCritDmg = finalDamage - baseDamage;
                if (bonusCritDmg > 0) {
                     livingTarget.damage(player.getDamageSources().playerAttack(player), bonusCritDmg);
                }
            }

            // 4. SPAWN DAMAGE INDICATOR (Hawa mein number)
            spawnDamageIndicator(target, (int) finalDamage, isCrit);
        }
    }

    private void spawnDamageIndicator(Entity target, int damage, boolean isCrit) {
        // TextDisplay Entity 1.20.1 mein best hai floating text ke liye
        net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity indicator = 
            new net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity(net.minecraft.entity.EntityType.TEXT_DISPLAY, target.getWorld());
        
        indicator.setPosition(target.getX(), target.getY() + target.getHeight() + 0.5, target.getZ());
        
        // Color Logic
        String text;
        if (isCrit) {
            // Crit: ✨ 1000 ✨ (Colors mimic Hypixel style)
            text = "§f✨ §c" + damage + " §f✨"; 
        } else {
            // Normal: Grey
            text = "§7" + damage;
        }

        indicator.setText(Text.literal(text));
        indicator.setBillboardMode(DisplayEntity.BillboardMode.CENTER); // Always face player
        indicator.setBackground(0); // Transparent background
        
        // Make it float up and die (Simple logic via NBT/Tick handling usually needed, 
        // but for simplicity we spawn it. It will stay unless we kill it.
        // Quick hack: Age handling requires ticking entity. 
        // Better: Use a custom particle, but TextDisplay is easier to code here.
        // We will set glowing to make it pop.)
        indicator.setGlowing(true);
        
        target.getWorld().spawnEntity(indicator);
        
        // Auto-Kill logic (Iske liye tick event chahiye hota hai, par abhi ke liye ye permanent rahega 
        // jab tak hum scheduler na lagayein. ModRegistries mein fix karenge).
        // Temporary: Hum ise 20 ticks (1 sec) ke liye 'Age' tag de sakte hain agar custom entity hoti.
        // Workaround: Niche ModRegistries mein cleanup code dunga.
    }

    private double getTotalStat(PlayerEntity p, SkyblockStatsApi.StatType type) {
        // Quick fetch helper
        double val = SkyblockProfileApi.getBaseStat(p, type);
        val += SkyblockStatsApi.getStat(p.getMainHandStack(), type);
        for(net.minecraft.item.ItemStack armor : p.getInventory().armor) {
            val += SkyblockStatsApi.getStat(armor, type);
        }
        return val;
    }
}
