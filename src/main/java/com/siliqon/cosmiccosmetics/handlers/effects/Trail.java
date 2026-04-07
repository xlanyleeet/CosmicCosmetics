package com.siliqon.cosmiccosmetics.handlers.effects;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.custom.ActiveEffectData;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.utils.Effects.*;

public class Trail {
    private static final CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();

    public static void startForPlayer(Player player) {
        EffectType effectType = getActiveEffect(player, EffectForm.TRAIL);
        if (effectType == null)
            return;
        if (!plugin.isEffectFormEnabledInWorld(EffectForm.TRAIL, player.getWorld().getName()))
            return;

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (player.isDead() || !player.isValid() || !player.isOnline()
                    || !player.getWorld().getPlayers().contains(player)) // TODO check if player is still
                return;
            if (!plugin.isEffectFormEnabledInWorld(EffectForm.TRAIL, player.getWorld().getName()))
                return;
            for (Player otherPlayer : player.getWorld().getPlayers()) {
                if (otherPlayer != player && !getEffectsEnabled(otherPlayer))
                    continue;

                Particle particle = getEffectParticle(effectType);
                Location loc = player.getLocation().add(0, .1, .0);
                int density = getEffectDensity(effectType);

                if (effectType == EffectType.RAINBOW) {
                    int size = 1;
                    int r = (int) (Math.random() * 256), g = (int) (Math.random() * 256),
                            b = (int) (Math.random() * 256);

                    otherPlayer.spawnParticle(
                            particle, loc, density,
                            new Particle.DustOptions(Color.fromRGB(r, g, b), size));
                    continue;
                }

                double xOffset = .05, yOffset = .05, zOffset = .05, speed = .01;
                otherPlayer.spawnParticle(
                        particle, loc, density,
                        xOffset, yOffset, zOffset, speed);
            }
        }, 0L, 2L);

        ActiveEffectData pdata = getPlayerActiveEffectData(player);
        pdata.addTaskId(EffectForm.TRAIL, taskId);
        if (plugin.debugLevel >= 2)
            log("Registered TRAIL task for " + player.getName());
    }
}
