package com.siliqon.cosmiccosmetics.utils;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.custom.ActiveEffectData;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import com.siliqon.cosmiccosmetics.files.ConfigCache;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.logError;
import static com.siliqon.cosmiccosmetics.utils.Effects.getEffectsEnabled;
import static com.siliqon.cosmiccosmetics.utils.Effects.getPlayerActiveEffectData;

public class Storage {
    private final CosmeticsPlugin plugin;
    private HikariDataSource dataSource;
    private final ExecutorService dbExecutor;

    public record PlayerData(boolean enabled, ActiveEffectData activeEffectData, Set<EffectType> purchasedEffects) {
    }

    private record SaveSnapshot(UUID playerUUID, boolean enabled, ActiveEffectData activeEffectData,
            Set<EffectType> purchasedEffects) {
    }

    public Storage(CosmeticsPlugin plugin) {
        this.plugin = plugin;
        this.dbExecutor = Executors.newFixedThreadPool(2, r -> {
            Thread thread = new Thread(r, "cosmiccosmetics-db");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void load() {
        initDataSource();
        initSchema();
        if (plugin.debugLevel >= 2)
            log("Initialized SQL storage");
    }

    public CompletableFuture<PlayerData> getPlayerDataAsync(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            ActiveEffectData activeEffectData = new ActiveEffectData(playerUUID, new HashMap<>(), new HashMap<>());
            Set<EffectType> purchasedEffects = new HashSet<>();
            boolean enabled = false;

            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT cosmetics_enabled FROM cc_player_settings WHERE uuid = ?")) {
                    statement.setString(1, playerUUID.toString());
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            enabled = resultSet.getInt("cosmetics_enabled") == 1;
                        }
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT effect_form, effect_type FROM cc_player_effects WHERE uuid = ?")) {
                    statement.setString(1, playerUUID.toString());
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            EffectForm form = EffectForm.valueOf(resultSet.getString("effect_form"));
                            EffectType type = EffectType.valueOf(resultSet.getString("effect_type"));
                            activeEffectData.addEffect(form, type);
                        }
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT effect_type FROM cc_player_purchases WHERE uuid = ?")) {
                    statement.setString(1, playerUUID.toString());
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            purchasedEffects.add(EffectType.valueOf(resultSet.getString("effect_type")));
                        }
                    }
                }
            } catch (Exception exception) {
                logError("Failed to load player data for " + playerUUID);
                exception.printStackTrace();
            }

            return new PlayerData(enabled, activeEffectData, purchasedEffects);
        }, dbExecutor);
    }

    public CompletableFuture<Void> savePlayerDataAsync(UUID playerUUID, boolean enabled,
            ActiveEffectData activeEffectData, Set<EffectType> purchasedEffects) {
        if (shouldSkipPersistence(playerUUID)) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(
                () -> savePlayerDataSync(playerUUID, enabled, activeEffectData, purchasedEffects), dbExecutor);
    }

    public CompletableFuture<Boolean> savePurchasedEffectAsync(UUID playerUUID, EffectType effectType) {
        if (shouldSkipPersistence(playerUUID)) {
            return CompletableFuture.completedFuture(true);
        }

        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement exists = connection.prepareStatement(
                        "SELECT 1 FROM cc_player_purchases WHERE uuid = ? AND effect_type = ?")) {
                    exists.setString(1, playerUUID.toString());
                    exists.setString(2, effectType.name());
                    try (ResultSet resultSet = exists.executeQuery()) {
                        if (resultSet.next()) {
                            return true;
                        }
                    }
                }

                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO cc_player_purchases(uuid, effect_type, purchased_at) VALUES (?, ?, ?)")) {
                    insert.setString(1, playerUUID.toString());
                    insert.setString(2, effectType.name());
                    insert.setLong(3, System.currentTimeMillis());
                    insert.executeUpdate();
                    return true;
                }
            } catch (Exception exception) {
                logError("Failed to save purchase for " + playerUUID + " and effect " + effectType);
                exception.printStackTrace();
                return false;
            }
        }, dbExecutor);
    }

    public void saveAllData(boolean log) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            UUID playerUUID = player.getUniqueId();
            savePlayerDataSync(
                    playerUUID,
                    getEffectsEnabled(player),
                    getPlayerActiveEffectData(player),
                    plugin.getPurchasedEffects(playerUUID));
        });

        if (plugin.debugLevel >= 1 && log) {
            log("Saved all data");
        }
    }

    public CompletableFuture<Void> saveAllDataAsync(boolean log) {
        List<SaveSnapshot> snapshots = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            UUID playerUUID = player.getUniqueId();
            snapshots.add(new SaveSnapshot(
                    playerUUID,
                    getEffectsEnabled(player),
                    copyActiveEffectData(playerUUID, getPlayerActiveEffectData(player)),
                    new HashSet<>(plugin.getPurchasedEffects(playerUUID))));
        });

        CompletableFuture<?>[] futures = snapshots.stream()
                .map(snapshot -> savePlayerDataAsync(
                        snapshot.playerUUID(),
                        snapshot.enabled(),
                        snapshot.activeEffectData(),
                        snapshot.purchasedEffects()))
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures).whenComplete((ignored, throwable) -> {
            if (throwable != null) {
                logError("Failed to save all data asynchronously");
            } else if (plugin.debugLevel >= 1 && log) {
                log("Saved all data asynchronously");
            }
        });
    }

    public void shutdown() {
        dbExecutor.shutdown();
        try {
            if (!dbExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                dbExecutor.shutdownNow();
                dbExecutor.awaitTermination(3, TimeUnit.SECONDS);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            dbExecutor.shutdownNow();
        }

        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private void savePlayerDataSync(UUID playerUUID, boolean enabled, ActiveEffectData activeEffectData,
            Set<EffectType> purchasedEffects) {
        if (shouldSkipPersistence(playerUUID)) {
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection
                        .prepareStatement("DELETE FROM cc_player_settings WHERE uuid = ?")) {
                    statement.setString(1, playerUUID.toString());
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO cc_player_settings(uuid, cosmetics_enabled) VALUES (?, ?)")) {
                    statement.setString(1, playerUUID.toString());
                    statement.setInt(2, enabled ? 1 : 0);
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection
                        .prepareStatement("DELETE FROM cc_player_effects WHERE uuid = ?")) {
                    statement.setString(1, playerUUID.toString());
                    statement.executeUpdate();
                }

                if (activeEffectData != null) {
                    for (Map.Entry<EffectForm, EffectType> entry : activeEffectData.getEffects().entrySet()) {
                        try (PreparedStatement statement = connection.prepareStatement(
                                "INSERT INTO cc_player_effects(uuid, effect_form, effect_type) VALUES (?, ?, ?)")) {
                            statement.setString(1, playerUUID.toString());
                            statement.setString(2, entry.getKey().name());
                            statement.setString(3, entry.getValue().name());
                            statement.executeUpdate();
                        }
                    }
                }

                try (PreparedStatement statement = connection
                        .prepareStatement("DELETE FROM cc_player_purchases WHERE uuid = ?")) {
                    statement.setString(1, playerUUID.toString());
                    statement.executeUpdate();
                }

                if (purchasedEffects != null) {
                    for (EffectType effectType : purchasedEffects) {
                        try (PreparedStatement statement = connection.prepareStatement(
                                "INSERT INTO cc_player_purchases(uuid, effect_type, purchased_at) VALUES (?, ?, ?)")) {
                            statement.setString(1, playerUUID.toString());
                            statement.setString(2, effectType.name());
                            statement.setLong(3, System.currentTimeMillis());
                            statement.executeUpdate();
                        }
                    }
                }

                connection.commit();
            } catch (Exception saveException) {
                connection.rollback();
                throw saveException;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception exception) {
            logError("Failed to save player data for " + playerUUID);
            exception.printStackTrace();
        }
    }

    private void initDataSource() {
        ConfigCache config = plugin.getConfigFile();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("CosmicCosmeticsPool");
        hikariConfig.setMaximumPoolSize(config.getDatabasePoolMaximumSize());
        hikariConfig.setConnectionTimeout(config.getDatabaseConnectionTimeoutMs());

        String storageType = config.getStorageType();
        if (storageType != null && storageType.equalsIgnoreCase("MARIADB")) {
            String jdbcUrl = "jdbc:mariadb://"
                    + config.getMariadbHost() + ":"
                    + config.getMariadbPort() + "/"
                    + config.getMariadbDatabase();
            hikariConfig.setJdbcUrl(jdbcUrl);
            hikariConfig.setUsername(config.getMariadbUsername());
            hikariConfig.setPassword(config.getMariadbPassword());
        } else {
            File dbFile = new File(plugin.getDataFolder(), config.getSqliteFileName());
            dbFile.getParentFile().mkdirs();
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        }

        dataSource = new HikariDataSource(hikariConfig);
    }

    private void initSchema() {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement settingsStatement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS cc_player_settings ("
                                + "uuid VARCHAR(36) PRIMARY KEY,"
                                + "cosmetics_enabled INTEGER NOT NULL DEFAULT 0"
                                + ")");
                PreparedStatement effectsStatement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS cc_player_effects ("
                                + "uuid VARCHAR(36) NOT NULL,"
                                + "effect_form VARCHAR(32) NOT NULL,"
                                + "effect_type VARCHAR(32) NOT NULL,"
                                + "PRIMARY KEY(uuid, effect_form)"
                                + ")");
                PreparedStatement purchasesStatement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS cc_player_purchases ("
                                + "uuid VARCHAR(36) NOT NULL,"
                                + "effect_type VARCHAR(32) NOT NULL,"
                                + "purchased_at BIGINT NOT NULL,"
                                + "PRIMARY KEY(uuid, effect_type)"
                                + ")")) {
            settingsStatement.executeUpdate();
            effectsStatement.executeUpdate();
            purchasesStatement.executeUpdate();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to initialize database schema", exception);
        }
    }

    private boolean shouldSkipPersistence(UUID playerUUID) {
        return Bukkit.getOfflinePlayer(playerUUID).isOp();
    }

    private ActiveEffectData copyActiveEffectData(UUID playerUUID, ActiveEffectData source) {
        if (source == null) {
            return null;
        }

        Map<EffectForm, EffectType> effectsCopy = new HashMap<>(source.getEffects());
        return new ActiveEffectData(playerUUID, effectsCopy, new HashMap<>());
    }
}
