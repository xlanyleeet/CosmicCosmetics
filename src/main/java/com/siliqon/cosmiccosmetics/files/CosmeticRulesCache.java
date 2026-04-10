package com.siliqon.cosmiccosmetics.files;

import com.siliqon.cosmiccosmetics.enums.Cape;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.Halo;
import com.siliqon.cosmiccosmetics.enums.Pet;
import com.siliqon.cosmiccosmetics.enums.Trail;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CosmeticRulesCache {
    public record EffectRule(double price, String permission, boolean permissionOnly) {}

    private final Map<EffectForm, List<Enum<?>>> formEffects;
    private final Map<EffectForm, Map<Enum<?>, EffectRule>> rules;

    private CosmeticRulesCache(
            Map<EffectForm, List<Enum<?>>> formEffects,
            Map<EffectForm, Map<Enum<?>, EffectRule>> rules) {
        this.formEffects = formEffects;
        this.rules = rules;
    }

    public static CosmeticRulesCache load(File cosmeticsDir, double defaultPrice) {
        Map<EffectForm, List<Enum<?>>> formEffects = new EnumMap<>(EffectForm.class);
        Map<EffectForm, Map<Enum<?>, EffectRule>> rules = new EnumMap<>(EffectForm.class);

        loadForm(cosmeticsDir, EffectForm.TRAIL, "trail.yml", defaultPrice, false, formEffects, rules);
        loadForm(cosmeticsDir, EffectForm.HALO, "halo.yml", defaultPrice, false, formEffects, rules);
        loadForm(cosmeticsDir, EffectForm.CAPES, "capes.yml", defaultPrice, false, formEffects, rules);
        loadForm(cosmeticsDir, EffectForm.PETS, "pets.yml", 0.0D, true, formEffects, rules);
        loadForm(cosmeticsDir, EffectForm.FUNGUN, "guns.yml", 0.0D, true, formEffects, rules);

        return new CosmeticRulesCache(formEffects, rules);
    }

    private static Enum<?> parseEffectType(EffectForm form, String key) {
        try {
            return switch (form) {
                case CAPES -> Cape.valueOf(key);
                case HALO -> Halo.valueOf(key);
                case PETS -> Pet.valueOf(key);
                case TRAIL -> Trail.valueOf(key);                  case FUNGUN -> com.siliqon.cosmiccosmetics.enums.Gun.valueOf(key);                default -> null;
            };
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static void loadForm(
            File cosmeticsDir,
            EffectForm form,
            String fileName,
            double defaultPrice,
            boolean defaultPermissionOnly,
            Map<EffectForm, List<Enum<?>>> formEffects,
            Map<EffectForm, Map<Enum<?>, EffectRule>> rules) {
        File file = new File(cosmeticsDir, fileName);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection effectsSection = yaml.getConfigurationSection("effects");

        List<Enum<?>> effects = new ArrayList<>();
        Map<Enum<?>, EffectRule> formRules = new HashMap<>();

        if (effectsSection != null) {
            for (String effectKey : effectsSection.getKeys(false)) {
                Enum<?> effectType = parseEffectType(form, effectKey.toUpperCase(Locale.ROOT));
                if (effectType == null) {
                    continue;
                }

                String path = "effects." + effectKey;
                double price = yaml.getDouble(path + ".price", defaultPrice);
                String permission = yaml.getString(path + ".permission", defaultPermission(form, effectType));
                boolean permissionOnly = yaml.getBoolean(path + ".permission-only", defaultPermissionOnly);

                effects.add(effectType);
                formRules.put(effectType, new EffectRule(Math.max(0.0D, price), permission, permissionOnly));
            }
        }

        for (Enum<?> effectType : defaultEffectsForForm(form)) {
            if (formRules.containsKey(effectType)) {
                continue;
            }

            effects.add(effectType);
            formRules.put(effectType, new EffectRule(
                    Math.max(0.0D, defaultPrice),
                    defaultPermission(form, effectType),
                    defaultPermissionOnly));
        }

        formEffects.put(form, List.copyOf(effects));
        rules.put(form, formRules);
    }

    private static String defaultPermission(EffectForm form, Enum<?> effectType) {
        return "cosmetics." + form.name().toLowerCase(Locale.ROOT) + "." + effectType.name().toLowerCase(Locale.ROOT);
    }

    private static List<Enum<?>> defaultEffectsForForm(EffectForm form) {
        if (form != EffectForm.CAPES) {
            return List.of();
        }

        return List.of(
                Cape.CAPE_CRIMSON,
                Cape.CAPE_AZURE,
                Cape.CAPE_EMERALD,
                Cape.CAPE_VIOLET,
                Cape.CAPE_RAINBOW,
                Cape.CAPE_GOLD,
                Cape.CAPE_ICE,
                Cape.CAPE_VOID,
                Cape.CAPE_SUNSET);
    }

    public List<Enum<?>> getFormEffects(EffectForm form) {
        return formEffects.getOrDefault(form, List.of());
    }

    public String resolvePermission(EffectForm form, Enum<?> effectType) {
        EffectRule rule = getRule(form, effectType);
        if (rule == null || rule.permission() == null || rule.permission().isBlank()) {
            return defaultPermission(form, effectType);
        }
        return rule.permission();
    }

    public boolean isPermissionOnly(EffectForm form, Enum<?> effectType) {
        EffectRule rule = getRule(form, effectType);
        if (rule == null) {
            return form == EffectForm.PETS;
        }
        return rule.permissionOnly();
    }

    public double getPrice(EffectForm form, Enum<?> effectType, double defaultPrice) {
        EffectRule rule = getRule(form, effectType);
        if (rule == null) {
            return Math.max(0.0D, defaultPrice);
        }
        return Math.max(0.0D, rule.price());
    }

    private EffectRule getRule(EffectForm form, Enum<?> effectType) {
        Map<Enum<?>, EffectRule> formRules = rules.get(form);
        if (formRules == null) {
            return null;
        }
        return formRules.get(effectType);
    }
}

