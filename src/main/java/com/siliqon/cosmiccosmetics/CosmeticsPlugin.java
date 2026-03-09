package com.siliqon.cosmiccosmetics;

import co.aikar.commands.PaperCommandManager;
import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.siliqon.cosmiccosmetics.commands.CosmeticsCommand;
import com.siliqon.cosmiccosmetics.custom.ActiveEffectData;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.files.*;
import com.siliqon.cosmiccosmetics.handlers.effects.Halo;
import com.siliqon.cosmiccosmetics.handlers.effects.Kill;
import com.siliqon.cosmiccosmetics.handlers.effects.Projectile;
import com.siliqon.cosmiccosmetics.handlers.effects.Trail;
import com.siliqon.cosmiccosmetics.listeners.PlayerListener;
import com.siliqon.cosmiccosmetics.listeners.ServerListener;
import com.siliqon.cosmiccosmetics.registries.*;
import com.siliqon.cosmiccosmetics.utils.Files;
import com.siliqon.cosmiccosmetics.utils.Storage;
import com.siliqon.cosmiccosmetics.guis.lib.*;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.crunch.data.Pair;

import java.util.*;

import static com.siliqon.cosmiccosmetics.utils.Effects.getPlayerActiveEffectData;

public final class CosmeticsPlugin extends JavaPlugin {
    private static CosmeticsPlugin INSTANCE;
    public final String PLUGIN_VERSION = "v"+getDescription().getVersion();

    public NamespacedKey customItemKey = new NamespacedKey(this, "menu-item");
    public String PREFIX = "&b&lCosmetics &7> &r ";
    public final String SPIGOT_RESOURCE_ID = "104768";

    public EffectNameRegistry effectNameRegistry;
    public EffectDescriptionRegistry effectDescriptionRegistry;
    public EffectMaterialRegistry effectMaterialRegistry;
    public EffectParticleRegistry effectParticleRegistry;
    public EffectDensityRegistry effectDensityRegistry;

    private PaperCommandManager commandManager;
    private GUIManager guiManager;
    private GUIListener guiListener;

    private MainConfig config;
    private LangFile lang;

    private boolean vaultEnabled = false;
    private Permission vaultPerms = null;

    public int debugLevel = 0;

    public Map<UUID, ActiveEffectData> playerActiveEffects = new HashMap<>();
    public Map<UUID, Boolean> cosmeticsEnabled = new HashMap<>();

    private Storage storage;

    @Override
    public void onEnable() {
        INSTANCE = this;

        Files files = new Files(this);
        files.initFiles();

        // plugin enabled?
        if (!config.getPluginEnabled()) {
            logError("Plugin is disabled in config.yml. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        debugLevel = config.getDebugLevel();

        setupRegistries();

        storage = new Storage(this);
        storage.load();

        // vault integration
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            if (debugLevel >=1) log("Vault found. Initiating integration process...");
            setupVault();
        } else {
            if (debugLevel >=1) log("Vault not found! Skipping integration process...");
        }

        // gui manager
        guiManager = new GUIManager();
        guiListener = new GUIListener(guiManager);
        registerListeners();

        // commands
        commandManager = new PaperCommandManager(this);
        registerCommandCompletions();
        registerCommands();

        // check for plugin updates
        new UpdateChecker(this, UpdateCheckSource.SPIGET, SPIGOT_RESOURCE_ID)
                .setNotifyOpsOnJoin(config.getNotifyUpdates())
                .setDownloadLink("https://www.spigotmc.org/resources/"+SPIGOT_RESOURCE_ID)
                .checkEveryXHours(12)
                .checkNow()
                .onFail(((commandSenders, e) -> logError("Failed to check for plugin updates!")));

        getOnlinePlayerData(); // protect data vanishing into thin air during /reload
        log(PLUGIN_VERSION+ " enabled successfully");
    }

    @Override
    public void onDisable() {
        storage.saveAllData(true);
        log("Disabled successfully");
        INSTANCE = null;
    }

    private void registerCommandCompletions() {
        commandManager.getCommandCompletions().registerCompletion("AllPlayers", context -> {
            List<String> nameList = new ArrayList<>();
            for (OfflinePlayer player: Bukkit.getOfflinePlayers()) {
                nameList.add(player.getName());
            }
            return nameList;
        });
        if (debugLevel >= 2) log("Registered command completions");
    }

    private void registerCommands() {
        commandManager.registerCommand(new CosmeticsCommand(this));
        if (debugLevel >= 2) log("Registered commands");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(guiListener, this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new Kill(this), this);
        Bukkit.getPluginManager().registerEvents(new ServerListener(this), this);
        if (debugLevel >= 2) log("Registered listeners");
    }

    private void getOnlinePlayerData() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Pair<Boolean, ActiveEffectData> pdata = storage.getPlayerData(player.getUniqueId());
            setupPlayerData(player, pdata);
        }
    }

    public void setupPlayerData(Player player, Pair<Boolean, ActiveEffectData> pdata) {
        cosmeticsEnabled.put(player.getUniqueId(), pdata.getFirst());
        playerActiveEffects.put(player.getUniqueId(), pdata.getSecond());

        // resume any active effect tasks
        ActiveEffectData ped = getPlayerActiveEffectData(player);
        for (EffectForm form : ped.getEffects().keySet()) {
            if (ped.getTaskIds().containsKey(form)) continue;
            switch (form) {
                case PROJECTILE: {
                    Projectile.startForPlayer(player);
                }
                case TRAIL: {
                    Trail.startForPlayer(player);
                }
                case HALO: {
                    Halo.startForPlayer(player);
                }
            }
        }

        if (debugLevel >= 2) log("Processed player data for "+player.getName());
    }

    private void setupVault() {
        RegisteredServiceProvider<Permission> rspPerm = getServer().getServicesManager().getRegistration(Permission.class);
        if (rspPerm != null) {
            vaultPerms = rspPerm.getProvider();
            if (debugLevel >=1) log("Vault integration successful.");
            vaultEnabled = true;
        } else {
            if (debugLevel >=1) logError("Failed to load Vault API!");
        }
    }

    private void setupRegistries() {
        effectNameRegistry = EffectNameRegistry.getInstance();
        effectNameRegistry.populate();
        effectDescriptionRegistry = EffectDescriptionRegistry.getInstance();
        effectDescriptionRegistry.populate();
        effectMaterialRegistry = EffectMaterialRegistry.getInstance();
        effectMaterialRegistry.populate();
        effectParticleRegistry = EffectParticleRegistry.getInstance();
        effectParticleRegistry.populate();
        effectDensityRegistry = EffectDensityRegistry.getInstance();
        effectDensityRegistry.populate();
    }

    public boolean isVaultEnabled() {
        return vaultEnabled;
    }
    public Permission getVaultPermissions() {
        return vaultPerms;
    }

    public MainConfig getConfigFile() {
        return config;
    }
    public void setConfigFile(MainConfig config) {
        this.config = config;
    }
    public LangFile getLang() {
        return lang;
    }
    public void setLang(LangFile lang) {
        this.lang = lang;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public Storage getStorage() {
        return storage;
    }

    public static void log(String message) {
        INSTANCE.getLogger().info(message);
    }
    public static void logError(String message) {
        INSTANCE.getLogger().severe(message);
    }

    public static CosmeticsPlugin getInstance() {
        return INSTANCE;
    }
}
