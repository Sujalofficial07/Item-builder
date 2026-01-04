package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier ITEM_CREATE_PACKET = new Identifier("skyblockmaker", "create_item");
    public static final Identifier SKILL_SYNC_PACKET = new Identifier("skyblockmaker", "skill_sync");

    public static void registerServerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(ITEM_CREATE_PACKET, (server, player, handler, buf, responseSender) -> {
            
            String type = buf.readString();
            String name = buf.readString();
            String rarity = buf.readString();
            String lore = buf.readString();
            
            String abName = buf.readString();
            String abDesc = buf.readString();
            double abCost = buf.readDouble();

            double dmg = buf.readDouble();
            double str = buf.readDouble();
            double cc = buf.readDouble();
            double cd = buf.readDouble();
            double atks = buf.readDouble();
            
            double hp = buf.readDouble();
            double def = buf.readDouble();
            double spd = buf.readDouble();
            double intel = buf.readDouble();
            
            double fero = buf.readDouble();
            double magic = buf.readDouble();

            server.execute(() -> {
                ItemStack stack;
                if (type.equals("SWORD")) stack = new ItemStack(Items.DIAMOND_SWORD);
                else if (type.equals("BOW")) stack = new ItemStack(Items.BOW);
                else if (type.equals("HYPERION")) stack = new ItemStack(Items.IRON_SWORD);
                else if (type.equals("ARMOR")) stack = new ItemStack(Items.DIAMOND_CHESTPLATE);
                else stack = new ItemStack(Items.STICK);

                Formatting color = getRarityColor(rarity);
                stack.setCustomName(Text.literal(name).formatted(color));

                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.DAMAGE, dmg);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.STRENGTH, str);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.CRIT_CHANCE, cc);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.CRIT_DAMAGE, cd);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.ATTACK_SPEED, atks);
                
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.HEALTH, hp);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.DEFENSE, def);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.SPEED, spd);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.INTELLIGENCE, intel);
                
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.FEROCITY, fero);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.MAGIC_FIND, magic);
                
                if(abCost > 0) SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.MANA_COST, abCost);

                SkyblockStatsApi.setString(stack, "Rarity", rarity);
                SkyblockStatsApi.setString(stack, "Lore", lore);
                SkyblockStatsApi.setString(stack, "AbilityName", abName);
                SkyblockStatsApi.setString(stack, "AbilityDesc", abDesc);

                player.getInventory().insertStack(stack);
            });
        });
    }

    public static Formatting getRarityColor(String rarity) {
        return switch (rarity.toUpperCase()) {
            case "COMMON" -> Formatting.WHITE;
            case "UNCOMMON" -> Formatting.GREEN;
            case "RARE" -> Formatting.BLUE;
            case "EPIC" -> Formatting.DARK_PURPLE;
            case "LEGENDARY" -> Formatting.GOLD;
            case "MYTHIC" -> Formatting.LIGHT_PURPLE;
            case "DIVINE" -> Formatting.AQUA;
            case "SPECIAL" -> Formatting.RED;
            default -> Formatting.GRAY;
        };
    }
}
