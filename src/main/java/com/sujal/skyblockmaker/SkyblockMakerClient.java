package com.sujal.skyblockmaker;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import com.sujal.skyblockmaker.client.gui.ItemBuilderScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SkyblockMakerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Console mein ye message aana chahiye start hote waqt
        System.out.println("SkyblockMaker Client Loading...");

        // 1. Command Register (/sbbuilder)
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("sbbuilder")
                .executes(context -> {
                    System.out.println("Command /sbbuilder detected!"); // Debug log
                    
                    // FIX: Screen ko main thread par schedule karo taaki chat band hone par ye band na ho
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.execute(() -> {
                        client.setScreen(new ItemBuilderScreen());
                    });
                    
                    return 1;
                }));
        });

        // 2. Tooltip Register (Stats Dikhana)
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.hasNbt() && stack.getNbt().contains(SkyblockStatsApi.NBT_KEY)) {
                
                double strength = SkyblockStatsApi.getStat(stack, SkyblockStatsApi.StatType.STRENGTH);
                if (strength != 0) {
                    lines.add(Text.literal("Strength: +" + (int)strength).formatted(Formatting.RED));
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
