package com.siliqon.cosmiccosmetics.utils;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.files.LangFile;
import com.siliqon.cosmiccosmetics.files.MainConfig;
import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import org.bukkit.ChatColor;

import java.io.File;
import java.nio.file.Path;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;

public class Files {
    private final CosmeticsPlugin plugin;

    public Files(CosmeticsPlugin plugin) {
        this.plugin = plugin;
    }

    private void initConfig() {
        YamlConfigurationProperties properties = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
                .header(
                        """
                        Do not add or remove any keys, only edit them
                        Make sure to follow the formatting very strictly
                        """
                )
                .footer("Authors: Siliqon")
                .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
                .setFieldFilter(field -> !field.getName().startsWith("private_"))
                .build();

        Path configFile = new File(plugin.getDataFolder(), "config.yml").toPath();
        plugin.setConfigFile(YamlConfigurations.update(
                configFile, MainConfig.class, properties
        ));
        if (plugin.debugLevel >= 2) log("Initialized config.yml file");
    }
    private void initLangFile() {
        YamlConfigurationProperties properties = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
                .header(
                        """
                        All color codes like &a, &d, &5, etc. are valid
                        Do not add or remove any keys, only edit them
                        Make sure to follow the formatting very strictly
                        """
                )
                .footer("Authors: Siliqon")
                .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
                .setFieldFilter(field -> !field.getName().startsWith("private_"))
                .build();

        Path langFile = new File(plugin.getDataFolder(), "lang.yml").toPath();
        plugin.setLang(YamlConfigurations.update(
                langFile, LangFile.class, properties
        ));
        plugin.PREFIX = ChatColor.translateAlternateColorCodes('&', plugin.getLang().getPrefix());
        if (plugin.debugLevel >= 2) log("Initialized lang.yml file");
    }

    public void initFiles() {
        initConfig();
        initLangFile();
    }
}
