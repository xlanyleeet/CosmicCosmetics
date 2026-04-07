package com.siliqon.cosmiccosmetics.files;

import com.siliqon.cosmiccosmetics.enums.EffectForm;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.EnumMap;
import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ConfigCache {
    public record ConfigValue(Object value) {
    }

    private final Map<String, ConfigValue> cache;
    private final Map<EffectForm, Set<String>> enabledWorlds;

    private ConfigCache(Map<String, ConfigValue> cache, Map<EffectForm, Set<String>> enabledWorlds) {
        this.cache = cache;
        this.enabledWorlds = enabledWorlds;
    }

    public static ConfigCache load(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        Map<String, ConfigValue> cache = new HashMap<>();
        Map<EffectForm, Set<String>> enabledWorlds = new EnumMap<>(EffectForm.class);

        cache.put("debug-level", new ConfigValue(yaml.getInt("debug-level", 0)));

        cache.put("storage-type", new ConfigValue(yaml.getString("storage-type", "SQLITE")));
        cache.put("sqlite-file-name", new ConfigValue(yaml.getString("sqlite-file-name", "cosmetics.db")));
        cache.put("mariadb-host", new ConfigValue(yaml.getString("mariadb-host", "127.0.0.1")));
        cache.put("mariadb-port", new ConfigValue(yaml.getInt("mariadb-port", 3306)));
        cache.put("mariadb-database", new ConfigValue(yaml.getString("mariadb-database", "cosmiccosmetics")));
        cache.put("mariadb-username", new ConfigValue(yaml.getString("mariadb-username", "root")));
        cache.put("mariadb-password", new ConfigValue(yaml.getString("mariadb-password", "password")));
        cache.put("database-pool-maximum-size", new ConfigValue(yaml.getInt("database-pool-maximum-size", 8)));
        cache.put("database-connection-timeout-ms",
                new ConfigValue(yaml.getLong("database-connection-timeout-ms", 30000L)));

        cache.put("cosmetic-purchasing-enabled", new ConfigValue(yaml.getBoolean("cosmetic-purchasing-enabled", true)));
        cache.put("cosmetic-testing-mode", new ConfigValue(yaml.getBoolean("cosmetic-testing-mode", false)));
        cache.put("default-cosmetic-price", new ConfigValue(yaml.getDouble("default-cosmetic-price", 100.0D)));
        cache.put("default-language", new ConfigValue(yaml.getString("default-language", "en_US")));
        cache.put("per-player-language", new ConfigValue(yaml.getBoolean("per-player-language", true)));

        for (EffectForm form : EffectForm.values()) {
            String path = "enabled-worlds." + form.name().toLowerCase(Locale.ROOT);
            Set<String> worlds = new HashSet<>();
            for (String worldName : yaml.getStringList(path)) {
                if (worldName == null || worldName.isBlank()) {
                    continue;
                }
                worlds.add(worldName.toLowerCase(Locale.ROOT));
            }
            enabledWorlds.put(form, Set.copyOf(worlds));
        }

        return new ConfigCache(cache, enabledWorlds);
    }

    private boolean bool(String key, boolean fallback) {
        Object value = cache.getOrDefault(key, new ConfigValue(fallback)).value();
        if (value instanceof Boolean bool) {
            return bool;
        }
        return fallback;
    }

    private int integer(String key, int fallback) {
        Object value = cache.getOrDefault(key, new ConfigValue(fallback)).value();
        if (value instanceof Number number) {
            return number.intValue();
        }
        return fallback;
    }

    private long longValue(String key, long fallback) {
        Object value = cache.getOrDefault(key, new ConfigValue(fallback)).value();
        if (value instanceof Number number) {
            return number.longValue();
        }
        return fallback;
    }

    private double decimal(String key, double fallback) {
        Object value = cache.getOrDefault(key, new ConfigValue(fallback)).value();
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return fallback;
    }

    private String text(String key, String fallback) {
        Object value = cache.getOrDefault(key, new ConfigValue(fallback)).value();
        if (value instanceof String str) {
            return str;
        }
        return fallback;
    }

    public Integer getDebugLevel() {
        return integer("debug-level", 0);
    }

    public String getStorageType() {
        return text("storage-type", "SQLITE");
    }

    public String getSqliteFileName() {
        return text("sqlite-file-name", "cosmetics.db");
    }

    public String getMariadbHost() {
        return text("mariadb-host", "127.0.0.1");
    }

    public Integer getMariadbPort() {
        return integer("mariadb-port", 3306);
    }

    public String getMariadbDatabase() {
        return text("mariadb-database", "cosmiccosmetics");
    }

    public String getMariadbUsername() {
        return text("mariadb-username", "root");
    }

    public String getMariadbPassword() {
        return text("mariadb-password", "password");
    }

    public Integer getDatabasePoolMaximumSize() {
        return integer("database-pool-maximum-size", 8);
    }

    public Long getDatabaseConnectionTimeoutMs() {
        return longValue("database-connection-timeout-ms", 30000L);
    }

    public Boolean getCosmeticPurchasingEnabled() {
        return bool("cosmetic-purchasing-enabled", true);
    }

    public Double getDefaultCosmeticPrice() {
        return decimal("default-cosmetic-price", 100.0D);
    }

    public Boolean getCosmeticTestingMode() {
        return bool("cosmetic-testing-mode", false);
    }

    public String getDefaultLanguage() {
        return text("default-language", "en_US");
    }

    public Boolean isPerPlayerLanguageEnabled() {
        return bool("per-player-language", true);
    }

    public boolean isWorldEnabled(EffectForm form, String worldName) {
        if (form == null) {
            return true;
        }

        Set<String> worlds = enabledWorlds.get(form);
        if (worlds == null || worlds.isEmpty()) {
            return true;
        }

        if (worldName == null || worldName.isBlank()) {
            return false;
        }

        return worlds.contains(worldName.toLowerCase(Locale.ROOT));
    }
}
