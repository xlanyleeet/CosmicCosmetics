package com.siliqon.cosmiccosmetics.handlers.effects;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.custom.ActiveEffectData;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import org.bukkit.Bukkit;

import java.lang.Math;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.utils.Effects.*;

public class Halo {
    private static final CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();

    public static void startForPlayer(Player player) {
        EffectType effectType = getActiveEffect(player, EffectForm.HALO);
        if (effectType == null) return;

        double radius = .4;
        double fullCircle = 2 * Math.PI;
        int particles = 15;
        long[] tickCounter = {0};
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (player.isDead() || !player.isValid() || !player.isOnline() || !player.getWorld().getPlayers().contains(player)) // TODO check if player is still
                return;

            int i = (int) (tickCounter[0] % particles);
            double angle = i * fullCircle / particles + (tickCounter[0] * 0.05);
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            double y = 1.95;

            int xOffset = 0, yOffset = 0, zOffset = 0, speed = 0, count = 1;
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                if (otherPlayer != player && !getEffectsEnabled(otherPlayer))
                    continue;

                if (effectType == EffectType.RAINBOW) {
                    int r = (int) (Math.random() * 256), g = (int) (Math.random() * 256), b = (int) (Math.random() * 256);
                    int size = 1;
                    otherPlayer.spawnParticle(
                            getEffectParticle(effectType),
                            player.getLocation().add(x, y, z),
                            count, xOffset, yOffset, zOffset, speed,
                            new Particle.DustOptions(Color.fromRGB(r, g, b), size)
                    );
                    continue;
                }
                otherPlayer.spawnParticle(
                        getEffectParticle(effectType),
                        player.getLocation().add(x, y, z),
                        count, xOffset, yOffset, zOffset,
                        speed
                );
            }

            tickCounter[0]++;
        }, 0L, 1L);

        ActiveEffectData pdata = getPlayerActiveEffectData(player);
        pdata.addTaskId(EffectForm.HALO, taskId);
        if (plugin.debugLevel >= 2) log("Registered HALO task for "+player.getName());
    }
}
