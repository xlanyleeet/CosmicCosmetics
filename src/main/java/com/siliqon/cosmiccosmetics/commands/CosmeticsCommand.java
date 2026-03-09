package com.siliqon.cosmiccosmetics.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.files.LangFile;
import com.siliqon.cosmiccosmetics.guis.MainWindow;
import com.siliqon.cosmiccosmetics.guis.lib.GUIManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.siliqon.cosmiccosmetics.utils.Effects.getEffectsEnabled;
import static com.siliqon.cosmiccosmetics.utils.Utils.sendMessage;

@CommandAlias("cosmetics|cc")
public class CosmeticsCommand extends BaseCommand {
    private final CosmeticsPlugin plugin;
    private GUIManager guiManager;
    private LangFile lang;

    public CosmeticsCommand(CosmeticsPlugin plugin) {
        this.plugin = plugin;
        this.guiManager = plugin.getGuiManager();
        this.lang = plugin.getLang();
    }

    @Default
    @CommandPermission("cosmetics.use")
    public void onCommand(Player player) {
        MainWindow mainWindow = new MainWindow(plugin);
        guiManager.openGUI(mainWindow, player);
    }

    @Subcommand("toggle")
    @CommandPermission("cosmetics.toggle")
    public void toggleCosmeticsCommand(Player player) {
        boolean enabled = getEffectsEnabled(player);
        if (enabled) {
            sendMessage(player, lang.getCosmeticsDisabledOther(), true);
            plugin.cosmeticsEnabled.put(player.getUniqueId(), false);
        } else {
            sendMessage(player, lang.getCosmeticsEnabledOther(), true);
            plugin.cosmeticsEnabled.put(player.getUniqueId(), true);
        }
    }

    @Subcommand("version")
    @CommandPermission("cosmetics.version")
    public void versionCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bThe current plugin version is &d"+plugin.PLUGIN_VERSION));
    }
}
