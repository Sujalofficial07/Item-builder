package com.sujal.skyblockmaker;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import com.sujal.skyblockmaker.client.gui.ItemBuilderScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SkyblockMakerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        
        // 1. Command Registration: /sbbuilder
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("sbbuilder")
                .executes(context -> {
                    // Command run hone par GUI open karo
                    // Hum 'context.getSource().getClient()' use kar sakte hain ya MinecraftClient.getInstance()
                    MinecraftClient.getInstance().setScreen(new ItemBuilderScreen());
                    return 1;
                }));
        });

        // 2. Tooltip logic (Stats dikhana)
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.hasNbt() && stack.getNbt().contains(SkyblockStatsApi.NBT_KEY)) {
                
                double strength = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.STRENGTH);
                if (strength != 0) {
                    lines.add(Text.literal("Strength: +" + strength).formatted(Formatting.RED));
                }

                String rarity = stack.getNbt().getCompound(SkyblockStatsApi.NBT_KEY).getString("Rarity");
                if (!rarity.isEmpty()) {
                    lines.add(Text.literal(""));
                    lines.add(Text.literal(rarity.toUpperCase()).formatted(Formatting.GOLD, Formatting.BOLD));
                }
            }
        });
    }
}
