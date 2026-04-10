package com.siliqon.cosmiccosmetics.registries;

import com.siliqon.cosmiccosmetics.custom.Registry;
import com.siliqon.cosmiccosmetics.enums.Cape;
import com.siliqon.cosmiccosmetics.enums.Halo;
import com.siliqon.cosmiccosmetics.enums.Pet;
import com.siliqon.cosmiccosmetics.enums.Gun;
import com.siliqon.cosmiccosmetics.enums.Trail;
import com.siliqon.cosmiccosmetics.files.LanguageCache;
import com.siliqon.cosmiccosmetics.CosmeticsPlugin;

public class EffectNameRegistry extends Registry<Enum<?>, String> {
    private static final EffectNameRegistry INSTANCE = new EffectNameRegistry();

    public void populate() {
        LanguageCache lang = CosmeticsPlugin.getInstance().getLang();
        this.register(Halo.SPLASH, lang.getSplashEffectName());
        this.register(Halo.ENDER, lang.getEnderEffectName());
        this.register(Halo.FLAME, lang.getFlameEffectName());
        this.register(Halo.CHARM, lang.getCharmEffectName());
        this.register(Halo.LOVE, lang.getLoveEffectName());
        this.register(Halo.BLOSSOM, lang.getBlossomEffectName());
        this.register(Halo.ENCHANTED, lang.getEnchantedEffectName());
        this.register(Halo.TEARS, lang.getTearsEffectName());
        this.register(Halo.MUSICAL, lang.getMusicalEffectName());
        this.register(Halo.RAINBOW, lang.getRainbowEffectName());

        this.register(Trail.ENDER, lang.getEnderEffectName());
        this.register(Trail.CHARM, lang.getCharmEffectName());
        this.register(Trail.LOVE, lang.getLoveEffectName());
        this.register(Trail.BLOSSOM, lang.getBlossomEffectName());
        this.register(Trail.MUSICAL, lang.getMusicalEffectName());
        this.register(Trail.RAINBOW, lang.getRainbowEffectName());
        this.register(Trail.CLOUDY, lang.getCloudEffectName());

        this.register(Cape.CAPE_CRIMSON, lang.getCapeCrimsonEffectName());
        this.register(Cape.CAPE_AZURE, lang.getCapeAzureEffectName());
        this.register(Cape.CAPE_EMERALD, lang.getCapeEmeraldEffectName());
        this.register(Cape.CAPE_VIOLET, lang.getCapeVioletEffectName());
        this.register(Cape.CAPE_RAINBOW, lang.getCapeRainbowEffectName());
        this.register(Cape.CAPE_GOLD, lang.getCapeGoldEffectName());
        this.register(Cape.CAPE_ICE, lang.getCapeIceEffectName());
        this.register(Cape.CAPE_VOID, lang.getCapeVoidEffectName());
        this.register(Cape.CAPE_SUNSET, lang.getCapeSunsetEffectName());

        this.register(Pet.PET_CAT, lang.getPetCatEffectName());
        this.register(Pet.PET_WOLF, lang.getPetWolfEffectName());
        this.register(Pet.PET_RABBIT, lang.getPetRabbitEffectName());
        this.register(Pet.PET_FOX, lang.getPetFoxEffectName());
        this.register(Pet.PET_PIG, lang.getPetPigEffectName());
        this.register(Pet.PET_SHEEP, lang.getPetSheepEffectName());
        this.register(Pet.PET_CHICKEN, lang.getPetChickenEffectName());
        this.register(Pet.PET_SLIME, lang.getPetSlimeEffectName());
        this.register(Pet.PET_TURTLE, lang.getPetTurtleEffectName());
        this.register(Pet.PET_BEE, lang.getPetBeeEffectName());
        this.register(Pet.PET_FROG, lang.getPetFrogEffectName());

        this.register(Gun.SNOWBALL, lang.getGunSnowballName());
        this.register(Gun.FIREBALL, lang.getGunFireballName());
        this.register(Gun.EXPLOSION, lang.getGunExplosionName());
        this.register(Gun.METEOR, lang.getGunMeteorName());
    }

    public static EffectNameRegistry getInstance() {
        return INSTANCE;
    }
}


