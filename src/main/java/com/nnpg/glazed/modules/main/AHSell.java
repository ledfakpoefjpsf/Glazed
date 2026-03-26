package com.nnpg.glazed.modules.main;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class AHSell extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Dropdown to select item to spawn
    private final Setting<Item> itemToSpawn = sgGeneral.add(new ItemSetting.Builder()
            .name("item")
            .description("Item to spawn.")
            .defaultValue(Items.DIAMOND)
            .build()
    );

    // KeyBindings (Meteor auto-registers them)
    private final Setting<KeyBinding> spawnItemKey = sgGeneral.add(new KeyBindingSetting.Builder()
            .name("spawn-key")
            .description("Key to spawn the selected item.")
            .defaultValue(new KeyBinding("Spawn Item", GLFW.GLFW_KEY_K, "AHSell"))
            .build()
    );

    private final Setting<KeyBinding> enchantKey = sgGeneral.add(new KeyBindingSetting.Builder()
            .name("enchant-key")
            .description("Key to enchant held item.")
            .defaultValue(new KeyBinding("Enchant Item", GLFW.GLFW_KEY_E, "AHSell"))
            .build()
    );

    // God mode & vanish (Creative powers)
    private final Setting<Boolean> godMode = sgGeneral.add(new BoolSetting.Builder()
            .name("god-mode")
            .description("Makes you invulnerable.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> vanish = sgGeneral.add(new BoolSetting.Builder()
            .name("vanish")
            .description("Makes you invisible to others.")
            .defaultValue(false)
            .build()
    );

    public AHSell() {
        super(Category.Misc, "ahsell", "Creative powers in survival mode (item spawn, enchant, vanish, godmode).");
    }

    @Override
    public void onActivate() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            if (godMode.get()) player.setHealth(20.0f); // simple god mode placeholder
            if (vanish.get()) player.setInvisible(true); // vanish placeholder
        }
    }

    @Override
    public void onDeactivate() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            if (vanish.get()) player.setInvisible(false);
        }
    }

    @Override
    public void onTick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        // Spawn item
        if (spawnItemKey.get().isDown()) {
            ItemStack stack = new ItemStack(itemToSpawn.get(), 64);
            if (player.getInventory().add(stack)) {
                ChatUtils.info("Spawned 64 " + stack.getHoverName().getString());
            }
        }

        // Enchant held item
        if (enchantKey.get().isDown()) {
            ItemStack stack = player.getMainHandItem();
            if (!stack.isEmpty()) {
                Map<Enchantment, Integer> enchants = EnchantmentHelper.get(stack);
                if (!enchants.isEmpty()) {
                    Enchantment firstEnchant = enchants.keySet().iterator().next();
                    EnchantmentHelper.set(enchants.keySet().iterator().next(), 5, stack);
                    ChatUtils.info("Enchanted " + stack.getHoverName().getString());
                }
            }
        }

        // Keep god mode alive
        if (godMode.get()) player.setHealth(20.0f);
    }
}
