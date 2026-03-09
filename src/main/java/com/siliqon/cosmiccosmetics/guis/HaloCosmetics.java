package com.siliqon.cosmiccosmetics.guis;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import com.siliqon.cosmiccosmetics.files.LangFile;
import com.siliqon.cosmiccosmetics.guis.lib.GUIManager;
import com.siliqon.cosmiccosmetics.guis.lib.InventoryButton;
import com.siliqon.cosmiccosmetics.guis.lib.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

import static com.siliqon.cosmiccosmetics.utils.Effects.*;
import static com.siliqon.cosmiccosmetics.utils.Utils.checkPlayerPermission;
import static com.siliqon.cosmiccosmetics.utils.UI.*;
import static com.siliqon.cosmiccosmetics.utils.Utils.sendMessage;

public class HaloCosmetics extends InventoryGUI {
    private final CosmeticsPlugin plugin;
    private final LangFile lang;
    private final GUIManager guiManager;

    private final Material backgroundMaterial = Material.GRAY_STAINED_GLASS_PANE;

    private EffectType activeEffect;
    public HaloCosmetics(CosmeticsPlugin plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.guiManager = plugin.getGuiManager();

        createInventory();
    }

    @Override
    protected void createInventory() {
        this.inventory = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', lang.getHaloEffectsMenuName()));
    }

    @Override
    public void decorate(Player player) {
        clearButtons(true);

        setMenuBackground(inventory, backgroundMaterial, 0, 27, " ");
        activeEffect = getActiveEffect(player, EffectForm.HALO);

        int slot = 9;
        for (EffectType effectType : getFormEffects(EffectForm.HALO)) {
            if (effectType == null) continue;

            String name = getEffectDisplayName(effectType);
            List<String> description = getEffectDescription(effectType);
            Material material = getEffectMaterial(effectType);

            if (material == Material.AIR || description.isEmpty() || Objects.equals(name, "NONE")) continue;

            if (effectType == activeEffect) {
                name = lang.getEffectSelectedName().replace("%effect_name%", name);
            }

            if (checkPlayerPermission(player, "cosmetics.halo."+effectType.name().toLowerCase())) {
                this.addButton(slot, selectEffectButton(material, effectType, name, description));
            } else {
                this.addButton(slot, lockedEffectButton(name));
            }
            slot++;
        }

        this.addButton(18, backButton());
        this.addButton(22, resetEffectButton());

        decorateButtons(player);
    }

    private InventoryButton selectEffectButton(Material material, EffectType effectType, String name, List<String> description) {
        return new InventoryButton()
                .creator(player -> makeItemWithLore(material, name, 1, description))
                .consumer(event -> {
                    if (activeEffect == effectType) {
                        removeActiveEffect(player, EffectForm.HALO);
                        sendMessage(player, lang.getEffectDisabled().replace("%effect_name%", getEffectDisplayName(effectType)), true);
                    } else {
                        setActiveEffect(player, EffectForm.HALO, effectType);
                        sendMessage(player, lang.getEffectEnabled().replace("%effect_name%", getEffectDisplayName(effectType)), true);
                    }
                    decorate(player);
                });
    }

    private InventoryButton lockedEffectButton(String name) {
        return new InventoryButton()
                .creator(player -> makeSimpleItem(Material.BARRIER, lang.getEffectNotUnlockedName().replace("%effect_name%", name), 1))
                .consumer(event -> {
                    int a = 0;
                    a = 1;
                });
    }

    private InventoryButton resetEffectButton() {
        return new InventoryButton()
                .creator(player -> makeSimpleItem(Material.REDSTONE_TORCH, lang.getResetEffectItemName(), 1))
                .consumer(event -> {
                    removeActiveEffect(player, EffectForm.HALO);
                    if (activeEffect != null) {
                        sendMessage(player, lang.getEffectDisabled().replace("%effect_name%", getEffectDisplayName(activeEffect)), true);
                        decorate(player);
                    }
                });
    }

    private InventoryButton backButton() {
        return new InventoryButton()
                .creator(player -> makeSimpleItem(Material.ARROW, lang.getBackButtonName(), 1))
                .consumer(event -> {
                    MainWindow mainWindow = new MainWindow(plugin);
                    guiManager.openGUI(mainWindow, player);
                });
    }
}
