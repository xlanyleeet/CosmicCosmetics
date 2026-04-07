package com.siliqon.cosmiccosmetics.utils;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Utils {
    private static final CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static void sendMessage(Player player, String message, Boolean prefixed) {
        String prefix = plugin.getLang(player).getPrefix();
        String payload = prefixed ? prefix + message : message;
        player.sendMessage(mm(payload));
    }

    public static void sendMessage(CommandSender sender, String message, Boolean prefixed) {
        String payload = prefixed ? plugin.PREFIX + message : message;
        sender.sendMessage(mm(payload));
    }

    public static Component mm(String input) {
        return MINI_MESSAGE.deserialize(input == null ? "" : input);
    }

    public static String mmToLegacy(String input) {
        return LegacyComponentSerializer.legacySection().serialize(mm(input));
    }

    public static boolean checkPlayerPermission(Player player, String permission) {
        if (plugin.isVaultEnabled())
            return plugin.getVaultPermissions().has(player, permission);
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
