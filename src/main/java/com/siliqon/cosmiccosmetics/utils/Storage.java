package com.siliqon.cosmiccosmetics.utils;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.custom.ActiveEffectData;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import redempt.crunch.data.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.logError;
import static com.siliqon.cosmiccosmetics.utils.Effects.getEffectsEnabled;
import static com.siliqon.cosmiccosmetics.utils.Effects.getPlayerActiveEffectData;

public class Storage {
    private final CosmeticsPlugin plugin;
    private File dataFile; private FileConfiguration data;

    public Storage(CosmeticsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            plugin.saveResource("data.yml", false);
        }

        data = YamlConfiguration.loadConfiguration(dataFile);
        if (plugin.debugLevel >= 2) log("Loaded data.yml file");
    }

    public Pair<Boolean, ActiveEffectData> getPlayerData(UUID playerUUID) {
        ActiveEffectData pdata = new ActiveEffectData(playerUUID, new HashMap<>(), new HashMap<>());
        ConfigurationSection section = data.getConfigurationSection(playerUUID.toString());

        if (section == null) return new Pair<>(false, pdata);

        boolean enabled = false;
        if (section.contains("enabled")) enabled = section.getBoolean("enabled");

        ConfigurationSection effectsData = section.getConfigurationSection("effects");
        if (effectsData == null) return new Pair<>(enabled, pdata);

        Map<EffectForm, EffectType> activeEffects = new HashMap<>();
        for (String key : effectsData.getKeys(false)) {
            activeEffects.put(EffectForm.valueOf(key), EffectType.valueOf(effectsData.getString(key)));
        }
        pdata.setEffects(activeEffects);

        if (plugin.debugLevel >= 2) log("Retrieved data for " + Bukkit.getPlayer(playerUUID).getName());
        return new Pair<>(enabled, pdata);
    }
    public void savePlayerData(UUID playerUUID, Boolean enabled, ActiveEffectData pdata) {
        data.set(playerUUID.toString(), null);

        ConfigurationSection section = data.getConfigurationSection(playerUUID.toString());

        if (section == null) section = data.createSection(playerUUID.toString());
        section.set("enabled", enabled);

        if (pdata == null) return;

        ConfigurationSection effectsSection = section.getConfigurationSection("effects");
        if (effectsSection == null) effectsSection = section.createSection("effects");

        for (EffectForm form : pdata.getEffects().keySet()) {
            EffectType type = pdata.getEffects().get(form);
            effectsSection.set(form.toString(), type.toString());
        }

        try {
            data.save(dataFile);
        } catch (Exception e) {
            logError("Failed to save player data to file");
            e.printStackTrace();
        }
        if (plugin.debugLevel >= 2) log("Saved data for " + Bukkit.getPlayer(playerUUID).getName());
    }
    public void saveAllData(boolean log) {
        Bukkit.getOnlinePlayers().forEach(player ->
                savePlayerData(player.getUniqueId(), getEffectsEnabled(player), getPlayerActiveEffectData(player))
        );
        try {
            data.save(dataFile);
        } catch (Exception e) {
            logError("Failed to save all data to file");
            e.printStackTrace();
        }
        if (plugin.debugLevel >= 1) log("Saved all data");
    }
}
