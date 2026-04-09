package com.siliqon.cosmiccosmetics;

import co.aikar.commands.PaperCommandManager;
import com.siliqon.cosmiccosmetics.commands.CosmeticsCommand;
import com.siliqon.cosmiccosmetics.custom.ActiveEffectData;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import com.siliqon.cosmiccosmetics.files.*;
import com.siliqon.cosmiccosmetics.handlers.effects.Halo;
import com.siliqon.cosmiccosmetics.handlers.effects.Kill;
import com.siliqon.cosmiccosmetics.handlers.effects.Projectile;
import com.siliqon.cosmiccosmetics.handlers.effects.Trail;
import com.siliqon.cosmiccosmetics.handlers.effects.Capes;
import com.siliqon.cosmiccosmetics.handlers.effects.Balloons;
import com.siliqon.cosmiccosmetics.handlers.effects.Pets;
import com.siliqon.cosmiccosmetics.listeners.PlayerListener;
import com.siliqon.cosmiccosmetics.listeners.ServerListener;
import com.siliqon.cosmiccosmetics.registries.*;
import com.siliqon.cosmiccosmetics.services.PurchaseService;
import com.siliqon.cosmiccosmetics.services.VaultService;
import com.siliqon.cosmiccosmetics.utils.Files;
import com.siliqon.cosmiccosmetics.utils.Storage;
import com.siliqon.cosmiccosmetics.utils.Utils;
import com.siliqon.cosmiccosmetics.guis.lib.GUIManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.siliqon.cosmiccosmetics.utils.Effects.getPlayerActiveEffectData;

public final class CosmeticsPlugin extends JavaPlugin {
    public enum PurchaseResult {
        SUCCESS,
        ALREADY_OWNED,
        PURCHASES_DISABLED,
        NO_ECONOMY,
        INSUFFICIENT_FUNDS,
        DATABASE_ERROR
    }

    private static CosmeticsPlugin INSTANCE;

    public NamespacedKey customItemKey = new NamespacedKey(this, "menu-item");
    public String PREFIX = "&b&lCosmetics &7> &r ";

    public EffectNameRegistry effectNameRegistry;
    public EffectDescriptionRegistry effectDescriptionRegistry;
    public EffectMaterialRegistry effectMaterialRegistry;
    public EffectParticleRegistry effectParticleRegistry;
    public EffectDensityRegistry effectDensityRegistry;

    private PaperCommandManager commandManager;
    private GUIManager guiManager;

    private ConfigCache config;
    private LanguageCache lang;
    private Map<String, LanguageCache> languages = new HashMap<>();
    private String defaultLanguageCode = "en_US";
    private CosmeticRulesCache cosmeticRules;

    private VaultService vaultService;
    private PurchaseService purchaseService;

    public int debugLevel = 0;

    public Map<UUID, ActiveEffectData> playerActiveEffects = new HashMap<>();
    public Map<UUID, Boolean> cosmeticsEnabled = new HashMap<>();
    public Map<UUID, Set<EffectType>> purchasedEffects = new ConcurrentHashMap<>();

    private Storage storage;

    @Override
    public void onEnable() {
        INSTANCE = this;

        Files files = new Files(this);
        files.initFiles();

        debugLevel = config.getDebugLevel();

        setupRegistries();

        storage = new Storage(this);
        storage.load();

        vaultService = new VaultService(this);

        // vault integration
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            if (debugLevel >= 1)
                log("Vault found. Initiating integration process...");
            setupVault();
        } else {
            if (debugLevel >= 1)
                log("Vault not found! Skipping integration process...");
        }

        purchaseService = new PurchaseService(this, vaultService, storage, purchasedEffects);

        // gui manager
        guiManager = new GUIManager();
        registerListeners();

        // commands
        commandManager = new PaperCommandManager(this);
        registerCommandCompletions();
        registerCommands();

