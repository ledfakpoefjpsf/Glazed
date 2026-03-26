package com.nnpg.glazed.modules.main;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.enchantment.EnchantmentHelper;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class CreativeSurvivalPlus extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Item spawn dropdown
    private final Setting<Item> itemToSpawn = sgGeneral.add(new ItemSetting.Builder()
        .name("item")
        .description("Pick an item to spawn.")
        .defaultValue(Items.DIAMOND)
        .build()
    );

    // Spawn button (uses Meteor’s autoset keybind system)
    private final Setting<KeyBinding> spawnItemKey = sgGeneral.add(new KeyBindingSetting.Builder()
        .name("spawn-key")
        .description("Key to spawn the item.")
        .defaultValue(new KeyBinding("Spawn Item", GLFW.GLFW_KEY_K, "CreativeSurvivalPlus"))
        .build()
    );

    // Enchant held item
    private final Setting<KeyBinding> enchantKey = sgGeneral.add(new KeyBindingSetting.Builder()
        .name("enchant-key")
        .description("Key to enchant held item.")
        .defaultValue(new KeyBinding("Enchant Item", GLFW.GLFW_KEY_E, "CreativeSurvivalPlus"))
        .build()
    );

    // Creative powers
    private final Setting<Boolean> godMode = sgGeneral.add(new BoolSetting.Builder()
        .name("god-mode")
        .description("Take no damage.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> vanish = sgGeneral.add(new BoolSetting.Builder()
        .name("vanish")
        .description("Makes you invisible to other players.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> flight = sgGeneral.add(new BoolSetting.Builder()
        .name("flight")
        .description("Allows you to fly like creative.")
        .defaultValue(false)
        .build()
    );

    public CreativeSurvivalPlus() {
        super(meteordevelopment.meteorclient.systems.modules.Category.World, 
              "creative-survival-plus", 
              "Spawn items, enchant, fly, vanish, and godmode while keeping achievements.");
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        // Spawn item
        if (spawnItemKey.get().isPressed()) {
            ItemStack stack = new ItemStack(itemToSpawn.get(), 64);
            if (player.getInventory().insertStack(stack)) {
                ChatUtils.info("Spawned 64 " + stack.getName().getString());
            }
        }

        // Enchant held item
        if (enchantKey.get().isPressed()) {
            ItemStack stack = player.getMainHandStack();
            if (!stack.isEmpty()) {
                var firstEnchant = EnchantmentHelper.get(stack).keySet().iterator().next();
                EnchantmentHelper.set(stack, Map.of(firstEnchant, 5));
                ChatUtils.info("Enchanted " + stack.getName().getString());
            }
        }

        // God mode
        if (godMode.get()) {
            player.setHealth(player.getMaxHealth());
            player.setInvulnerable(true);
        } else {
            player.setInvulnerable(false);
        }

        // Flight
        player.getAbilities().allowFlying = flight.get();
        player.getAbilities().flying = flight.get();

        // Vanish
        player.noClip = vanish.get();
    }

    @Override
    public void onDeactivate() {
        // Reset creative powers on disable
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        player.setInvulnerable(false);
        player.getAbilities().allowFlying = false;
        player.getAbilities().flying = false;
        player.noClip = false;
    }
}
