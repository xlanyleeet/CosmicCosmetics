package com.siliqon.cosmiccosmetics.utils;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.files.ConfigCache;
import com.siliqon.cosmiccosmetics.files.CosmeticRulesCache;
import com.siliqon.cosmiccosmetics.files.LanguageCache;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;

public class Files {
        private final CosmeticsPlugin plugin;

        public Files(CosmeticsPlugin plugin) {
                this.plugin = plugin;
        }

        private void initConfig() {
                File configFile = new File(plugin.getDataFolder(), "config.yml");
                if (!configFile.exists()) {
                        plugin.saveResource("config.yml", false);
                }

                syncMissingResourceKeys(configFile, "config.yml");

                plugin.setConfigFile(ConfigCache.load(configFile));
                if (plugin.debugLevel >= 2) {
                        log("Initialized config.yml file");
                }
        }

        private void initLangFile() {
                File langDirectory = new File(plugin.getDataFolder(), "lang");
                if (!langDirectory.exists()) {
                        langDirectory.mkdirs();
                }

                migrateLegacyLanguageFile(langDirectory);

                ensureLanguageFile("en_US.yml");
                ensureLanguageFile("uk_UA.yml");

                YamlConfiguration defaults = loadBundledEnglishDefaults();

                File[] languageFiles = langDirectory.listFiles(
                                (dir, name) -> name.toLowerCase(Locale.ROOT).endsWith(".yml"));
                if (languageFiles != null) {
                        for (File languageFile : languageFiles) {
                                syncMissingLanguageKeys(languageFile, defaults);
                        }
                }

                Map<String, LanguageCache> languages = new HashMap<>();
                if (languageFiles != null) {
                        for (File languageFile : languageFiles) {
                                String name = languageFile.getName();
                                String code = name.substring(0, name.length() - 4);
                                languages.put(normalizeLanguageCode(code), LanguageCache.load(languageFile));
                        }
                }

                String defaultLanguage = normalizeLanguageCode(plugin.getConfigFile().getDefaultLanguage());
                plugin.setLanguages(languages, defaultLanguage);
                plugin.PREFIX = plugin.getLang().getPrefix();
                if (plugin.debugLevel >= 2) {
                        log("Initialized lang/*.yml files");
                }
        }

        private void migrateLegacyLanguageFile(File langDirectory) {
                File legacyFile = new File(plugin.getDataFolder(), "lang.yml");
                File englishFile = new File(langDirectory, "en_US.yml");
                if (!legacyFile.exists() || englishFile.exists()) {
                        return;
                }

                try {
                        java.nio.file.Files.move(legacyFile.toPath(), englishFile.toPath());
                } catch (Exception exception) {
                        log("Failed to migrate legacy lang.yml to lang/en_US.yml");
                        exception.printStackTrace();
                }
        }

        private void ensureLanguageFile(String fileName) {
                File target = new File(plugin.getDataFolder(), "lang/" + fileName);
                if (!target.exists()) {
                        plugin.saveResource("lang/" + fileName, false);
                }
        }

        private YamlConfiguration loadBundledEnglishDefaults() {
                try (InputStream stream = plugin.getResource("lang/en_US.yml")) {
                        if (stream != null) {
                                return YamlConfiguration.loadConfiguration(
                                                new InputStreamReader(stream, StandardCharsets.UTF_8));
                        }
                } catch (Exception ignored) {
                }

                File fallback = new File(plugin.getDataFolder(), "lang/en_US.yml");
                return YamlConfiguration.loadConfiguration(fallback);
        }

        private void syncMissingLanguageKeys(File languageFile, YamlConfiguration defaults) {
                YamlConfiguration current = YamlConfiguration.loadConfiguration(languageFile);
                boolean changed = false;

                for (String key : defaults.getKeys(true)) {
                        if (defaults.isConfigurationSection(key)) {
                                continue;
                        }
                        if (current.contains(key)) {
                                continue;
                        }

                        current.set(key, defaults.get(key));
                        changed = true;
                }

                if (!changed) {
                        return;
                }

                try {
                        current.save(languageFile);
                } catch (Exception exception) {
                        log("Failed to sync language keys for " + languageFile.getName());
                        exception.printStackTrace();
                }
        }

