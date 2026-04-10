package com.siliqon.cosmiccosmetics.handlers.effects;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.custom.ActiveEffectData;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.Halo;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.utils.Effects.*;

public class Halos {
    private static final CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();

    public static void startForPlayer(Player player) {
        Enum<?> activeEffectEnum = getActiveEffect(player, EffectForm.HALO);
        if (!(activeEffectEnum instanceof Halo)) {
            return;
        }
        Halo haloType = (Halo) activeEffectEnum;

        if (!plugin.isEffectFormEnabledInWorld(EffectForm.HALO, player.getWorld().getName()))
            return;

        double radius = .4;
        double fullCircle = 2 * Math.PI;
        int particles = 15;
        long[] tickCounter = { 0 };
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (player.isDead() || !player.isValid() || !player.isOnline()
                    || !player.getWorld().getPlayers().contains(player))
                return;
            if (!plugin.isEffectFormEnabledInWorld(EffectForm.HALO, player.getWorld().getName()))
                return;

            if (haloType == Halo.CROWN) {
                if (tickCounter[0] % 3 != 0) {
                    tickCounter[0]++;
                    return;
                }
                Location base = player.getEyeLocation().add(0, 0.4, 0);
                Particle p = getEffectParticle(haloType);
                for (Player otherPlayer : player.getWorld().getPlayers()) {
                    if (otherPlayer != player && !getEffectsEnabled(otherPlayer))
                        continue;

                    Particle.DustOptions gold = new Particle.DustOptions(Color.fromRGB(255, 215, 0), 0.7f);
                    Particle.DustOptions ruby = new Particle.DustOptions(Color.fromRGB(220, 20, 60), 0.8f);

                    for (int j = 0; j < 360; j += 12) {
                        double rAngle = Math.toRadians(j);
                        Location newLoc = base.clone().add(Math.sin(rAngle) * 0.35, 0, Math.cos(rAngle) * 0.35);
                        otherPlayer.spawnParticle(p, newLoc, 1, 0, 0, 0, 0, gold);

                        if (j % 45 == 0) {
                            Location spikeLoc = newLoc.clone().add(0, 0.1, 0);
                            otherPlayer.spawnParticle(p, spikeLoc, 1, 0, 0, 0, 0, gold);
                            spikeLoc.add(0, 0.1, 0);
                            otherPlayer.spawnParticle(p, spikeLoc, 1, 0, 0, 0, 0, gold);
                            spikeLoc.add(0, 0.1, 0);
                            otherPlayer.spawnParticle(p, spikeLoc, 1, 0, 0, 0, 0, ruby);
                        }
                    }
                }
                tickCounter[0]++;
                return;
            }

            double x = Math.sin(tickCounter[0] * fullCircle / particles) * radius;
            double z = Math.cos(tickCounter[0] * fullCircle / particles) * radius;
            double y = 1.95;

            int xOffset = 0, yOffset = 0, zOffset = 0, speed = 0, count = 1;
            for (Player otherPlayer : player.getWorld().getPlayers()) {
                if (otherPlayer != player && !getEffectsEnabled(otherPlayer))
                    continue;

                if (haloType == Halo.RAINBOW) {
                    int r = (int) (Math.random() * 256), g = (int) (Math.random() * 256),
                            b = (int) (Math.random() * 256);
                    int size = 1;
                    otherPlayer.spawnParticle(
                            getEffectParticle(haloType),
                            player.getLocation().add(x, y, z),
                            count, xOffset, yOffset, zOffset, speed,
                            new Particle.DustOptions(Color.fromRGB(r, g, b), size));
                    continue;
                }
                otherPlayer.spawnParticle(
                        getEffectParticle(haloType),
                        player.getLocation().add(x, y, z),
                        count, xOffset, yOffset, zOffset,
                        speed);
            }

            tickCounter[0]++;
        }, 0L, 1L);

        ActiveEffectData pdata = getPlayerActiveEffectData(player);
        pdata.addTaskId(EffectForm.HALO, taskId);
        if (plugin.debugLevel >= 2)
            log("Registered HALO task for " + player.getName());
    }
}
