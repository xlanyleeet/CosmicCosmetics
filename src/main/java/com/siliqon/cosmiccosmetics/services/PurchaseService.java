package com.siliqon.cosmiccosmetics.services;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.utils.Storage;
import com.siliqon.cosmiccosmetics.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PurchaseService {
    private final CosmeticsPlugin plugin;
    private final VaultService vaultService;
    private final Storage storage;
    private final Map<UUID, Set<Enum<?>>> purchasedEffects;

    public PurchaseService(
            CosmeticsPlugin plugin,
            VaultService vaultService,
            Storage storage,
            Map<UUID, Set<Enum<?>>> purchasedEffects) {
        this.plugin = plugin;
        this.vaultService = vaultService;
        this.storage = storage;
        this.purchasedEffects = purchasedEffects;
    }

    public Set<Enum<?>> getPurchasedEffects(UUID playerUUID) {
        return purchasedEffects.computeIfAbsent(playerUUID, ignored -> new HashSet<>());
    }

    public boolean hasPurchasedEffect(Player player, Enum<?> effectType) {
        return getPurchasedEffects(player.getUniqueId()).contains(effectType);
    }

    public boolean canUseEffect(Player player, EffectForm form, Enum<?> effectType) {
        String permission = plugin.resolveEffectPermission(form, effectType);
        if (plugin.isPermissionOnlyEffect(form, effectType)) {
            return Utils.checkPlayerPermission(player, permission);
        }
        if (plugin.getConfigFile().getCosmeticTestingMode()) {
            return true;
        }
        if (Utils.checkPlayerPermission(player, permission)) {
            return true;
        }
        return hasPurchasedEffect(player, effectType);
    }

    public double getEffectPrice(EffectForm form, Enum<?> effectType) {
        return plugin.resolveEffectPrice(form, effectType);
    }

    public String formatPrice(double value) {
        return new DecimalFormat("0.##").format(value);
    }

    public boolean isCosmeticPurchasingEnabled() {
        return plugin.getConfigFile().getCosmeticPurchasingEnabled();
    }

    public CompletableFuture<CosmeticsPlugin.PurchaseResult> purchaseEffect(
            Player player,
            EffectForm form,
            Enum<?> effectType) {
        if (!isCosmeticPurchasingEnabled()) {
            return CompletableFuture.completedFuture(CosmeticsPlugin.PurchaseResult.PURCHASES_DISABLED);
        }
        if (hasPurchasedEffect(player, effectType)) {
            return CompletableFuture.completedFuture(CosmeticsPlugin.PurchaseResult.ALREADY_OWNED);
        }
        if (plugin.isPermissionOnlyEffect(form, effectType)) {
            return CompletableFuture.completedFuture(CosmeticsPlugin.PurchaseResult.PURCHASES_DISABLED);
        }

        double price = getEffectPrice(form, effectType);
        Economy economy = vaultService.getEconomy();

        if (price > 0.0D) {
            if (economy == null) {
                return CompletableFuture.completedFuture(CosmeticsPlugin.PurchaseResult.NO_ECONOMY);
            }
            if (!economy.has(player, price)) {
                return CompletableFuture.completedFuture(CosmeticsPlugin.PurchaseResult.INSUFFICIENT_FUNDS);
            }

            EconomyResponse withdrawResponse = economy.withdrawPlayer(player, price);
            if (withdrawResponse == null || !withdrawResponse.transactionSuccess()) {
                return CompletableFuture.completedFuture(CosmeticsPlugin.PurchaseResult.INSUFFICIENT_FUNDS);
            }
        }

        CompletableFuture<CosmeticsPlugin.PurchaseResult> future = new CompletableFuture<>();
        storage.savePurchasedEffectAsync(player.getUniqueId(), effectType)
                .whenComplete((saved, throwable) -> Bukkit.getScheduler().runTask(plugin, () -> {
                    if (throwable != null || !Boolean.TRUE.equals(saved)) {
                        if (price > 0.0D && economy != null) {
                            economy.depositPlayer(player, price);
                        }
                        future.complete(CosmeticsPlugin.PurchaseResult.DATABASE_ERROR);
                        return;
                    }

                    getPurchasedEffects(player.getUniqueId()).add(effectType);
                    future.complete(CosmeticsPlugin.PurchaseResult.SUCCESS);
                }));

        return future;
    }
}
