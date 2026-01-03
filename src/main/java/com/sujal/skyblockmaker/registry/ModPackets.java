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

    public static void registerServerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(ITEM_CREATE_PACKET, (server, player, handler, buf, responseSender) -> {
            
            // 1. Reading HUGE Amount of Data
            String name = buf.readString();
            String reforge = buf.readString(); // Prefix like "Spicy"
            String rarity = buf.readString();
            String lore = buf.readString();
            String abilityName = buf.readString();
            String abilityDesc = buf.readString();

            double dmg = buf.readDouble();
            double str = buf.readDouble();
            double cc = buf.readDouble();
            double cd = buf.readDouble();
            double hp = buf.readDouble();
            double def = buf.readDouble();
            double speed = buf.readDouble();
            double intel = buf.readDouble();

            server.execute(() -> {
                ItemStack stack = new ItemStack(Items.DIAMOND_SWORD); // Base Item
                
                // 2. Name Logic (Reforge + Name)
                String fullName = (reforge.isEmpty() ? "" : reforge + " ") + name;
                Formatting color = getRarityColor(rarity);
                stack.setCustomName(Text.literal(fullName).formatted(color));

                // 3. Hide Vanilla Junk
                stack.addHideFlag(ItemStack.TooltipSection.MODIFIERS);
                stack.addHideFlag(ItemStack.TooltipSection.ADDITIONAL);
                stack.addHideFlag(ItemStack.TooltipSection.UNBREAKABLE);
                stack.getOrCreateNbt().putBoolean("Unbreakable", true); // Hypixel items don't break

                // 4. Saving Data
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.DAMAGE, dmg);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.STRENGTH, str);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.CRIT_CHANCE, cc);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.CRIT_DAMAGE, cd);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.HEALTH, hp);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.DEFENSE, def);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.SPEED, speed);
                SkyblockStatsApi.setStat(stack, SkyblockStatsApi.StatType.INTELLIGENCE, intel);

                SkyblockStatsApi.setString(stack, "Rarity", rarity);
                SkyblockStatsApi.setString(stack, "Lore", lore);
                SkyblockStatsApi.setString(stack, "AbilityName", abilityName);
                SkyblockStatsApi.setString(stack, "AbilityDesc", abilityDesc);

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
