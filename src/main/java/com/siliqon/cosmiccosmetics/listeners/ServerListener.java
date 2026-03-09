package com.siliqon.cosmiccosmetics.listeners;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.logError;
import static com.siliqon.cosmiccosmetics.utils.Utils.sendMessage;

public class ServerListener implements Listener {
    private final CosmeticsPlugin plugin;

    public ServerListener(CosmeticsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent e) {
        if (e.getType() == ServerLoadEvent.LoadType.RELOAD) {
            if (plugin.debugLevel >= 2) log("Detected reload.");
            logError("Server reload detected! No support will be provided for issues that arise from server reloads.");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp()) {
                    sendMessage(player, "&cServer reload detected! No support will be provided for issues that arise from server reloads.", true);
                }
            }
        }
    }
}
