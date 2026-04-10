package com.siliqon.cosmiccosmetics.files;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LanguageCache {
    public record CachedValue(String text, List<String> lines) {
    }

    private static final CachedValue EMPTY = new CachedValue("", List.of());

    private final Map<String, CachedValue> cache;

    private LanguageCache(Map<String, CachedValue> cache) {
        this.cache = cache;
    }

    public static LanguageCache load(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        Map<String, CachedValue> cache = new HashMap<>();

        for (String key : yaml.getKeys(true)) {
            if (yaml.isConfigurationSection(key)) {
                continue;
            }

            if (yaml.isList(key)) {
                cache.put(key, new CachedValue("", List.copyOf(yaml.getStringList(key))));
            } else {
                cache.put(key, new CachedValue(yaml.getString(key, ""), List.of()));
            }
        }

        return new LanguageCache(cache);
    }

    private String text(String key) {
        return cache.getOrDefault(key, EMPTY).text();
    }

    private List<String> lines(String key) {
        return cache.getOrDefault(key, EMPTY).lines();
    }

    private String textOrDefault(String key, String defaultValue) {
        String value = text(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private List<String> linesOrDefault(String key, List<String> defaultValue) {
        List<String> value = lines(key);
        return value == null || value.isEmpty() ? defaultValue : value;
    }

    public String getBackButtonName() {
        return text("back-button-name");
    }

    public String getMainMenuName() {
        return text("main-menu-name");
    }

    public String getPetsEffectsMenuName() {
        return text("pets-effects-menu-name");
    }

    public String getCapesEffectsMenuName() {
        return text("capes-effects-menu-name");
    }

    public String getCloudEffectName() {
        return text("cloud-effect-name");
    }

    public String getCapeCrimsonEffectName() {
        return text("cape-crimson-effect-name");
    }

    public List<String> getCapeCrimsonEffectDescription() {
        return lines("cape-crimson-effect-description");
    }

    public String getCapeAzureEffectName() {
        return text("cape-azure-effect-name");
    }

    public List<String> getCapeAzureEffectDescription() {
        return lines("cape-azure-effect-description");
    }

    public String getCapeEmeraldEffectName() {
        return text("cape-emerald-effect-name");
    }

    public List<String> getCapeEmeraldEffectDescription() {
        return lines("cape-emerald-effect-description");
    }

    public String getCapeVioletEffectName() {
        return text("cape-violet-effect-name");
    }

    public List<String> getCapeVioletEffectDescription() {
        return lines("cape-violet-effect-description");
    }

    public String getCapeRainbowEffectName() {
        return text("cape-rainbow-effect-name");
    }

    public List<String> getCapeRainbowEffectDescription() {
        return lines("cape-rainbow-effect-description");
    }

    public String getCapeGoldEffectName() {
        return textOrDefault("cape-gold-effect-name", "<yellow>Golden Cape");
    }

    public List<String> getCapeGoldEffectDescription() {
        return linesOrDefault("cape-gold-effect-description", List.of(
                " ",
                "<gray>A bright golden particle cape",
                "<gray>for a regal look"));
    }

    public String getCapeIceEffectName() {
        return textOrDefault("cape-ice-effect-name", "<aqua>Ice Cape");
    }

    public List<String> getCapeIceEffectDescription() {
        return linesOrDefault("cape-ice-effect-description", List.of(
                " ",
                "<gray>A frosty light-blue cape",
                "<gray>that shimmers in motion"));
    }

    public String getCapeVoidEffectName() {
        return textOrDefault("cape-void-effect-name", "<dark_gray>Void Cape");
    }

    public List<String> getCapeVoidEffectDescription() {
        return linesOrDefault("cape-void-effect-description", List.of(
                " ",
                "<gray>A dark cosmic cape",
                "<gray>with deep shadow tones"));
    }

    public String getCapeSunsetEffectName() {
        return textOrDefault("cape-sunset-effect-name", "<gold>Sunset Cape");
    }

    public List<String> getCapeSunsetEffectDescription() {
        return linesOrDefault("cape-sunset-effect-description", List.of(
                " ",
                "<gray>An orange dusk-inspired cape",
                "<gray>with warm glow colors"));
    }

    public String getBalloonsEffectName() {
        return text("balloons-effect-name");
    }

    public List<String> getBalloonsEffectDescription() {
        return lines("balloons-effect-description");
    }

    public String getRedBalloonEffectName() {
        return text("red-balloon-effect-name");
    }

    public List<String> getRedBalloonEffectDescription() {
        return lines("red-balloon-effect-description");
    }

    public String getBlueBalloonEffectName() {
        return text("blue-balloon-effect-name");
    }

    public List<String> getBlueBalloonEffectDescription() {
        return lines("blue-balloon-effect-description");
    }

    public String getGreenBalloonEffectName() {
        return text("green-balloon-effect-name");
    }

    public List<String> getGreenBalloonEffectDescription() {
        return lines("green-balloon-effect-description");
    }

    public String getYellowBalloonEffectName() {
        return text("yellow-balloon-effect-name");
    }

    public List<String> getYellowBalloonEffectDescription() {
        return lines("yellow-balloon-effect-description");
    }

    public String getPurpleBalloonEffectName() {
        return text("purple-balloon-effect-name");
    }

    public List<String> getPurpleBalloonEffectDescription() {
        return lines("purple-balloon-effect-description");
    }

    public String getOrangeBalloonEffectName() {
        return text("orange-balloon-effect-name");
    }

    public List<String> getOrangeBalloonEffectDescription() {
        return lines("orange-balloon-effect-description");
    }

    public String getCyanBalloonEffectName() {
        return text("cyan-balloon-effect-name");
    }

    public List<String> getCyanBalloonEffectDescription() {
        return lines("cyan-balloon-effect-description");
    }

    public String getPinkBalloonEffectName() {
        return text("pink-balloon-effect-name");
    }

    public List<String> getPinkBalloonEffectDescription() {
        return lines("pink-balloon-effect-description");
    }

    public String getPetCatEffectName() {
        return text("pet-cat-effect-name");
    }

    public List<String> getPetCatEffectDescription() {
        return lines("pet-cat-effect-description");
    }

    public String getPetWolfEffectName() {
        return text("pet-wolf-effect-name");
    }

    public List<String> getPetWolfEffectDescription() {
        return lines("pet-wolf-effect-description");
    }

    public String getPetRabbitEffectName() {
        return text("pet-rabbit-effect-name");
    }

    public List<String> getPetRabbitEffectDescription() {
        return lines("pet-rabbit-effect-description");
    }

    public String getPetFoxEffectName() {
        return text("pet-fox-effect-name");
    }

    public List<String> getPetFoxEffectDescription() {
        return lines("pet-fox-effect-description");
    }

    public String getPetPigEffectName() {
        return text("pet-pig-effect-name");
    }

    public List<String> getPetPigEffectDescription() {
        return lines("pet-pig-effect-description");
    }

    public String getPetSheepEffectName() {
        return text("pet-sheep-effect-name");
    }

    public List<String> getPetSheepEffectDescription() {
        return lines("pet-sheep-effect-description");
    }

    public String getPetChickenEffectName() {
        return text("pet-chicken-effect-name");
    }

    public List<String> getPetChickenEffectDescription() {
        return lines("pet-chicken-effect-description");
    }

    public String getPetSlimeEffectName() {
        return text("pet-slime-effect-name");
    }

    public List<String> getPetSlimeEffectDescription() {
        return lines("pet-slime-effect-description");
    }

    public String getPetBeeEffectName() {
        return text("pet-bee-effect-name");
    }

    public List<String> getPetBeeEffectDescription() {
        return lines("pet-bee-effect-description");
    }

    public String getPetTurtleEffectName() {
        return text("pet-turtle-effect-name");
    }

    public List<String> getPetTurtleEffectDescription() {
        return lines("pet-turtle-effect-description");
    }

    public String getPetFrogEffectName() {
        return text("pet-frog-effect-name");
    }

    public List<String> getPetFrogEffectDescription() {
        return lines("pet-frog-effect-description");
    }

    public List<String> getCloudEffectDescription() {
        return lines("cloud-effect-description");
    }

    public String getRainbowEffectName() {
        return text("rainbow-effect-name");
    }

    public List<String> getRainbowEffectDescription() {
        return lines("rainbow-effect-description");
    }

    public String getCrownEffectName() {
        return textOrDefault("crown-effect-name", "<gold>Crown Halo");
    }

    public List<String> getCrownEffectDescription() {
        return linesOrDefault("crown-effect-description", List.of("<gray>A golden crown above your head."));
    }

    public String getShadowFootprintsEffectName() {
        return textOrDefault("shadow-footprints-effect-name", "<dark_gray>Shadow Footprints");
    }

    public List<String> getShadowFootprintsEffectDescription() {
        return linesOrDefault("shadow-footprints-effect-description",
                List.of("<gray>Leave a trail of shadow footprints behind you."));
    }

    public String getSnowFootprintsEffectName() {
        return textOrDefault("snow-footprints-effect-name", "<white>Snow Footprints");
    }

    public List<String> getSnowFootprintsEffectDescription() {
        return linesOrDefault("snow-footprints-effect-description",
                List.of("<gray>Leave a trail of snowy footprints."));
    }

    public String getFlameHelixEffectName() {
        return textOrDefault("flame-helix-effect-name", "<gold>Flame Helix");
    }

    public List<String> getFlameHelixEffectDescription() {
        return linesOrDefault("flame-helix-effect-description", List.of("<gray>A swirling flame helix around you."));
    }

    public String getRedstoneHelixEffectName() {
        return textOrDefault("redstone-helix-effect-name", "<red>Redstone Helix");
    }

    public List<String> getRedstoneHelixEffectDescription() {
        return linesOrDefault("redstone-helix-effect-description",
                List.of("<gray>A swirling redstone helix around you."));
    }

    public String getSoulFireHelixEffectName() {
        return textOrDefault("soul-fire-helix-effect-name", "<blue>Soul Fire Helix");
    }

    public List<String> getSoulFireHelixEffectDescription() {
        return linesOrDefault("soul-fire-helix-effect-description",
                List.of("<gray>A swirling soul fire helix around you."));
    }

    public String getTrailEffectsMenuName() {
        return text("trail-effects-menu-name");
    }

    public String getKillEffectsMenuName() {
        return text("kill-effects-menu-name");
    }

    public String getSplashEffectName() {
        return text("splash-effect-name");
    }

    public List<String> getSplashEffectDescription() {
        return lines("splash-effect-description");
    }

    public String getEnderEffectName() {
        return text("ender-effect-name");
    }

    public List<String> getEnderEffectDescription() {
        return lines("ender-effect-description");
    }

    public String getCharmEffectName() {
        return text("charm-effect-name");
    }

    public List<String> getCharmEffectDescription() {
        return lines("charm-effect-description");
    }

    public String getLoveEffectName() {
        return text("love-effect-name");
    }

    public List<String> getLoveEffectDescription() {
        return lines("love-effect-description");
    }

    public String getBlossomEffectName() {
        return text("blossom-effect-name");
    }

    public List<String> getBlossomEffectDescription() {
        return lines("blossom-effect-description");
    }

    public String getEnchantedEffectName() {
        return text("enchanted-effect-name");
    }

    public List<String> getEnchantedEffectDescription() {
        return lines("enchanted-effect-description");
    }

    public String getTearsEffectName() {
        return text("tears-effect-name");
    }

    public List<String> getTearsEffectDescription() {
        return lines("tears-effect-description");
    }

    public String getMusicalEffectName() {
        return text("musical-effect-name");
    }

    public List<String> getMusicalEffectDescription() {
        return lines("musical-effect-description");
    }

    public String getCosmeticsDisabledOther() {
        return text("cosmetics-disabled-other");
    }

    public String getCosmeticsEnabledOther() {
        return text("cosmetics-enabled-other");
    }

    public String getPurchasesDisabledMessage() {
        return text("purchases-disabled-message");
    }

    public String getPurchasesNeedVaultMessage() {
        return text("purchases-need-vault-message");
    }

    public String getPurchaseInsufficientFunds() {
        return text("purchase-insufficient-funds");
    }

    public String getPurchaseSuccessful() {
        return text("purchase-successful");
    }

    public String getPurchaseAlreadyOwned() {
        return text("purchase-already-owned");
    }

    public String getPurchaseFailed() {
        return text("purchase-failed");
    }

    public String getNoPermissionMessage() {
        return text("no-permission-message");
    }

    public String getResetEffectItemName() {
        return text("reset-effect-item-name");
    }

    public String getEffectEnabled() {
        return text("effect-enabled");
    }

    public String getEffectDisabled() {
        return text("effect-disabled");
    }

    public String getDisabledAllEffectsMessage() {
        return text("disabled-all-effects-message");
    }

    public String getEffectSelectedName() {
        return text("effect-selected-name");
    }

    public String getEffectNotUnlockedName() {
        return text("effect-not-unlocked-name");
    }

    public List<String> getEffectNotUnlockedLore() {
        return lines("effect-not-unlocked-lore");
    }

    public List<String> getEffectPermissionOnlyLore() {
        return lines("effect-permission-only-lore");
    }

    public String getFlameEffectName() {
        return text("flame-effect-name");
    }

    public List<String> getFlameEffectDescription() {
        return lines("flame-effect-description");
    }

    public String getProjectileEffectsMenuName() {
        return text("projectile-effects-menu-name");
    }

    public String getTrailEffectsItemName() {
        return text("trail-effects-item-name");
    }

    public String getProjectileEffectsItemName() {
        return text("projectile-effects-item-name");
    }

    public String getKillEffectsItemName() {
        return text("kill-effects-item-name");
    }

    public String getBalloonsEffectsItemName() {
        return text("balloons-effects-item-name");
    }

    public String getPetsEffectsItemName() {
        return text("pets-effects-item-name");
    }

    public String getCapesEffectsItemName() {
        return text("capes-effects-item-name");
    }

    public List<String> getBalloonsEffectsItemLore() {
        return lines("balloons-effects-item-lore");
    }

    public List<String> getPetsEffectsItemLore() {
        return lines("pets-effects-item-lore");
    }

    public List<String> getCapesEffectsItemLore() {
        return lines("capes-effects-item-lore");
    }

    public String getHaloEffectsItemName() {
        return text("halo-effects-item-name");
    }

    public List<String> getHaloEffectsItemLore() {
        return lines("halo-effects-item-lore");
    }

    public String getHaloEffectsMenuName() {
        return text("halo-effects-menu-name");
    }

    public String getGlowEffectsItemName() {
        return text("glow-effects-item-name");
    }

    public List<String> getGlowEffectsItemLore() {
        return lines("glow-effects-item-lore");
    }

    public String getGlowEffectsMenuName() {
        return text("glow-effects-menu-name");
    }

    public List<String> getTrailEffectsItemLore() {
        return lines("trail-effects-item-lore");
    }

    public List<String> getProjectileEffectsItemLore() {
        return lines("projectile-effects-item-lore");
    }

    public List<String> getKillEffectsItemLore() {
        return lines("kill-effects-item-lore");
    }

    public String getResetAllEffectsItemName() {
        return text("reset-all-effects-item-name");
    }

    public List<String> getResetAllEffectsItemLore() {
        return lines("reset-all-effects-item-lore");
    }

    public String getCategoryUnlockedLoreLine() {
        return textOrDefault("category-unlocked-lore-line",
                "<gray>Unlocked: <green>%unlocked_count%<gray>/%total_count%");
    }

    public String getPrefix() {
        return text("prefix");
    }

    public String getEffectName(Enum<?> effectType) {
        if (effectType == null) {
            return "NONE";
        }
        if (effectType instanceof com.siliqon.cosmiccosmetics.enums.Glow glow) {
            String nameLower = glow.name().toLowerCase(java.util.Locale.ROOT).replace('_', '-');
            return textOrDefault("glow-" + nameLower + "-effect-name", "<" + nameLower + ">" + glow.name() + " Glow");
        }

        return switch (effectType.name()) {
            case "SPLASH" -> getSplashEffectName();
            case "ENDER" -> getEnderEffectName();
            case "FLAME" -> getFlameEffectName();
            case "CHARM" -> getCharmEffectName();
            case "LOVE" -> getLoveEffectName();
            case "BLOSSOM" -> getBlossomEffectName();
            case "ENCHANTED" -> getEnchantedEffectName();
            case "TEARS" -> getTearsEffectName();
            case "MUSICAL" -> getMusicalEffectName();
            case "RAINBOW" -> getRainbowEffectName();
            case "CROWN" -> getCrownEffectName();
            case "CLOUDY" -> getCloudEffectName();
            case "SHADOW_FOOTPRINTS" -> getShadowFootprintsEffectName();
            case "SNOW_FOOTPRINTS" -> getSnowFootprintsEffectName();
            case "FLAME_HELIX" -> getFlameHelixEffectName();
            case "REDSTONE_HELIX" -> getRedstoneHelixEffectName();
            case "SOUL_FIRE_HELIX" -> getSoulFireHelixEffectName();
            case "CAPE_CRIMSON" -> getCapeCrimsonEffectName();
            case "CAPE_AZURE" -> getCapeAzureEffectName();
            case "CAPE_EMERALD" -> getCapeEmeraldEffectName();
            case "CAPE_VIOLET" -> getCapeVioletEffectName();
            case "CAPE_RAINBOW" -> getCapeRainbowEffectName();
            case "CAPE_GOLD" -> getCapeGoldEffectName();
            case "CAPE_ICE" -> getCapeIceEffectName();
            case "CAPE_VOID" -> getCapeVoidEffectName();
            case "CAPE_SUNSET" -> getCapeSunsetEffectName();
            case "PET_CAT" -> getPetCatEffectName();
            case "PET_WOLF" -> getPetWolfEffectName();
            case "PET_RABBIT" -> getPetRabbitEffectName();
            case "PET_FOX" -> getPetFoxEffectName();
            case "PET_PIG" -> getPetPigEffectName();
            case "PET_SHEEP" -> getPetSheepEffectName();
            case "PET_CHICKEN" -> getPetChickenEffectName();
            case "PET_SLIME" -> getPetSlimeEffectName();
            case "PET_BEE" -> getPetBeeEffectName();
            case "PET_TURTLE" -> getPetTurtleEffectName();
            case "PET_FROG" -> getPetFrogEffectName();
            case "BALLOONS" -> getBalloonsEffectName();
            case "BALLOON_RED" -> getRedBalloonEffectName();
            case "BALLOON_BLUE" -> getBlueBalloonEffectName();
            case "BALLOON_GREEN" -> getGreenBalloonEffectName();
            case "BALLOON_YELLOW" -> getYellowBalloonEffectName();
            case "BALLOON_PURPLE" -> getPurpleBalloonEffectName();
            case "BALLOON_ORANGE" -> getOrangeBalloonEffectName();
            case "BALLOON_CYAN" -> getCyanBalloonEffectName();
            case "BALLOON_PINK" -> getPinkBalloonEffectName();
            case "SNOWBALL" -> getGunSnowballName();
            case "FIREBALL" -> getGunFireballName();
            case "EXPLOSION" -> getGunExplosionName();
            case "METEOR" -> getGunMeteorName();
            default -> "Unknown";
        };
    }

    public List<String> getEffectDescription(Enum<?> effectType) {
        if (effectType == null) {
            return List.of();
        }
        if (effectType instanceof com.siliqon.cosmiccosmetics.enums.Glow glow) {
            String nameLower = glow.name().toLowerCase(java.util.Locale.ROOT).replace('_', '-');
            return linesOrDefault("glow-" + nameLower + "-effect-description", java.util.List.of(" ", "<gray>A glowing " + nameLower + " aura."));
        }

        return switch (effectType.name()) {
            case "SPLASH" -> getSplashEffectDescription();
            case "ENDER" -> getEnderEffectDescription();
            case "FLAME" -> getFlameEffectDescription();
            case "CHARM" -> getCharmEffectDescription();
            case "LOVE" -> getLoveEffectDescription();
            case "BLOSSOM" -> getBlossomEffectDescription();
            case "ENCHANTED" -> getEnchantedEffectDescription();
            case "TEARS" -> getTearsEffectDescription();
            case "MUSICAL" -> getMusicalEffectDescription();
            case "RAINBOW" -> getRainbowEffectDescription();
            case "CROWN" -> getCrownEffectDescription();
            case "CLOUDY" -> getCloudEffectDescription();
            case "SHADOW_FOOTPRINTS" -> getShadowFootprintsEffectDescription();
            case "SNOW_FOOTPRINTS" -> getSnowFootprintsEffectDescription();
            case "FLAME_HELIX" -> getFlameHelixEffectDescription();
            case "REDSTONE_HELIX" -> getRedstoneHelixEffectDescription();
            case "SOUL_FIRE_HELIX" -> getSoulFireHelixEffectDescription();
            case "CAPE_CRIMSON" -> getCapeCrimsonEffectDescription();
            case "CAPE_AZURE" -> getCapeAzureEffectDescription();
            case "CAPE_EMERALD" -> getCapeEmeraldEffectDescription();
            case "CAPE_VIOLET" -> getCapeVioletEffectDescription();
            case "CAPE_RAINBOW" -> getCapeRainbowEffectDescription();
            case "CAPE_GOLD" -> getCapeGoldEffectDescription();
            case "CAPE_ICE" -> getCapeIceEffectDescription();
            case "CAPE_VOID" -> getCapeVoidEffectDescription();
            case "CAPE_SUNSET" -> getCapeSunsetEffectDescription();
            case "PET_CAT" -> getPetCatEffectDescription();
            case "PET_WOLF" -> getPetWolfEffectDescription();
            case "PET_RABBIT" -> getPetRabbitEffectDescription();
            case "PET_FOX" -> getPetFoxEffectDescription();
            case "PET_PIG" -> getPetPigEffectDescription();
            case "PET_SHEEP" -> getPetSheepEffectDescription();
            case "PET_CHICKEN" -> getPetChickenEffectDescription();
            case "PET_SLIME" -> getPetSlimeEffectDescription();
            case "PET_BEE" -> getPetBeeEffectDescription();
            case "PET_TURTLE" -> getPetTurtleEffectDescription();
            case "PET_FROG" -> getPetFrogEffectDescription();
            case "SNOWBALL" -> getGunSnowballDescription();
            case "FIREBALL" -> getGunFireballDescription();
            case "EXPLOSION" -> getGunExplosionDescription();
            case "METEOR" -> getGunMeteorDescription();

            default -> List.of();
        };
    }

    public String getGunsEffectsMenuName() {
        return text("guns-effects-menu-name");
    }

    public String getGunsEffectsItemName() {
        return text("guns-effects-item-name");
    }

    public java.util.List<String> getGunsEffectsItemLore() {
        return lines("guns-effects-item-lore");
    }

    public String getGunSnowballName() {
        return text("gun-snowball-name");
    }

    public java.util.List<String> getGunSnowballDescription() {
        return lines("gun-snowball-description");
    }

    public String getGunFireballName() {
        return text("gun-fireball-name");
    }

    public java.util.List<String> getGunFireballDescription() {
        return lines("gun-fireball-description");
    }

    public String getGunExplosionName() {
        return text("gun-explosion-name");
    }

    public java.util.List<String> getGunExplosionDescription() {
        return lines("gun-explosion-description");
    }

    public String getGunMeteorName() {
        return text("gun-meteor-name");
    }

    public java.util.List<String> getGunMeteorDescription() {
        return lines("gun-meteor-description");
    }
}
