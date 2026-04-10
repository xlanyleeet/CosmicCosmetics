package com.siliqon.cosmiccosmetics.guis;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.files.LanguageCache;
import com.siliqon.cosmiccosmetics.guis.lib.GUIManager;
import com.siliqon.cosmiccosmetics.guis.lib.InventoryGUI;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.siliqon.cosmiccosmetics.utils.Effects.getActiveEffect;
import static com.siliqon.cosmiccosmetics.utils.Effects.getEffectMaterial;
import static com.siliqon.cosmiccosmetics.utils.Effects.getFormEffects;
import static com.siliqon.cosmiccosmetics.utils.Effects.removeActiveEffect;
import static com.siliqon.cosmiccosmetics.utils.Effects.setActiveEffect;
import static com.siliqon.cosmiccosmetics.utils.UI.makeItemWithLore;
import static com.siliqon.cosmiccosmetics.utils.UI.makeSimpleItem;
import static com.siliqon.cosmiccosmetics.utils.Utils.sendMessage;

public abstract class EffectSelectionMenu extends InventoryGUI {

    protected final GUIManager guiManager;

    protected EffectSelectionMenu(CosmeticsPlugin plugin) {
        super(plugin);
        this.guiManager = plugin.getGuiManager();
    }

    protected abstract EffectForm getForm();

    protected abstract String getMenuTitle(Player player);

    protected int getBackSlot(int rows) {
        return 49;
    }

    protected int getResetSlot(int rows) {
        return 53;
    }

    protected int getMenuRows(Player player) {
        return 6;
    }

    @Override
    protected String getTitle(Player player) {
        return getMenuTitle(player);
    }

    @Override
    protected Gui createGui(Player player) {
        int rows = getMenuRows(player);
        Gui gui = createMenu(rows, getTitle(player), makeSimpleItem(Material.GRAY_STAINED_GLASS_PANE, " ", 1));
        renderMenuItems(gui, player, rows);
        return gui;
    }

    private void renderMenuItems(Gui gui, Player player, int rows) {
        LanguageCache lang = plugin.getLang(player);

        EffectForm form = getForm();
        Enum<?> activeEffect = getActiveEffect(player, form);
        List<Integer> effectSlots = getDynamicEffectSlots(rows);

        int nextSlot = 0;
        for (Enum<?> effectType : getFormEffects(form)) {
            if (effectType == null || nextSlot >= effectSlots.size()) {
                continue;
            }

            String effectName = lang.getEffectName(effectType);
            List<String> description = lang.getEffectDescription(effectType);
            Material material = getEffectMaterial(effectType);
            if (material == Material.AIR || description.isEmpty() || Objects.equals(effectName, "NONE")) {
                continue;
            }

            int slot = effectSlots.get(nextSlot++);
            String displayName = effectType == activeEffect
                    ? lang.getEffectSelectedName().replace("%effect_name%", effectName)
                    : effectName;

            if (plugin.canUseEffect(player, form, effectType)) {
                setItem(gui, slot,
                        createSelectEffectItem(gui, player, effectType, material, displayName, description, lang));
            } else {
                setItem(gui, slot, createLockedEffectItem(gui, player, effectType, displayName, lang));
            }
        }

        setItem(gui, getBackSlot(rows), createBackItem(gui, player, lang));
        setItem(gui, getResetSlot(rows), createResetItem(gui, player, lang));
    }

    private List<Integer> getDynamicEffectSlots(int rows) {
        List<Integer> slots = new ArrayList<>();
        int[] validRows = { 1, 2, 3, 4 }; // Rows 2 to 5 (0-indexed)
        for (int r : validRows) {
            for (int c = 1; c <= 7; c++) {
                slots.add(r * 9 + c);
            }
        }
        return slots;
    }

