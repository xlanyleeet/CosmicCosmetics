package com.siliqon.cosmiccosmetics.utils;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.custom.*;
import com.siliqon.cosmiccosmetics.enums.*;
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

    private static final List<Enum<?>> trailEffectTypes = List.of(Trail.values());
    private static final List<Enum<?>> haloEffectTypes = List.of(Halo.values());
    private static final List<Enum<?>> capesEffectTypes = List.of(Cape.values());
    private static final List<Enum<?>> petsEffectTypes = List.of(Pet.values());
    private static final List<Enum<?>> glowEffectTypes = List.of(Glow.values());
    private static final List<Enum<?>> gunEffectTypes = List.of(Gun.values());

    private static final Map<String, Enum<?>> stringToEnumMap = new HashMap<>();

    static {
        List.of(trailEffectTypes, haloEffectTypes, capesEffectTypes, petsEffectTypes, glowEffectTypes, gunEffectTypes)
                .forEach(list -> list.forEach(e -> stringToEnumMap.put(e.name(), e)));
    }

    public static Enum<?> getEnumFromString(String name) {
        if (name == null || name.isEmpty() || name.equals("NONE"))
            return null;
        return stringToEnumMap.get(name);
    }

    public static String getEffectDisplayName(Enum<?> type) {
        if (type == null || plugin.effectNameRegistry.get(type) == null)
            return "NONE";
        return plugin.effectNameRegistry.get(type).toString();
    }

    public static List<String> getEffectDescription(Enum<?> type) {
        if (type == null || plugin.effectDescriptionRegistry.get(type) == null)
            return new ArrayList<>();
        return (List<String>) plugin.effectDescriptionRegistry.get(type);
    }

    public static Material getEffectMaterial(Enum<?> type) {
        if (type == null || plugin.effectMaterialRegistry.get(type) == null)
            return Material.AIR;
        return plugin.effectMaterialRegistry.get(type);
    }

    public static Particle getEffectParticle(Enum<?> type) {
        if (type == null || plugin.effectParticleRegistry.get(type) == null)
            return null;
        return plugin.effectParticleRegistry.get(type);
    }

    public static Integer getEffectDensity(Enum<?> type) {
        if (type == null || plugin.effectDensityRegistry.get(type) == null)
            return 0;
        return plugin.effectDensityRegistry.get(type);
    }

    public static Enum<?> getActiveEffect(Player player, EffectForm form) {
        ActiveEffectData playerData = getPlayerActiveEffectData(player);
        if (playerData == null)
            return null;

        if (plugin.debugLevel >= 2)
            log("Got active effects (" + form + ") for " + player.getName());
        return playerData.getEffects().get(form);
    }

    public static String getActiveEffectName(Player player, EffectForm form) {
        ActiveEffectData playerData = getPlayerActiveEffectData(player);
        if (playerData == null)
            return "NONE";

        Enum<?> type = playerData.getEffects().get(form);
        if (type == null)
            return "NONE";

        return toDisplayCase(type.toString());
    }

    public static Map<EffectForm, Enum<?>> getActiveEffects(Player player) {
        ActiveEffectData playerData = getPlayerActiveEffectData(player);
        if (playerData == null)
            return new HashMap<>();

        if (plugin.debugLevel >= 2)
            log("Retrieved active effects for " + player.getName());
        return playerData.getEffects();
    }

    public static boolean getEffectsEnabled(Player player) {
        if (plugin.getCosmeticsEnabled().get(player.getUniqueId()) == null)
            return false;
        return plugin.getCosmeticsEnabled().get(player.getUniqueId());
    }

    public static ActiveEffectData getPlayerActiveEffectData(Player player) {
        return plugin.getPlayerActiveEffects().get(player.getUniqueId());
    }

    public static void setActiveEffect(Player player, EffectForm form, Enum<?> type) {
        ActiveEffectData playerData = getPlayerActiveEffectData(player);
        if (playerData == null)
            playerData = new ActiveEffectData(player.getUniqueId(), new HashMap<>(), new HashMap<>());

        if (type == null) {
            removeActiveEffect(player, form);
            return;
        }

        removeActiveEffect(player, form);

        playerData.addEffect(form, type);
        if (!plugin.getPlayerActiveEffects().containsKey(player.getUniqueId()))
            plugin.getPlayerActiveEffects().put(player.getUniqueId(), playerData);
        switch (form) {
            case EffectForm.TRAIL: {
                com.siliqon.cosmiccosmetics.handlers.effects.Trails.startForPlayer(player);
                break;
            }
            case EffectForm.HALO: {
                com.siliqon.cosmiccosmetics.handlers.effects.Halos.startForPlayer(player);
                break;
            }
            case EffectForm.GLOW: {
                com.siliqon.cosmiccosmetics.handlers.effects.Glows.startForPlayer(player);
                break;
            }
            case EffectForm.CAPES: {
                com.siliqon.cosmiccosmetics.handlers.effects.Capes.startForPlayer(player);
                break;
            }
            case EffectForm.PETS: {
                com.siliqon.cosmiccosmetics.handlers.effects.Pets.startForPlayer(player);
                break;
            }
            case EffectForm.FUNGUN: {
                com.siliqon.cosmiccosmetics.handlers.effects.Guns.startForPlayer(player);
                break;
            }
        }
        if (plugin.debugLevel >= 2)
            log("Set active effect (" + form + ") for " + player.getName());
    }

    public static void removeActiveEffect(Player player, EffectForm form) {
        ActiveEffectData playerData = getPlayerActiveEffectData(player);
        if (playerData == null)
            return;
        else if (form == EffectForm.PETS) {
            com.siliqon.cosmiccosmetics.handlers.effects.Pets.removeForPlayer(player);
        } else if (form == EffectForm.GLOW) {
            com.siliqon.cosmiccosmetics.handlers.effects.Glows.removeForPlayer(player);
        } else if (form == EffectForm.FUNGUN) {
            com.siliqon.cosmiccosmetics.handlers.effects.Guns.removeForPlayer(player);
        }

        playerData.removeEffect(form);
        if (plugin.debugLevel >= 2)
            log("Removed effect (" + form + ") for " + player.getName());
    }

    public static void removeAllActiveEffects(Player player) {
        Map<EffectForm, Enum<?>> current = new HashMap<>(getActiveEffects(player));
        for (EffectForm form : current.keySet()) {
            removeActiveEffect(player, form);
        }
    }

    public static List<Enum<?>> getFormEffects(EffectForm form) {
        if (plugin.getCosmeticRules() != null) {
            List<Enum<?>> configuredEffects = plugin.getCosmeticRules().getFormEffects(form);
            if (!configuredEffects.isEmpty()) {
                return configuredEffects;
            }
        }

        if (form == EffectForm.TRAIL) {
            return trailEffectTypes;
        } else if (form == EffectForm.HALO) {
            return haloEffectTypes;
        } else if (form == EffectForm.CAPES) {
            return capesEffectTypes;
        } else if (form == EffectForm.PETS) {
            return petsEffectTypes;
        } else if (form == EffectForm.FUNGUN) {
            return gunEffectTypes;
        } else if (form == EffectForm.GLOW) {
            return glowEffectTypes;
        }

        return new ArrayList<>();
    }
}
