package com.siliqon.cosmiccosmetics.handlers.effects;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.utils.Effects.*;

public class Kill implements Listener {
    private final CosmeticsPlugin plugin;

    public Kill(CosmeticsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Player killer = player.getKiller();
        if (killer == null) return;

        EffectType effectType = getActiveEffect(killer, EffectForm.KILL);
        if (effectType == null) return;

        Particle particle = getEffectParticle(effectType);
        int density = getEffectDensity(effectType);

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (otherPlayer != player && !getEffectsEnabled(otherPlayer))
                continue;
            if (effectType == EffectType.RAINBOW) {
                int count = density * 5, playerLocYOffset = 1, size = 1;
                int r = (int) (Math.random() * 256), g = (int) (Math.random() * 256), b = (int) (Math.random() * 256);
                double xOffset = .3, yOffset = .5, zOffset = .3, speed = .05;

                otherPlayer.spawnParticle(particle,
                        player.getLocation().add(0,playerLocYOffset,0),
                        count, xOffset, yOffset, zOffset, speed,
                        new Particle.DustOptions(Color.fromRGB(r,g,b), size));
                continue;
            }

            int count = density*10, playerLocYOffset = 1;
            double xOffset = .4, yOffset = .75, zOffset = .4, speed = .1;
            otherPlayer.spawnParticle(particle,
                    player.getLocation().add(0,playerLocYOffset,0),
                    count, xOffset, yOffset, zOffset, speed);
        }
        if (plugin.debugLevel >= 2) log("Showed kill effect of " + player.getName());
    }
}
