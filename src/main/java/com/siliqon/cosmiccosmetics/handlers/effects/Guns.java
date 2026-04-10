package com.siliqon.cosmiccosmetics.handlers.effects;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.Gun;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.metadata.FixedMetadataValue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;
import java.util.UUID;

import static com.siliqon.cosmiccosmetics.utils.Effects.getActiveEffect;

public class Guns implements Listener {
    private final CosmeticsPlugin plugin;
    private final Cache<UUID, Long> cooldowns = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build();

    public Guns(CosmeticsPlugin plugin) {
        this.plugin = plugin;
    }

    public static void startForPlayer(Player player) {
        Enum<?> activeEffectEnum = getActiveEffect(player, EffectForm.FUNGUN);
        if (!(activeEffectEnum instanceof Gun)) {
            return;
        }
        ItemStack gunItem = new ItemStack(Material.COMPARATOR);
        ItemMeta meta = gunItem.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Cosmetic Gun", NamedTextColor.GOLD));
            gunItem.setItemMeta(meta);
        }
        player.getInventory().setItemInOffHand(gunItem);
    }

    public static void removeForPlayer(Player player) {
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (offHand != null && offHand.getType() == Material.COMPARATOR) {
            if (offHand.hasItemMeta() && offHand.getItemMeta().hasDisplayName()) {
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!player.getInventory().getItemInMainHand().getType().isAir()) {
            return;
        }

        Enum<?> activeEffectEnum = getActiveEffect(player, EffectForm.FUNGUN);
        if (!(activeEffectEnum instanceof Gun)) {
            return;
        }

        if (!plugin.isEffectFormEnabledInWorld(EffectForm.FUNGUN, player.getWorld().getName())) {
            return;
        }

        long now = System.currentTimeMillis();
        Long lastUse = cooldowns.getIfPresent(player.getUniqueId());
        if (lastUse != null && now - lastUse < 1000) { // 1 second cooldown
            return;
        }
        cooldowns.put(player.getUniqueId(), now);

        Gun gunType = (Gun) activeEffectEnum;

        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setVelocity(player.getLocation().getDirection().multiply(1.5));
        projectile.setMetadata("CosmeticGun", new FixedMetadataValue(plugin, gunType.name()));

        player.playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1f, 1f);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Snowball snowball) || !snowball.hasMetadata("CosmeticGun")) {
            return;
        }

        String gunTypeName = snowball.getMetadata("CosmeticGun").get(0).asString();
        Gun gunType;
        try {
            gunType = Gun.valueOf(gunTypeName);
        } catch (Exception e) {
            return;
        }

        Location loc = entity.getLocation();

        Runnable particleTask = null;
        switch (gunType) {
            case SNOWBALL:
                loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 20, 0.5, 0.5, 0.5, 0.1);
                loc.getWorld().playSound(loc, Sound.BLOCK_SNOW_BREAK, 1f, 1f);
                particleTask = () -> loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 5, 0.7, 0.7, 0.7, 0.02);
                break;
            case FIREBALL:
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 30, 0.5, 0.5, 0.5, 0.1);
                loc.getWorld().playSound(loc, Sound.ENTITY_GHAST_SHOOT, 1f, 1f);
                particleTask = () -> {
                    loc.getWorld().spawnParticle(Particle.FLAME, loc, 5, 0.7, 0.7, 0.7, 0.02);
                    loc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 2, 0.7, 0.7, 0.7, 0.02);
                };
                break;
            case EXPLOSION:
                loc.getWorld().spawnParticle(Particle.EXPLOSION, loc, 5, 1, 1, 1, 0);
                loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                particleTask = () -> loc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 10, 1.5, 1.5, 1.5,
                        0.05);
                break;
            case METEOR:
                loc.getWorld().spawnParticle(Particle.LAVA, loc, 40, 1, 1, 1, 0.1);
                loc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 20, 1, 1, 1, 0.05);
                loc.getWorld().playSound(loc, Sound.ITEM_TRIDENT_THUNDER, 1f, 0.5f);
                particleTask = () -> {
                    loc.getWorld().spawnParticle(Particle.LAVA, loc, 10, 1.5, 1.5, 1.5, 0.02);
                    loc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 5, 1.5, 1.5, 1.5, 0.05);
                };
                break;
        }

        if (particleTask != null) {
            Runnable finalParticleTask = particleTask;
            new org.bukkit.scheduler.BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (ticks++ >= 20) {
                        this.cancel();
                        return;
                    }
                    finalParticleTask.run();
                }
            }.runTaskTimer(plugin, 2L, 2L);
        }
    }
}