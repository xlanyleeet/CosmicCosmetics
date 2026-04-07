package com.siliqon.cosmiccosmetics.registries;

import com.siliqon.cosmiccosmetics.custom.Registry;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import com.siliqon.cosmiccosmetics.files.LanguageCache;

import java.util.List;

public class EffectDescriptionRegistry extends Registry<EffectType, List<String>> {
    private static final EffectDescriptionRegistry INSTANCE = new EffectDescriptionRegistry();

    public void populate() {
        LanguageCache lang = plugin.getLang();
        this.register(EffectType.SPLASH, lang.getSplashEffectDescription());
        this.register(EffectType.ENDER, lang.getEnderEffectDescription());
        this.register(EffectType.FLAME, lang.getFlameEffectDescription());
        this.register(EffectType.CHARM, lang.getCharmEffectDescription());
        this.register(EffectType.LOVE, lang.getLoveEffectDescription());
        this.register(EffectType.BLOSSOM, lang.getBlossomEffectDescription());
        this.register(EffectType.ENCHANTED, lang.getEnchantedEffectDescription());
        this.register(EffectType.TEARS, lang.getTearsEffectDescription());
        this.register(EffectType.MUSICAL, lang.getMusicalEffectDescription());
        this.register(EffectType.RAINBOW, lang.getRainbowEffectDescription());
        this.register(EffectType.CLOUDY, lang.getCloudEffectDescription());
        this.register(EffectType.CAPE_CRIMSON, lang.getCapeCrimsonEffectDescription());
        this.register(EffectType.CAPE_AZURE, lang.getCapeAzureEffectDescription());
        this.register(EffectType.CAPE_EMERALD, lang.getCapeEmeraldEffectDescription());
        this.register(EffectType.CAPE_VIOLET, lang.getCapeVioletEffectDescription());
        this.register(EffectType.CAPE_RAINBOW, lang.getCapeRainbowEffectDescription());
        this.register(EffectType.CAPE_GOLD, lang.getCapeGoldEffectDescription());
        this.register(EffectType.CAPE_ICE, lang.getCapeIceEffectDescription());
        this.register(EffectType.CAPE_VOID, lang.getCapeVoidEffectDescription());
        this.register(EffectType.CAPE_SUNSET, lang.getCapeSunsetEffectDescription());
        this.register(EffectType.BALLOONS, lang.getBalloonsEffectDescription());
        this.register(EffectType.BALLOON_RED, lang.getRedBalloonEffectDescription());
        this.register(EffectType.BALLOON_BLUE, lang.getBlueBalloonEffectDescription());
        this.register(EffectType.BALLOON_GREEN, lang.getGreenBalloonEffectDescription());
        this.register(EffectType.BALLOON_YELLOW, lang.getYellowBalloonEffectDescription());
        this.register(EffectType.BALLOON_PURPLE, lang.getPurpleBalloonEffectDescription());
        this.register(EffectType.BALLOON_ORANGE, lang.getOrangeBalloonEffectDescription());
        this.register(EffectType.BALLOON_CYAN, lang.getCyanBalloonEffectDescription());
        this.register(EffectType.BALLOON_PINK, lang.getPinkBalloonEffectDescription());
        this.register(EffectType.PET_CAT, lang.getPetCatEffectDescription());
        this.register(EffectType.PET_WOLF, lang.getPetWolfEffectDescription());
        this.register(EffectType.PET_RABBIT, lang.getPetRabbitEffectDescription());
        this.register(EffectType.PET_FOX, lang.getPetFoxEffectDescription());
        this.register(EffectType.PET_PIG, lang.getPetPigEffectDescription());
        this.register(EffectType.PET_SHEEP, lang.getPetSheepEffectDescription());
    }

    public static EffectDescriptionRegistry getInstance() {
        return INSTANCE;
    }
}