    private GuiItem createSelectEffectItem(Gui gui, Player viewer, Enum<?> effectType, Material material,
            String itemName, List<String> description, LanguageCache lang) {
        return button(
                viewer,
                player -> makeItemWithLore(material, itemName, 1, description),
                player -> {
                    LanguageCache playerLang = plugin.getLang(player);
                    Enum<?> current = getActiveEffect(player, getForm());
                    if (current == effectType) {
                        removeActiveEffect(player, getForm());
                        sendMessage(player,
                                playerLang.getEffectDisabled().replace("%effect_name%",
                                        playerLang.getEffectName(effectType)),
                                true);
                    } else {
                        setActiveEffect(player, getForm(), effectType);
                        sendMessage(player,
                                playerLang.getEffectEnabled().replace("%effect_name%",
                                        playerLang.getEffectName(effectType)),
                                true);
                    }
                    renderMenuItems(gui, player, getMenuRows(player));
                    gui.update();
                });
    }

    private GuiItem createLockedEffectItem(Gui gui, Player viewer, Enum<?> effectType, String effectName,
            LanguageCache lang) {
        if (plugin.isPermissionOnlyEffect(getForm(), effectType)) {
            return button(
                    viewer,
                    player -> makeItemWithLore(
                            Material.BARRIER,
                            lang.getEffectNotUnlockedName().replace("%effect_name%", effectName),
                            1,
                            lang.getEffectPermissionOnlyLore()),
                    player -> sendMessage(player, lang.getNoPermissionMessage(), true));
        }

        return button(
                viewer,
                player -> {
                    LanguageCache playerLang = plugin.getLang(player);
                    List<String> lore = new ArrayList<>();
                    String effectPrice = plugin.formatPrice(plugin.getEffectPrice(getForm(), effectType));
                    for (String line : playerLang.getEffectNotUnlockedLore()) {
                        lore.add(line.replace("%effect_price%", effectPrice));
                    }
                    return makeItemWithLore(
                            Material.BARRIER,
                            playerLang.getEffectNotUnlockedName().replace("%effect_name%", effectName),
                            1,
                            lore);
                },
                player -> plugin.purchaseEffect(player, getForm(), effectType)
                        .thenAccept(result -> Bukkit.getScheduler().runTask(plugin, () -> {
                            LanguageCache playerLang = plugin.getLang(player);
                            String resolvedName = playerLang.getEffectName(effectType);
                            String effectPrice = plugin.formatPrice(plugin.getEffectPrice(getForm(), effectType));

                            switch (result) {
                                case SUCCESS -> {
                                    sendMessage(player, playerLang.getPurchaseSuccessful()
                                            .replace("%effect_name%", resolvedName)
                                            .replace("%effect_price%", effectPrice), true);
                                    renderMenuItems(gui, player, getMenuRows(player));
                                    gui.update();
                                }
                                case ALREADY_OWNED -> sendMessage(player,
                                        playerLang.getPurchaseAlreadyOwned().replace("%effect_name%", resolvedName),
                                        true);
                                case PURCHASES_DISABLED ->
                                    sendMessage(player, playerLang.getPurchasesDisabledMessage(), true);
                                case NO_ECONOMY -> sendMessage(player, playerLang.getPurchasesNeedVaultMessage(), true);
                                case INSUFFICIENT_FUNDS -> sendMessage(player,
                                        playerLang.getPurchaseInsufficientFunds().replace("%effect_price%",
                                                effectPrice),
                                        true);
                                case DATABASE_ERROR -> sendMessage(player, playerLang.getPurchaseFailed(), true);
                            }
                        })));
    }

    private GuiItem createResetItem(Gui gui, Player viewer, LanguageCache lang) {
        return button(
                viewer,
                player -> makeSimpleItem(Material.REDSTONE_TORCH, lang.getResetEffectItemName(), 1),
                player -> {
                    LanguageCache playerLang = plugin.getLang(player);
                    Enum<?> current = getActiveEffect(player, getForm());
                    removeActiveEffect(player, getForm());
                    if (current != null) {
                        sendMessage(player,
                                playerLang.getEffectDisabled().replace("%effect_name%",
                                        playerLang.getEffectName(current)),
                                true);
                        renderMenuItems(gui, player, getMenuRows(player));
                        gui.update();
                    }
                });
    }

    private GuiItem createBackItem(Gui gui, Player viewer, LanguageCache lang) {
        return button(
                viewer,
                player -> makeSimpleItem(Material.ARROW, lang.getBackButtonName(), 1),
                player -> guiManager.openGUI(new MainWindow(plugin), player));
    }
}
