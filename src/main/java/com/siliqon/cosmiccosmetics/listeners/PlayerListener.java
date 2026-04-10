package com.siliqon.cosmiccosmetics.listeners;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.custom.ActiveEffectData;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.handlers.effects.Capes;
import com.siliqon.cosmiccosmetics.handlers.effects.Halos;
import com.siliqon.cosmiccosmetics.handlers.effects.Pets;
import com.siliqon.cosmiccosmetics.handlers.effects.Trails;
import com.siliqon.cosmiccosmetics.handlers.effects.Glows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.utils.Effects.getEffectsEnabled;
import static com.siliqon.cosmiccosmetics.utils.Effects.getPlayerActiveEffectData;

public class PlayerListener implements Listener {

    private final CosmeticsPlugin plugin;

    public PlayerListener(CosmeticsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> Glows.resyncForJoinedPlayer(player), 20L);
        
        plugin.getStorage().getPlayerDataAsync(player.getUniqueId())
                .thenAccept(playerData -> Bukkit.getScheduler().runTask(plugin, () -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    plugin.setupPlayerData(player, playerData);
                }));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        ActiveEffectData pdata = getPlayerActiveEffectData(player);
        boolean enabled = getEffectsEnabled(player);

        // stop any particle tasks for player
        if (pdata != null) {
            pdata.getTaskIds().forEach((form, taskIds) -> {
                for (int taskId : taskIds) {
                    Bukkit.getScheduler().cancelTask(taskId);
                }
            });
        }
        if (plugin.debugLevel >= 2) {
            log("Stopped ongoing tasks for " + player.getName());
        }
        Pets.removeForPlayer(player);

        // save data and let go of cache
        plugin.getStorage().savePlayerDataAsync(
                player.getUniqueId(),
                enabled,
                pdata,
                plugin.getPurchasedEffects(player.getUniqueId()));
        plugin.getPlayerActiveEffects().remove(player.getUniqueId());
        plugin.getCosmeticsEnabled().remove(player.getUniqueId());
        plugin.getAllPurchasedEffects().remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        ActiveEffectData pdata = getPlayerActiveEffectData(player);
        if (pdata == null) {
            return;
        }

        pdata.getTaskIds().forEach((form, taskIds) -> {
            for (int taskId : taskIds) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
        });
        pdata.getTaskIds().clear();
        Pets.removeForPlayer(player);

        for (EffectForm form : new ArrayList<>(pdata.getEffects().keySet())) {
            if (!plugin.isEffectFormEnabledInWorld(form, player.getWorld().getName())) {
                continue;
            }

            switch (form) {
                case TRAIL -> Trails.startForPlayer(player);
                case HALO -> Halos.startForPlayer(player);
                case CAPES -> Capes.startForPlayer(player);
                case PETS -> Pets.startForPlayer(player);
                case GLOW, FUNGUN -> {
                }
            }
        }
    }
}
