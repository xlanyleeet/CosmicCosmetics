package com.siliqon.cosmiccosmetics.utils;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class UI {
    private static final CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();

    public static void setMenuBackground(Inventory inv, Material material, int start, int stop, String displayName) {
        for (int i = start; i < stop; i++) {
            inv.setItem(i, makeSimpleItem(material, displayName, 1));
        }
    }

    public static ItemStack makeSimpleItem(Material material, String displayName, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        meta.getPersistentDataContainer().set(plugin.customItemKey, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack makeItemWithLore(Material material, String displayName, int amount, List<String> lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(newLore);
        item.setItemMeta(meta);
        meta.getPersistentDataContainer().set(plugin.customItemKey, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);

        return item;
    }
}
