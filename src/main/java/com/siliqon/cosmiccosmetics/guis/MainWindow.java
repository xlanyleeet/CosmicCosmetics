package com.siliqon.cosmiccosmetics.guis;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import com.siliqon.cosmiccosmetics.files.LanguageCache;
import com.siliqon.cosmiccosmetics.guis.lib.GUIManager;
import com.siliqon.cosmiccosmetics.guis.lib.InventoryGUI;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.siliqon.cosmiccosmetics.utils.Effects.*;
import static com.siliqon.cosmiccosmetics.utils.UI.makeItemWithLore;
import static com.siliqon.cosmiccosmetics.utils.UI.makeSimpleItem;
import static com.siliqon.cosmiccosmetics.utils.Utils.sendMessage;

public class MainWindow extends InventoryGUI {
    private final GUIManager guiManager;

    public MainWindow(CosmeticsPlugin plugin) {
        super(plugin);
        this.guiManager = plugin.getGuiManager();
    }

    @Override
    protected String getTitle(Player player) {
        LanguageCache lang = plugin.getLang(player);
        return lang.getMainMenuName();
    }

    @Override
    protected Gui createGui(Player player) {
        Gui gui = createMenu(3, getTitle(player), makeSimpleItem(Material.GRAY_STAINED_GLASS_PANE, " ", 1));
        renderMainItems(gui, player);
        return gui;
    }

    private void renderMainItems(Gui gui, Player player) {
        setItem(gui, 9, trailEffectsItem(player));
        setItem(gui, 10, capesEffectsItem(player));
        setItem(gui, 11, haloEffectsItem(player));
        setItem(gui, 13, balloonsEffectsItem(player));
        setItem(gui, 15, projectileEffectsItem(player));
        setItem(gui, 16, petsEffectsItem(player));
        setItem(gui, 17, killEffectsItem(player));
        setItem(gui, 22, resetEffectsItem(gui, player));
    }

    private GuiItem trailEffectsItem(Player viewer) {
        return button(
                viewer,
                player -> {
                    LanguageCache lang = plugin.getLang(player);
                    return makeItemWithLore(
                            Material.LEATHER_BOOTS,
                            lang.getTrailEffectsItemName(),
                            1,
                            resolveCategoryLore(player, EffectForm.TRAIL, lang.getTrailEffectsItemLore(), lang));
                },
                player -> guiManager.openGUI(new TrailCosmetics(plugin), player));
    }

    private GuiItem haloEffectsItem(Player viewer) {
        return button(
                viewer,
                player -> {
                    LanguageCache lang = plugin.getLang(player);
                    return makeItemWithLore(
                            Material.END_ROD,
                            lang.getHaloEffectsItemName(),
                            1,
                            resolveCategoryLore(player, EffectForm.HALO, lang.getHaloEffectsItemLore(), lang));
                },
                player -> guiManager.openGUI(new HaloCosmetics(plugin), player));
    }

    private GuiItem projectileEffectsItem(Player viewer) {
        return button(
                viewer,
                player -> {
                    LanguageCache lang = plugin.getLang(player);
                    return makeItemWithLore(
                            Material.BOW,
                            lang.getProjectileEffectsItemName(),
                            1,
                            resolveCategoryLore(player, EffectForm.PROJECTILE, lang.getProjectileEffectsItemLore(),
                                    lang));
                },
                player -> guiManager.openGUI(new ProjectileCosmetics(plugin), player));
    }

    private GuiItem capesEffectsItem(Player viewer) {
        return button(
                viewer,
                player -> {
                    LanguageCache lang = plugin.getLang(player);
                    return makeItemWithLore(
                            Material.ELYTRA,
                            lang.getCapesEffectsItemName(),
                            1,
                            resolveCategoryLore(player, EffectForm.CAPES, lang.getCapesEffectsItemLore(), lang));
                },
                player -> guiManager.openGUI(new CapesCosmetics(plugin), player));
    }

