package com.siliqon.cosmiccosmetics.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.guis.MainWindow;
import com.siliqon.cosmiccosmetics.guis.lib.GUIManager;
import org.bukkit.entity.Player;

import static com.siliqon.cosmiccosmetics.utils.Effects.getEffectsEnabled;
import static com.siliqon.cosmiccosmetics.utils.Utils.sendMessage;

@CommandAlias("cosmetics|cc")
public class CosmeticsCommand extends BaseCommand {
    private final CosmeticsPlugin plugin;
    private GUIManager guiManager;

    public CosmeticsCommand(CosmeticsPlugin plugin) {
        this.plugin = plugin;
        this.guiManager = plugin.getGuiManager();
    }

    @Default
    public void onCommand(Player player) {
        MainWindow mainWindow = new MainWindow(plugin);
        guiManager.openGUI(mainWindow, player);
    }

    @Subcommand("toggle")
    public void toggleCosmeticsCommand(Player player) {
        var lang = plugin.getLang(player);
        boolean enabled = getEffectsEnabled(player);
        if (enabled) {
            sendMessage(player, lang.getCosmeticsDisabledOther(), true);
            plugin.getCosmeticsEnabled().put(player.getUniqueId(), false);
        } else {
            sendMessage(player, lang.getCosmeticsEnabledOther(), true);
            plugin.getCosmeticsEnabled().put(player.getUniqueId(), true);
        }
    }

}
