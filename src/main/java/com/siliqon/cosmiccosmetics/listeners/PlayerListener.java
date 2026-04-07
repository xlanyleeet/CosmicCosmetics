package com.siliqon.cosmiccosmetics.listeners;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.custom.ActiveEffectData;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.handlers.effects.Balloons;
import com.siliqon.cosmiccosmetics.handlers.effects.Capes;
import com.siliqon.cosmiccosmetics.handlers.effects.Halo;
import com.siliqon.cosmiccosmetics.handlers.effects.Pets;
import com.siliqon.cosmiccosmetics.handlers.effects.Projectile;
import com.siliqon.cosmiccosmetics.handlers.effects.Trail;
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

        Balloons.removeForPlayer(player);
        Pets.removeForPlayer(player);

        // save data and let go of cache
        plugin.getStorage().savePlayerDataAsync(
                player.getUniqueId(),
                enabled,
                pdata,
                plugin.getPurchasedEffects(player.getUniqueId()));
        plugin.playerActiveEffects.remove(player.getUniqueId());
        plugin.cosmeticsEnabled.remove(player.getUniqueId());
        plugin.purchasedEffects.remove(player.getUniqueId());
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

        Balloons.removeForPlayer(player);
        Pets.removeForPlayer(player);

        for (EffectForm form : new ArrayList<>(pdata.getEffects().keySet())) {
            if (!plugin.isEffectFormEnabledInWorld(form, player.getWorld().getName())) {
                continue;
            }

            switch (form) {
                case PROJECTILE -> Projectile.startForPlayer(player);
                case TRAIL -> Trail.startForPlayer(player);
                case HALO -> Halo.startForPlayer(player);
                case CAPES -> Capes.startForPlayer(player);
                case BALLOONS -> Balloons.startForPlayer(player);
                case PETS -> Pets.startForPlayer(player);
                case KILL, GLOW -> {
                }
            }
        }
    }
}
