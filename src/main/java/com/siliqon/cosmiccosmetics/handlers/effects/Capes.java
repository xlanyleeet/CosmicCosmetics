package com.siliqon.cosmiccosmetics.handlers.effects;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.custom.ActiveEffectData;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.Cape;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.utils.Effects.getActiveEffect;
import static com.siliqon.cosmiccosmetics.utils.Effects.getEffectParticle;
import static com.siliqon.cosmiccosmetics.utils.Effects.getEffectsEnabled;
import static com.siliqon.cosmiccosmetics.utils.Effects.getPlayerActiveEffectData;

public class Capes {
    private static final CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();

    public static void startForPlayer(Player player) {
        Enum<?> activeEffectEnum = getActiveEffect(player, EffectForm.CAPES);
        if (!(activeEffectEnum instanceof Cape)) {
            return;
        }
        Cape effectType = (Cape) activeEffectEnum;
        if (!plugin.isEffectFormEnabledInWorld(EffectForm.CAPES, player.getWorld().getName())) {
            return;
        }

        ActiveEffectData pdata = getPlayerActiveEffectData(player);
        if (pdata == null) {
            return;
        }

        if (pdata.getTaskIds().containsKey(EffectForm.CAPES)) {
            for (int taskId : pdata.getTaskIds().get(EffectForm.CAPES)) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
            pdata.getTaskIds().remove(EffectForm.CAPES);
        }

        long[] tick = { 0L };
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (player.isDead() || !player.isValid() || !player.isOnline()) {
                return;
            }
            if (!plugin.isEffectFormEnabledInWorld(EffectForm.CAPES, player.getWorld().getName())) {
                return;
            }

            Location origin = player.getLocation().add(0.0, 1.45, 0.0);
            Vector facing = player.getLocation().getDirection().setY(0.0);
            if (facing.lengthSquared() < 1.0E-6) {
                facing = new Vector(0.0, 0.0, 1.0);
            } else {
                facing.normalize();
            }

            Vector back = facing.clone().multiply(-0.30);
            Vector right = new Vector(-facing.getZ(), 0.0, facing.getX()).normalize();
            Particle particle = getEffectParticle(effectType);
            Particle.DustOptions staticDustOptions = effectType == Cape.CAPE_RAINBOW
                    ? null
                    : dustFor(effectType);

            for (Player otherPlayer : player.getWorld().getPlayers()) {
                if (otherPlayer != player && !getEffectsEnabled(otherPlayer)) {
                    continue;
                }

                for (int row = 0; row < 10; row++) {
                    double yOffset = -0.09 * row;
                    double rowDepth = row * 0.032;
                    double sway = Math.sin((tick[0] + row * 3) * 0.26) * 0.03;

                    for (int col = -2; col <= 2; col++) {
                        double xOffset = col * 0.075;
                        Location point = origin.clone()
                                .add(back)
                                .add(back.clone().multiply(rowDepth))
                                .add(right.clone().multiply(xOffset))
                                .add(back.clone().multiply(sway))
                                .add(0.0, yOffset, 0.0);

                        Particle.DustOptions dustOptions = staticDustOptions != null
                                ? staticDustOptions
                                : dustFor(effectType);
                        if (dustOptions == null) {
                            otherPlayer.spawnParticle(particle, point, 1, 0.0, 0.0, 0.0, 0.0);
                        } else {
                            otherPlayer.spawnParticle(
                                    particle,
                                    point,
                                    1,
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.0,
                                    dustOptions,
                                    otherPlayer == player);
                        }
                    }
                }
            }

            tick[0]++;
        }, 0L, 1L);

        pdata.addTaskId(EffectForm.CAPES, taskId);
        if (plugin.debugLevel >= 2) {
            log("Registered CAPES task for " + player.getName());
        }
    }

    private static Particle.DustOptions dustFor(Cape effectType) {
        int size = 1;
        return switch (effectType) {
            case CAPE_CRIMSON -> new Particle.DustOptions(Color.fromRGB(214, 48, 49), size);
            case CAPE_AZURE -> new Particle.DustOptions(Color.fromRGB(52, 152, 219), size);
            case CAPE_EMERALD -> new Particle.DustOptions(Color.fromRGB(39, 174, 96), size);
            case CAPE_VIOLET -> new Particle.DustOptions(Color.fromRGB(142, 68, 173), size);
            case CAPE_RAINBOW -> {
                int r = (int) (Math.random() * 256);
                int g = (int) (Math.random() * 256);
                int b = (int) (Math.random() * 256);
                yield new Particle.DustOptions(Color.fromRGB(r, g, b), size);
            }
            case CAPE_GOLD -> new Particle.DustOptions(Color.fromRGB(241, 196, 15), size);
            case CAPE_ICE -> new Particle.DustOptions(Color.fromRGB(173, 232, 244), size);
            case CAPE_VOID -> new Particle.DustOptions(Color.fromRGB(36, 22, 55), size);
            case CAPE_SUNSET -> new Particle.DustOptions(Color.fromRGB(255, 126, 95), size);
            default -> null;
        };
    }
}
