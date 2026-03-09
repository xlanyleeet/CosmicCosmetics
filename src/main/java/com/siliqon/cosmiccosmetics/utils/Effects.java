package com.siliqon.cosmiccosmetics.utils;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.custom.*;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import com.siliqon.cosmiccosmetics.handlers.effects.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.utils.Utils.toDisplayCase;

public class Effects {
    private static final CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();

    private static final List<EffectType> projectileEffectTypes = List.of(EffectType.SPLASH, EffectType.ENDER, EffectType.FLAME, EffectType.CHARM, EffectType.LOVE, EffectType.BLOSSOM, EffectType.ENCHANTED, EffectType.TEARS, EffectType.MUSICAL);
    private static final List<EffectType> trailEffectTypes = List.of(EffectType.BLOSSOM, EffectType.RAINBOW, EffectType.CHARM, EffectType.LOVE, EffectType.MUSICAL, EffectType.CLOUDY, EffectType.ENDER);
    private static final List<EffectType> killEffectTypes = List.of(EffectType.ENDER, EffectType.FLAME, EffectType.CHARM, EffectType.LOVE, EffectType.BLOSSOM, EffectType.ENCHANTED, EffectType.TEARS, EffectType.RAINBOW, EffectType.CLOUDY);
    private static final List<EffectType> haloEffectTypes = List.of(EffectType.SPLASH, EffectType.ENDER, EffectType.FLAME, EffectType.CHARM, EffectType.LOVE, EffectType.BLOSSOM, EffectType.ENCHANTED, EffectType.TEARS, EffectType.RAINBOW);

    public static String getEffectDisplayName(EffectType type) {
        if (type == null || plugin.effectNameRegistry.get(type) == null) return "NONE";
        return plugin.effectNameRegistry.get(type).toString();
    }
    public static List<String> getEffectDescription(EffectType type) {
        if (type == null || plugin.effectDescriptionRegistry.get(type) == null) return new ArrayList<>();
        return (List<String>) plugin.effectDescriptionRegistry.get(type);
    }
    public static Material getEffectMaterial(EffectType type) {
        if (type == null || plugin.effectMaterialRegistry.get(type) == null) return Material.AIR;
        return Material.valueOf(plugin.effectMaterialRegistry.get(type).toString());
    }
    public static Particle getEffectParticle(EffectType type) {
        if (type == null || plugin.effectParticleRegistry.get(type) == null) return null;
        return Particle.valueOf(plugin.effectParticleRegistry.get(type).toString());
    }
    public static Integer getEffectDensity(EffectType type) {
        if (type == null || plugin.effectDensityRegistry.get(type) == null) return 0;
        return Integer.parseInt(plugin.effectDensityRegistry.get(type).toString());
    }

    public static EffectType getActiveEffect(Player player, EffectForm form) {
        ActiveEffectData playerData = getPlayerActiveEffectData(player);
        if (playerData == null) return null;

        if (plugin.debugLevel >=2) log("Got active effects (" + form + ") for "+player.getName());
        return playerData.getEffects().get(form);
    }
    public static String getActiveEffectName(Player player, EffectForm form) {
        ActiveEffectData playerData = getPlayerActiveEffectData(player);
        if (playerData == null) return "NONE";

        EffectType type = playerData.getEffects().get(form);
        if (type == null) return "NONE";

        return toDisplayCase(type.toString());
    }
    public static Map<EffectForm, EffectType> getActiveEffects(Player player) {
        ActiveEffectData playerData = getPlayerActiveEffectData(player);
        if (playerData == null) return new HashMap<>();

        if (plugin.debugLevel >= 2) log("Retrieved active effects for "+ player.getName());
        return playerData.getEffects();
    }

    public static boolean getEffectsEnabled(Player player) {
        if (plugin.cosmeticsEnabled.get(player.getUniqueId()) == null) return false;
        return plugin.cosmeticsEnabled.get(player.getUniqueId());
    }

    public static ActiveEffectData getPlayerActiveEffectData(Player player) {
        return plugin.playerActiveEffects.get(player.getUniqueId());
    }

    public static void setActiveEffect(Player player, EffectForm form, EffectType type) {
        ActiveEffectData playerData = getPlayerActiveEffectData(player);
        if (playerData == null) playerData = new ActiveEffectData(player.getUniqueId(), new HashMap<>(), new HashMap<>());

        if (type == null) {
            playerData.removeEffect(form);
            return;
        }

        playerData.addEffect(form, type);
        if (!plugin.playerActiveEffects.containsKey(player.getUniqueId())) plugin.playerActiveEffects.put(player.getUniqueId(), playerData);
        switch (form) {
            case EffectForm.PROJECTILE: {
                Projectile.startForPlayer(player);
            }
            case EffectForm.TRAIL: {
                Trail.startForPlayer(player);
            }
            case EffectForm.HALO: {
                Halo.startForPlayer(player);
            }
            // kill is event based
        }
        if (plugin.debugLevel >= 2) log("Set active effect ("+form+") for " + player.getName());
    }

    public static void removeActiveEffect(Player player, EffectForm form) {
        ActiveEffectData playerData = getPlayerActiveEffectData(player);
        if (playerData == null) return;

        playerData.removeEffect(form);
        if (plugin.debugLevel >= 2) log("Removed effect ("+form+") for " + player.getName());
    }
    public static void removeAllActiveEffects(Player player) {
        Map<EffectForm, EffectType> current = new HashMap<>(getActiveEffects(player));
        for (EffectForm form : current.keySet()) {
            removeActiveEffect(player, form);
        }
    }

    public static List<EffectType> getFormEffects(EffectForm form) {
        if (form == EffectForm.PROJECTILE) {
            return projectileEffectTypes;
        } else if (form == EffectForm.TRAIL) {
            return trailEffectTypes;
        } else if (form == EffectForm.KILL) {
            return killEffectTypes;
        } else if (form == EffectForm.HALO) {
            return haloEffectTypes;
        }

        return new ArrayList<>();
    }
}
