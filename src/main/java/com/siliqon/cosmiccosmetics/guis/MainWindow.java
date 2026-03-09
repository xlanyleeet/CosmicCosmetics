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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static com.siliqon.cosmiccosmetics.utils.Effects.*;
import static com.siliqon.cosmiccosmetics.utils.UI.*;
import static com.siliqon.cosmiccosmetics.utils.Utils.sendMessage;

public class MainWindow extends InventoryGUI {
    private final CosmeticsPlugin plugin;
    private final LangFile lang;
    private final GUIManager guiManager;

    private final Material backgroundMaterial = Material.GRAY_STAINED_GLASS_PANE;

    public MainWindow(CosmeticsPlugin plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.guiManager = plugin.getGuiManager();

        createInventory();
    }

    @Override
    protected void createInventory() {
        this.inventory = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', lang.getMainMenuName()));
    }

    @Override
    public void decorate(Player player) {
        clearButtons(true);

        setMenuBackground(inventory, backgroundMaterial, 0, 27, " ");

        this.addButton(10, trailEffectsButton());
        this.addButton(12, haloEffectsButton());
        this.addButton(14, projectileEffectsButton());
        this.addButton(16, killEffectsButton());

        this.addButton(22, resetEffectsButton());

        decorateButtons(player);
    }

    private InventoryButton trailEffectsButton() {
        return new InventoryButton()
                .creator(player -> {
                    ItemStack item = makeSimpleItem(Material.LEATHER_BOOTS, lang.getTrailEffectsItemName(), 1);
                    ItemMeta meta = item.getItemMeta();

                    ArrayList<String> newLore = new ArrayList<>();
                    EffectType activeEffect = getActiveEffect(player, EffectForm.TRAIL);
                    for (String line : lang.getTrailEffectsItemLore()) {
                        newLore.add(ChatColor.translateAlternateColorCodes('&', line
                                .replace("%active_effect_name%", getEffectDisplayName(activeEffect))
                        ));
                    }
                    meta.setLore(newLore);

                    item.setItemMeta(meta);
                    return item;
                })
                .consumer(event -> {
                    TrailCosmetics trailCosmetics = new TrailCosmetics(plugin);
                    guiManager.openGUI(trailCosmetics, player);
                });
    }
    private InventoryButton haloEffectsButton() {
        return new InventoryButton()
                .creator(player -> {
                    ItemStack item = makeSimpleItem(Material.END_ROD, lang.getHaloEffectsItemName(), 1);
                    ItemMeta meta = item.getItemMeta();

                    ArrayList<String> newLore = new ArrayList<>();
                    EffectType activeEffect = getActiveEffect(player, EffectForm.HALO);
                    for (String line : lang.getHaloEffectsItemLore()) {
                        newLore.add(ChatColor.translateAlternateColorCodes('&', line
                                .replace("%active_effect_name%", getEffectDisplayName(activeEffect))
                        ));
                    }
                    meta.setLore(newLore);

                    item.setItemMeta(meta);
                    return item;
                })
                .consumer(event -> {
                    HaloCosmetics haloCosmetics = new HaloCosmetics(plugin);
                    guiManager.openGUI(haloCosmetics, player);
                });
    }
    private InventoryButton projectileEffectsButton() {
        return new InventoryButton()
                .creator(player -> {
                    ItemStack item = makeSimpleItem(Material.BOW, lang.getProjectileEffectsItemName(), 1);
                    ItemMeta meta = item.getItemMeta();

                    ArrayList<String> newLore = new ArrayList<>();
                    EffectType activeEffect = getActiveEffect(player, EffectForm.PROJECTILE);
                    for (String line : lang.getProjectileEffectsItemLore()) {
                        newLore.add(ChatColor.translateAlternateColorCodes('&', line
                                .replace("%active_effect_name%", getEffectDisplayName(activeEffect))
                        ));
                    }
                    meta.setLore(newLore);

                    item.setItemMeta(meta);
                    return item;
                })
                .consumer(event -> {
                    ProjectileCosmetics projectileCosmetics = new ProjectileCosmetics(plugin);
                    guiManager.openGUI(projectileCosmetics, player);
                });
    }
    private InventoryButton killEffectsButton() {
        return new InventoryButton()
                .creator(player -> {
                    ItemStack item = makeSimpleItem(Material.DIAMOND_SWORD, lang.getKillEffectsItemName(), 1);
                    ItemMeta meta = item.getItemMeta();

                    ArrayList<String> newLore = new ArrayList<>();
                    EffectType activeEffect = getActiveEffect(player, EffectForm.KILL);
                    for (String line : lang.getKillEffectsItemLore()) {
                        newLore.add(ChatColor.translateAlternateColorCodes('&', line
                                .replace("%active_effect_name%", getEffectDisplayName(activeEffect))
                        ));
                    }
                    meta.setLore(newLore);

                    item.setItemMeta(meta);
                    return item;
                })
                .consumer(event -> {
                    KillCosmetics killCosmetics = new KillCosmetics(plugin);
                    guiManager.openGUI(killCosmetics, player);
                });
    }
    private InventoryButton resetEffectsButton() {
        return new InventoryButton()
                .creator(player -> makeItemWithLore(Material.REDSTONE_TORCH, lang.getResetAllEffectsItemName(), 1, lang.getResetAllEffectsItemLore()))
                .consumer(event -> {
                    removeAllActiveEffects(player);
                    sendMessage(player, lang.getDisabledAllEffectsMessage(), true);
                    decorate(player);
                });
    }
}
