package com.siliqon.cosmiccosmetics.files;

import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CosmeticRulesCache {
    public record EffectRule(double price, String permission, boolean permissionOnly) {
    }

    private final Map<EffectForm, List<EffectType>> formEffects;
    private final Map<EffectForm, Map<EffectType, EffectRule>> rules;

    private CosmeticRulesCache(
            Map<EffectForm, List<EffectType>> formEffects,
            Map<EffectForm, Map<EffectType, EffectRule>> rules) {
        this.formEffects = formEffects;
        this.rules = rules;
    }

    public static CosmeticRulesCache load(File cosmeticsDir, double defaultPrice) {
        Map<EffectForm, List<EffectType>> formEffects = new EnumMap<>(EffectForm.class);
        Map<EffectForm, Map<EffectType, EffectRule>> rules = new EnumMap<>(EffectForm.class);

        loadForm(cosmeticsDir, EffectForm.PROJECTILE, "projectile.yml", defaultPrice, false, formEffects, rules);
        loadForm(cosmeticsDir, EffectForm.TRAIL, "trail.yml", defaultPrice, false, formEffects, rules);
        loadForm(cosmeticsDir, EffectForm.KILL, "kill.yml", defaultPrice, false, formEffects, rules);
        loadForm(cosmeticsDir, EffectForm.HALO, "halo.yml", defaultPrice, false, formEffects, rules);
        loadForm(cosmeticsDir, EffectForm.CAPES, "capes.yml", defaultPrice, false, formEffects, rules);
        loadForm(cosmeticsDir, EffectForm.BALLOONS, "balloons.yml", defaultPrice, false, formEffects, rules);
        loadForm(cosmeticsDir, EffectForm.PETS, "pets.yml", 0.0D, true, formEffects, rules);

        return new CosmeticRulesCache(formEffects, rules);
    }

    private static void loadForm(
            File cosmeticsDir,
            EffectForm form,
            String fileName,
            double defaultPrice,
            boolean defaultPermissionOnly,
            Map<EffectForm, List<EffectType>> formEffects,
            Map<EffectForm, Map<EffectType, EffectRule>> rules) {
        File file = new File(cosmeticsDir, fileName);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection effectsSection = yaml.getConfigurationSection("effects");

        List<EffectType> effects = new ArrayList<>();
        Map<EffectType, EffectRule> formRules = new EnumMap<>(EffectType.class);

        if (effectsSection != null) {
            for (String effectKey : effectsSection.getKeys(false)) {
                EffectType effectType;
                try {
                    effectType = EffectType.valueOf(effectKey.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException ignored) {
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

        for (EffectType effectType : defaultEffectsForForm(form)) {
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

    private static String defaultPermission(EffectForm form, EffectType effectType) {
        return "cosmetics." + form.name().toLowerCase(Locale.ROOT) + "." + effectType.name().toLowerCase(Locale.ROOT);
    }

    private static List<EffectType> defaultEffectsForForm(EffectForm form) {
        if (form != EffectForm.CAPES) {
            return List.of();
        }

        return List.of(
                EffectType.CAPE_CRIMSON,
                EffectType.CAPE_AZURE,
                EffectType.CAPE_EMERALD,
                EffectType.CAPE_VIOLET,
                EffectType.CAPE_RAINBOW,
                EffectType.CAPE_GOLD,
                EffectType.CAPE_ICE,
                EffectType.CAPE_VOID,
                EffectType.CAPE_SUNSET);
    }

    public List<EffectType> getFormEffects(EffectForm form) {
        return formEffects.getOrDefault(form, List.of());
    }

    public String resolvePermission(EffectForm form, EffectType effectType) {
        EffectRule rule = getRule(form, effectType);
        if (rule == null || rule.permission() == null || rule.permission().isBlank()) {
            return defaultPermission(form, effectType);
        }
        return rule.permission();
    }

    public boolean isPermissionOnly(EffectForm form, EffectType effectType) {
        EffectRule rule = getRule(form, effectType);
        if (rule == null) {
            return form == EffectForm.PETS;
        }
        return rule.permissionOnly();
    }

    public double getPrice(EffectForm form, EffectType effectType, double defaultPrice) {
        EffectRule rule = getRule(form, effectType);
        if (rule == null) {
            return Math.max(0.0D, defaultPrice);
        }
        return Math.max(0.0D, rule.price());
    }

    private EffectRule getRule(EffectForm form, EffectType effectType) {
        Map<EffectType, EffectRule> formRules = rules.get(form);
        if (formRules == null) {
            return null;
        }
        return formRules.get(effectType);
    }
}
