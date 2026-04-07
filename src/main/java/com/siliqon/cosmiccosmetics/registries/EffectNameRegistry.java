package com.siliqon.cosmiccosmetics.registries;

import com.siliqon.cosmiccosmetics.custom.Registry;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import com.siliqon.cosmiccosmetics.files.LanguageCache;

public class EffectNameRegistry extends Registry<EffectType, String> {
    private static final EffectNameRegistry INSTANCE = new EffectNameRegistry();

    public void populate() {
        LanguageCache lang = plugin.getLang();
        this.register(EffectType.SPLASH, lang.getSplashEffectName());
        this.register(EffectType.ENDER, lang.getEnderEffectName());
        this.register(EffectType.FLAME, lang.getFlameEffectName());
        this.register(EffectType.CHARM, lang.getCharmEffectName());
        this.register(EffectType.LOVE, lang.getLoveEffectName());
        this.register(EffectType.BLOSSOM, lang.getBlossomEffectName());
        this.register(EffectType.ENCHANTED, lang.getEnchantedEffectName());
        this.register(EffectType.TEARS, lang.getTearsEffectName());
        this.register(EffectType.MUSICAL, lang.getMusicalEffectName());
        this.register(EffectType.RAINBOW, lang.getRainbowEffectName());
        this.register(EffectType.CLOUDY, lang.getCloudEffectName());
        this.register(EffectType.CAPE_CRIMSON, lang.getCapeCrimsonEffectName());
        this.register(EffectType.CAPE_AZURE, lang.getCapeAzureEffectName());
        this.register(EffectType.CAPE_EMERALD, lang.getCapeEmeraldEffectName());
        this.register(EffectType.CAPE_VIOLET, lang.getCapeVioletEffectName());
        this.register(EffectType.CAPE_RAINBOW, lang.getCapeRainbowEffectName());
        this.register(EffectType.CAPE_GOLD, lang.getCapeGoldEffectName());
        this.register(EffectType.CAPE_ICE, lang.getCapeIceEffectName());
        this.register(EffectType.CAPE_VOID, lang.getCapeVoidEffectName());
        this.register(EffectType.CAPE_SUNSET, lang.getCapeSunsetEffectName());
        this.register(EffectType.BALLOONS, lang.getBalloonsEffectName());
        this.register(EffectType.BALLOON_RED, lang.getRedBalloonEffectName());
        this.register(EffectType.BALLOON_BLUE, lang.getBlueBalloonEffectName());
        this.register(EffectType.BALLOON_GREEN, lang.getGreenBalloonEffectName());
        this.register(EffectType.BALLOON_YELLOW, lang.getYellowBalloonEffectName());
        this.register(EffectType.BALLOON_PURPLE, lang.getPurpleBalloonEffectName());
        this.register(EffectType.BALLOON_ORANGE, lang.getOrangeBalloonEffectName());
        this.register(EffectType.BALLOON_CYAN, lang.getCyanBalloonEffectName());
        this.register(EffectType.BALLOON_PINK, lang.getPinkBalloonEffectName());
        this.register(EffectType.PET_CAT, lang.getPetCatEffectName());
        this.register(EffectType.PET_WOLF, lang.getPetWolfEffectName());
        this.register(EffectType.PET_RABBIT, lang.getPetRabbitEffectName());
        this.register(EffectType.PET_FOX, lang.getPetFoxEffectName());
        this.register(EffectType.PET_PIG, lang.getPetPigEffectName());
        this.register(EffectType.PET_SHEEP, lang.getPetSheepEffectName());
    }

    public static EffectNameRegistry getInstance() {
        return INSTANCE;
    }
}