    private GuiItem balloonsEffectsItem(Player viewer) {
        return button(
                viewer,
                player -> {
                    LanguageCache lang = plugin.getLang(player);
                    return makeItemWithLore(
                            Material.FIREWORK_ROCKET,
                            lang.getBalloonsEffectsItemName(),
                            1,
                            resolveCategoryLore(player, EffectForm.BALLOONS, lang.getBalloonsEffectsItemLore(), lang));
                },
                player -> guiManager.openGUI(new BalloonsCosmetics(plugin), player));
    }

    private GuiItem killEffectsItem(Player viewer) {
        return button(
                viewer,
                player -> {
                    LanguageCache lang = plugin.getLang(player);
                    return makeItemWithLore(
                            Material.DIAMOND_SWORD,
                            lang.getKillEffectsItemName(),
                            1,
                            resolveCategoryLore(player, EffectForm.KILL, lang.getKillEffectsItemLore(), lang));
                },
                player -> guiManager.openGUI(new KillCosmetics(plugin), player));
    }

    private GuiItem petsEffectsItem(Player viewer) {
        return button(
                viewer,
                player -> {
                    LanguageCache lang = plugin.getLang(player);
                    return makeItemWithLore(
                            Material.BONE,
                            lang.getPetsEffectsItemName(),
                            1,
                            resolveCategoryLore(player, EffectForm.PETS, lang.getPetsEffectsItemLore(), lang));
                },
                player -> guiManager.openGUI(new PetsCosmetics(plugin), player));
    }

    private GuiItem resetEffectsItem(Gui gui, Player viewer) {
        return button(
                viewer,
                player -> {
                    LanguageCache lang = plugin.getLang(player);
                    return makeItemWithLore(Material.REDSTONE_TORCH, lang.getResetAllEffectsItemName(), 1,
                            lang.getResetAllEffectsItemLore());
                },
                player -> {
                    LanguageCache lang = plugin.getLang(player);
                    removeAllActiveEffects(player);
                    sendMessage(player, lang.getDisabledAllEffectsMessage(), true);
                    renderMainItems(gui, player);
                    gui.update();
                });
    }

    private ArrayList<String> resolveCategoryLore(Player player, EffectForm form, Iterable<String> lines,
            LanguageCache lang) {
        ArrayList<String> lore = new ArrayList<>();
        EffectType activeEffect = getActiveEffect(player, form);
        for (String line : lines) {
            lore.add(line.replace("%active_effect_name%", lang.getEffectName(activeEffect)));
        }

        int totalCount = countDisplayableEffects(form);
        int unlockedCount = countUnlockedEffects(player, form);
        lore.add(lang.getCategoryUnlockedLoreLine()
                .replace("%unlocked_count%", String.valueOf(unlockedCount))
                .replace("%total_count%", String.valueOf(totalCount)));

        return lore;
    }

    private int countUnlockedEffects(Player player, EffectForm form) {
        int unlocked = 0;
        for (EffectType effectType : getFormEffects(form)) {
            if (!isDisplayableEffect(effectType)) {
                continue;
            }
            if (plugin.canUseEffect(player, form, effectType)) {
                unlocked++;
            }
        }
        return unlocked;
    }

    private int countDisplayableEffects(EffectForm form) {
        int total = 0;
        for (EffectType effectType : getFormEffects(form)) {
            if (isDisplayableEffect(effectType)) {
                total++;
            }
        }
        return total;
    }

    private boolean isDisplayableEffect(EffectType effectType) {
        if (effectType == null) {
            return false;
        }

        String effectName = plugin.getLang().getEffectName(effectType);
        Material material = getEffectMaterial(effectType);
        List<String> description = plugin.getLang().getEffectDescription(effectType);

        return material != Material.AIR
                && description != null
                && !description.isEmpty()
                && !"NONE".equals(effectName);
    }
}
