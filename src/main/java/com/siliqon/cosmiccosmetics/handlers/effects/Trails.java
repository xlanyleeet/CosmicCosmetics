package com.siliqon.cosmiccosmetics.handlers.effects;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.custom.ActiveEffectData;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.Trail;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.utils.Effects.*;

public class Trails {
    private static final CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();

    public static void startForPlayer(Player player) {
        Enum<?> activeEffectEnum = getActiveEffect(player, EffectForm.TRAIL);
        if (!(activeEffectEnum instanceof Trail)) {
            return;
        }
        Trail effectType = (Trail) activeEffectEnum;
        if (!plugin.isEffectFormEnabledInWorld(EffectForm.TRAIL, player.getWorld().getName()))
            return;

        int[] stepIndex = { 0 };
        double[] angle = { 0.0 };
        Location[] lastLoc = { player.getLocation() };

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (player.isDead() || !player.isValid() || !player.isOnline()) {
                return;
            }
            if (!plugin.isEffectFormEnabledInWorld(EffectForm.TRAIL, player.getWorld().getName()))
                return;

            Particle particle = getEffectParticle(effectType);
            Location loc = player.getLocation();
            int density = getEffectDensity(effectType);
            double defaultXOffset = .05, defaultYOffset = .05, defaultZOffset = .05, defaultSpeed = .01;

            boolean isMoving = loc.distanceSquared(lastLoc[0]) > 0.001;
            lastLoc[0] = loc.clone();

            if (!isMoving)
                return;

            Location spawnLoc = loc.clone().add(0, .1, 0);

            if (effectType == Trail.SHADOW_FOOTPRINTS || effectType == Trail.SNOW_FOOTPRINTS
                    || effectType == Trail.ENDER) {

                double pitch = (loc.getPitch() + 90.0F) * Math.PI / 180.0D;
                double yaw = (loc.getYaw() + 90.0F) * Math.PI / 180.0D;
                double xOffset = Math.sin(pitch) * Math.cos(yaw);
                double zOffset = Math.sin(pitch) * Math.sin(yaw);

                double gap = 0.3;
                if (stepIndex[0] % 2 == 0) {
                    spawnLoc.add(zOffset * gap, 0, -xOffset * gap);
                } else {
                    spawnLoc.add(-zOffset * gap, 0, xOffset * gap);
                }

                stepIndex[0]++;
                defaultXOffset = 0;
                defaultYOffset = 0;
                defaultZOffset = 0;
                defaultSpeed = 0;
            } else if (effectType == Trail.FLAME_HELIX || effectType == Trail.REDSTONE_HELIX
                    || effectType == Trail.SOUL_FIRE_HELIX) {
                angle[0] += Math.PI / 8;
                double radius = 0.6;
                double yOffsetHelix = (angle[0] % (Math.PI * 2)) / (Math.PI * 2) * 2.0;
                spawnLoc = loc.clone().add(Math.cos(angle[0]) * radius, yOffsetHelix, Math.sin(angle[0]) * radius);
                defaultXOffset = 0;
                defaultYOffset = 0;
                defaultZOffset = 0;
                defaultSpeed = 0;
            }

            for (Player otherPlayer : player.getWorld().getPlayers()) {
                if (otherPlayer != player && !getEffectsEnabled(otherPlayer))
                    continue;

                if (effectType == Trail.RAINBOW) {
                    int size = 1;
                    int r = (int) (Math.random() * 256), g = (int) (Math.random() * 256),
                            b = (int) (Math.random() * 256);

                    otherPlayer.spawnParticle(
                            particle, spawnLoc, density,
                            new Particle.DustOptions(Color.fromRGB(r, g, b), size));
                    continue;
                }

                otherPlayer.spawnParticle(
                        particle, spawnLoc, density,
                        defaultXOffset, defaultYOffset, defaultZOffset, defaultSpeed);
            }
        }, 0L, 1L);

        ActiveEffectData pdata = getPlayerActiveEffectData(player);
        pdata.addTaskId(EffectForm.TRAIL, taskId);
        if (plugin.debugLevel >= 2)
            log("Registered TRAIL task for " + player.getName());
    }
}
