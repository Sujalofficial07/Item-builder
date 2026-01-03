package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockSkillsApi;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.block.BlockState;

public class SkillListener {

    public static void register() {
        
        // 1. MINING, FARMING & FORAGING (Block Break)
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient || !(player instanceof ServerPlayerEntity serverPlayer)) return;

            // Mining (Stone, Ores)
            if (state.isOf(Blocks.STONE) || state.isOf(Blocks.COBBLESTONE)) {
                SkyblockSkillsApi.addXp(serverPlayer, SkyblockSkillsApi.Skill.MINING, 1);
            } else if (state.getBlock().getName().getString().contains("Ore")) {
                SkyblockSkillsApi.addXp(serverPlayer, SkyblockSkillsApi.Skill.MINING, 5);
            }
            
            // Foraging (Logs)
            else if (state.isIn(net.minecraft.registry.tag.BlockTags.LOGS)) {
                SkyblockSkillsApi.addXp(serverPlayer, SkyblockSkillsApi.Skill.FORAGING, 6);
            }

            // Farming (Crops)
            else if (state.isOf(Blocks.WHEAT) || state.isOf(Blocks.CARROTS) || state.isOf(Blocks.POTATOES)) {
                SkyblockSkillsApi.addXp(serverPlayer, SkyblockSkillsApi.Skill.FARMING, 4);
            }
        });

        // 2. COMBAT (Mob Kill)
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof ServerPlayerEntity player) {
                
                // Hostile Mobs (Zombie, Skeleton, etc.) -> Combat XP
                if (killedEntity instanceof HostileEntity) {
                    SkyblockSkillsApi.addXp(player, SkyblockSkillsApi.Skill.COMBAT, 10);
                }
                // Animals -> Farming XP (Hypixel logic: Killing cows gives Farming XP)
                else if (killedEntity instanceof net.minecraft.entity.passive.AnimalEntity) {
                    SkyblockSkillsApi.addXp(player, SkyblockSkillsApi.Skill.FARMING, 2);
                }
            }
        });
    }
}