        getOnlinePlayerData(); // protect data vanishing into thin air during /reload
        log("Plugin enabled successfully");
    }

    @Override
    public void onDisable() {
        Balloons.removeAll();
        Pets.removeAll();

        try {
            storage.saveAllDataAsync(true).get(10, TimeUnit.SECONDS);
        } catch (Exception exception) {
            logError("Timed out or failed while waiting for async data save during shutdown");
            exception.printStackTrace();
        }

        storage.shutdown();
        log("Disabled successfully");
        INSTANCE = null;
    }

    private void registerCommandCompletions() {
        commandManager.getCommandCompletions().registerCompletion("AllPlayers", context -> {
            List<String> nameList = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                nameList.add(player.getName());
            }
            return nameList;
        });
        if (debugLevel >= 2)
            log("Registered command completions");
    }

    private void registerCommands() {
        commandManager.registerCommand(new CosmeticsCommand(this));
        if (debugLevel >= 2)
            log("Registered commands");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new Kill(this), this);
        Bukkit.getPluginManager().registerEvents(new ServerListener(this), this);
        if (debugLevel >= 2)
            log("Registered listeners");
    }

    private void getOnlinePlayerData() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            storage.getPlayerDataAsync(player.getUniqueId())
                    .thenAccept(playerData -> Bukkit.getScheduler().runTask(this, () -> {
                        if (!player.isOnline()) {
                            return;
                        }
                        setupPlayerData(player, playerData);
                    }));
        }
    }

    public void setupPlayerData(Player player, Storage.PlayerData playerData) {
        cosmeticsEnabled.put(player.getUniqueId(), playerData.enabled());
        playerActiveEffects.put(player.getUniqueId(), playerData.activeEffectData());
        purchasedEffects.put(player.getUniqueId(), new HashSet<>(playerData.purchasedEffects()));

        // resume any active effect tasks
        ActiveEffectData ped = getPlayerActiveEffectData(player);
        if (ped == null) {
            return;
        }
        for (EffectForm form : ped.getEffects().keySet()) {
            if (ped.getTaskIds().containsKey(form))
                continue;
            switch (form) {
                case PROJECTILE: {
                    Projectile.startForPlayer(player);
                    break;
                }
                case TRAIL: {
                    Trail.startForPlayer(player);
                    break;
                }
                case HALO: {
                    Halo.startForPlayer(player);
                    break;
                }
                case CAPES: {
                    Capes.startForPlayer(player);
                    break;
                }
                case BALLOONS: {
                    Balloons.startForPlayer(player);
                    break;
                }
                case PETS: {
                    Pets.startForPlayer(player);
                    break;
                }
                case KILL:
                case GLOW: {
                    break;
                }
            }
        }

        if (debugLevel >= 2)
            log("Processed player data for " + player.getName());
    }

    private void setupVault() {
        vaultService.initialize();
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
        return vaultService != null && vaultService.isVaultEnabled();
    }

    public Permission getVaultPermissions() {
        return vaultService == null ? null : vaultService.getVaultPermissions();
    }

    public boolean isEconomyEnabled() {
        return vaultService != null && vaultService.isEconomyEnabled();
    }

    public Set<EffectType> getPurchasedEffects(UUID playerUUID) {
        if (purchaseService == null) {
            return purchasedEffects.computeIfAbsent(playerUUID, ignored -> new HashSet<>());
        }
        return purchaseService.getPurchasedEffects(playerUUID);
    }

    public boolean hasPurchasedEffect(Player player, EffectType effectType) {
        return purchaseService != null && purchaseService.hasPurchasedEffect(player, effectType);
    }

    public boolean canUseEffect(Player player, EffectForm form, EffectType effectType) {
        if (purchaseService == null) {
            return Utils.checkPlayerPermission(
                    player,
                    resolveEffectPermission(form, effectType));
        }
        return purchaseService.canUseEffect(player, form, effectType);
    }

    public double getEffectPrice(EffectForm form, EffectType effectType) {
        if (purchaseService == null) {
            return resolveEffectPrice(form, effectType);
        }
        return purchaseService.getEffectPrice(form, effectType);
    }

    public String formatPrice(double value) {
        return purchaseService == null ? String.valueOf(value) : purchaseService.formatPrice(value);
    }

    public boolean isCosmeticPurchasingEnabled() {
        return purchaseService != null && purchaseService.isCosmeticPurchasingEnabled();
    }

    public CompletableFuture<PurchaseResult> purchaseEffect(Player player, EffectForm form, EffectType effectType) {
        if (purchaseService == null) {
            return CompletableFuture.completedFuture(PurchaseResult.DATABASE_ERROR);
        }
        return purchaseService.purchaseEffect(player, form, effectType);
    }

    public ConfigCache getConfigFile() {
        return config;
    }

    public void setConfigFile(ConfigCache config) {
        this.config = config;
    }

    public LanguageCache getLang() {
        return lang;
    }

    public LanguageCache getLang(Player player) {
        if (player == null || config == null || !config.isPerPlayerLanguageEnabled()) {
            return getLang();
        }

        Locale locale = player.locale();
        if (locale == null) {
            return getLang();
        }

        return resolveLanguage(normalizeLanguageCode(locale.toString()));
    }

    public void setLang(LanguageCache lang) {
        this.lang = lang;
    }

    public void setLanguages(Map<String, LanguageCache> languages, String defaultLanguageCode) {
        this.languages = new HashMap<>(languages);
        this.defaultLanguageCode = normalizeLanguageCode(defaultLanguageCode);
        this.lang = resolveLanguage(this.defaultLanguageCode);
    }

    public CosmeticRulesCache getCosmeticRules() {
        return cosmeticRules;
    }

    public void setCosmeticRules(CosmeticRulesCache cosmeticRules) {
        this.cosmeticRules = cosmeticRules;
    }

    public String resolveEffectPermission(EffectForm form, EffectType effectType) {
        if (cosmeticRules == null) {
            return "cosmetics." + form.name().toLowerCase() + "." + effectType.name().toLowerCase();
        }
        return cosmeticRules.resolvePermission(form, effectType);
    }

    public boolean isPermissionOnlyEffect(EffectForm form, EffectType effectType) {
        if (cosmeticRules == null) {
            return form == EffectForm.PETS;
        }
        return cosmeticRules.isPermissionOnly(form, effectType);
    }

    public double resolveEffectPrice(EffectForm form, EffectType effectType) {
        if (cosmeticRules == null) {
            return Math.max(0.0D, config.getDefaultCosmeticPrice());
        }
        return cosmeticRules.getPrice(form, effectType, config.getDefaultCosmeticPrice());
    }

    public boolean isEffectFormEnabledInWorld(EffectForm form, String worldName) {
        if (config == null) {
            return true;
        }
        return config.isWorldEnabled(form, worldName);
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

    private LanguageCache resolveLanguage(String languageCode) {
        if (languages.isEmpty()) {
            return lang;
        }

        String normalized = normalizeLanguageCode(languageCode);

        LanguageCache exact = languages.get(normalized);
        if (exact != null) {
            return exact;
        }

        int separatorIndex = normalized.indexOf('_');
        if (separatorIndex > 0) {
            String base = normalized.substring(0, separatorIndex);
            for (Map.Entry<String, LanguageCache> entry : languages.entrySet()) {
                if (entry.getKey().startsWith(base + "_")) {
                    return entry.getValue();
                }
            }
        }

        LanguageCache defaultLanguage = languages.get(defaultLanguageCode);
        if (defaultLanguage != null) {
            return defaultLanguage;
        }

        LanguageCache english = languages.get("en_US");
        if (english != null) {
            return english;
        }

        return languages.values().iterator().next();
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

    public static CosmeticsPlugin getInstance() {
        return INSTANCE;
    }
}
