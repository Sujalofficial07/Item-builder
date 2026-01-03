package com.sujal.skyblockmaker;

import com.sujal.skyblockmaker.api.SkyblockStatsApi;
import com.sujal.skyblockmaker.client.gui.ItemBuilderScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class SkyblockMakerClient implements ClientModInitializer {

    private static KeyBinding openBuilderKey;

    @Override
    public void onInitializeClient() {
        
        // 1. Keybind Register karna (Menu kholne ke liye 'M' dabayein)
        openBuilderKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.skyblockmaker.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M, // Default key 'M'
                "category.skyblockmaker"
        ));

        // 2. Keybind check karna har tick pe
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openBuilderKey.wasPressed()) {
                client.setScreen(new ItemBuilderScreen());
            }
        });

        // 3. Tooltip (Stats dikhana)
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
