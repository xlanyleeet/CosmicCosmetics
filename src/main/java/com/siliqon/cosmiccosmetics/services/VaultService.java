package com.siliqon.cosmiccosmetics.services;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.logError;

public class VaultService {
    private final CosmeticsPlugin plugin;

    private boolean vaultEnabled;
    private Permission vaultPerms;
    private Economy vaultEconomy;

    public VaultService(CosmeticsPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        RegisteredServiceProvider<Permission> rspPerm = plugin.getServer().getServicesManager()
                .getRegistration(Permission.class);
        if (rspPerm != null) {
            vaultPerms = rspPerm.getProvider();
            vaultEnabled = true;
            if (plugin.debugLevel >= 1) {
                log("Vault integration successful.");
            }
        } else if (plugin.debugLevel >= 1) {
            logError("Failed to load Vault permission API!");
        }

        RegisteredServiceProvider<Economy> rspEconomy = plugin.getServer().getServicesManager()
                .getRegistration(Economy.class);
        if (rspEconomy != null) {
            vaultEconomy = rspEconomy.getProvider();
            if (plugin.debugLevel >= 1) {
                log("Vault economy integration successful.");
            }
        } else if (plugin.debugLevel >= 1) {
            logError("Failed to load Vault economy API!");
        }
    }

    public boolean isVaultEnabled() {
        return vaultEnabled;
    }

    public Permission getVaultPermissions() {
        return vaultPerms;
    }

    public boolean isEconomyEnabled() {
        return vaultEconomy != null;
    }

    public Economy getEconomy() {
        return vaultEconomy;
    }
}
