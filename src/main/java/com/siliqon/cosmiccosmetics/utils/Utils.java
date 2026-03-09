package com.siliqon.cosmiccosmetics.utils;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utils {
    private static final CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();

    public static void sendMessage(Player player, String message, Boolean prefixed) {
        if (prefixed) player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.PREFIX + message));
        else player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static boolean checkPlayerPermission(Player player, String permission) {
        if (plugin.isVaultEnabled()) return plugin.getVaultPermissions().has(player, permission);
        return player.hasPermission(permission);
    }

    public static String toDisplayCase(String s) {

        final String ACTIONABLE_DELIMITERS = " '-/"; // these cause the character following
        // to be capitalized

        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        for (char c : s.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0); // explicit cast not needed
        }
        return sb.toString();
    }
}