        private void syncMissingResourceKeys(File targetFile, String resourcePath) {
                YamlConfiguration current = YamlConfiguration.loadConfiguration(targetFile);
                YamlConfiguration defaults = loadBundledYaml(resourcePath, targetFile);
                boolean changed = false;

                for (String key : defaults.getKeys(true)) {
                        if (defaults.isConfigurationSection(key)) {
                                continue;
                        }
                        if (current.contains(key)) {
                                continue;
                        }

                        current.set(key, defaults.get(key));
                        changed = true;
                }

                if (!changed) {
                        return;
                }

                try {
                        current.save(targetFile);
                } catch (Exception exception) {
                        log("Failed to sync missing keys for " + targetFile.getName());
                        exception.printStackTrace();
                }
        }

        private YamlConfiguration loadBundledYaml(String resourcePath, File fallbackFile) {
                try (InputStream stream = plugin.getResource(resourcePath)) {
                        if (stream != null) {
                                return YamlConfiguration.loadConfiguration(
                                                new InputStreamReader(stream, StandardCharsets.UTF_8));
                        }
                } catch (Exception ignored) {
                }

                return YamlConfiguration.loadConfiguration(fallbackFile);
        }

        private String normalizeLanguageCode(String languageCode) {
                if (languageCode == null || languageCode.isBlank()) {
                        return "en_US";
                }

                String code = languageCode.replace('-', '_');
                String[] parts = code.split("_");
                if (parts.length == 1) {
                        return parts[0].toLowerCase(Locale.ROOT);
                }
                return parts[0].toLowerCase(Locale.ROOT) + "_" + parts[1].toUpperCase(Locale.ROOT);
        }

        private void initCosmeticRuleFiles() {
                File cosmeticsDir = new File(plugin.getDataFolder(), "cosmetics");
                if (!cosmeticsDir.exists()) {
                        cosmeticsDir.mkdirs();
                }

                ensureCosmeticRuleFile("projectile.yml");
                ensureCosmeticRuleFile("trail.yml");
                ensureCosmeticRuleFile("kill.yml");
                ensureCosmeticRuleFile("halo.yml");
                ensureCosmeticRuleFile("capes.yml");
                ensureCosmeticRuleFile("balloons.yml");
                ensureCosmeticRuleFile("pets.yml");

                syncMissingResourceKeys(new File(cosmeticsDir, "projectile.yml"), "cosmetics/projectile.yml");
                syncMissingResourceKeys(new File(cosmeticsDir, "trail.yml"), "cosmetics/trail.yml");
                syncMissingResourceKeys(new File(cosmeticsDir, "kill.yml"), "cosmetics/kill.yml");
                syncMissingResourceKeys(new File(cosmeticsDir, "halo.yml"), "cosmetics/halo.yml");
                syncMissingResourceKeys(new File(cosmeticsDir, "capes.yml"), "cosmetics/capes.yml");
                syncMissingResourceKeys(new File(cosmeticsDir, "balloons.yml"), "cosmetics/balloons.yml");
                syncMissingResourceKeys(new File(cosmeticsDir, "pets.yml"), "cosmetics/pets.yml");

                plugin.setCosmeticRules(CosmeticRulesCache.load(cosmeticsDir,
                                plugin.getConfigFile().getDefaultCosmeticPrice()));
                if (plugin.debugLevel >= 2) {
                        log("Initialized cosmetics/*.yml files");
                }
        }

        private void ensureCosmeticRuleFile(String fileName) {
                File target = new File(plugin.getDataFolder(), "cosmetics/" + fileName);
                if (!target.exists()) {
                        plugin.saveResource("cosmetics/" + fileName, false);
                }
        }

        public void initFiles() {
                initConfig();
                initLangFile();
                initCosmeticRuleFiles();
        }
}
