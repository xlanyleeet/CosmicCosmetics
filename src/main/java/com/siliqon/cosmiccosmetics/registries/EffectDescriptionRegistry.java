package com.siliqon.cosmiccosmetics.registries;

import com.siliqon.cosmiccosmetics.custom.Registry;
import com.siliqon.cosmiccosmetics.enums.Cape;
import com.siliqon.cosmiccosmetics.enums.Halo;
import com.siliqon.cosmiccosmetics.enums.Pet;
import com.siliqon.cosmiccosmetics.enums.Gun;
import com.siliqon.cosmiccosmetics.enums.Trail;
import com.siliqon.cosmiccosmetics.files.LanguageCache;
import com.siliqon.cosmiccosmetics.CosmeticsPlugin;

import java.util.List;

public class EffectDescriptionRegistry extends Registry<Enum<?>, List<String>> {
    private static final EffectDescriptionRegistry INSTANCE = new EffectDescriptionRegistry();

    public void populate() {
        LanguageCache lang = CosmeticsPlugin.getInstance().getLang();
        this.register(Halo.SPLASH, lang.getSplashEffectDescription());
        this.register(Halo.ENDER, lang.getEnderEffectDescription());
        this.register(Halo.FLAME, lang.getFlameEffectDescription());
        this.register(Halo.CHARM, lang.getCharmEffectDescription());
        this.register(Halo.LOVE, lang.getLoveEffectDescription());
        this.register(Halo.BLOSSOM, lang.getBlossomEffectDescription());
        this.register(Halo.ENCHANTED, lang.getEnchantedEffectDescription());
        this.register(Halo.TEARS, lang.getTearsEffectDescription());
        this.register(Halo.MUSICAL, lang.getMusicalEffectDescription());
        this.register(Halo.RAINBOW, lang.getRainbowEffectDescription());

        this.register(Trail.ENDER, lang.getEnderEffectDescription());
        this.register(Trail.CHARM, lang.getCharmEffectDescription());
        this.register(Trail.LOVE, lang.getLoveEffectDescription());
        this.register(Trail.BLOSSOM, lang.getBlossomEffectDescription());
        this.register(Trail.MUSICAL, lang.getMusicalEffectDescription());
        this.register(Trail.RAINBOW, lang.getRainbowEffectDescription());
        this.register(Trail.CLOUDY, lang.getCloudEffectDescription());

        this.register(Cape.CAPE_CRIMSON, lang.getCapeCrimsonEffectDescription());
        this.register(Cape.CAPE_AZURE, lang.getCapeAzureEffectDescription());
        this.register(Cape.CAPE_EMERALD, lang.getCapeEmeraldEffectDescription());
        this.register(Cape.CAPE_VIOLET, lang.getCapeVioletEffectDescription());
        this.register(Cape.CAPE_RAINBOW, lang.getCapeRainbowEffectDescription());
        this.register(Cape.CAPE_GOLD, lang.getCapeGoldEffectDescription());
        this.register(Cape.CAPE_ICE, lang.getCapeIceEffectDescription());
        this.register(Cape.CAPE_VOID, lang.getCapeVoidEffectDescription());
        this.register(Cape.CAPE_SUNSET, lang.getCapeSunsetEffectDescription());

        this.register(Pet.PET_CAT, lang.getPetCatEffectDescription());
        this.register(Pet.PET_WOLF, lang.getPetWolfEffectDescription());
        this.register(Pet.PET_RABBIT, lang.getPetRabbitEffectDescription());
        this.register(Pet.PET_FOX, lang.getPetFoxEffectDescription());
        this.register(Pet.PET_PIG, lang.getPetPigEffectDescription());
        this.register(Pet.PET_SHEEP, lang.getPetSheepEffectDescription());
        this.register(Pet.PET_CHICKEN, lang.getPetChickenEffectDescription());
        this.register(Pet.PET_SLIME, lang.getPetSlimeEffectDescription());
        this.register(Pet.PET_TURTLE, lang.getPetTurtleEffectDescription());
        this.register(Pet.PET_BEE, lang.getPetBeeEffectDescription());
        this.register(Pet.PET_FROG, lang.getPetFrogEffectDescription());

        this.register(Gun.SNOWBALL, lang.getGunSnowballDescription());
        this.register(Gun.FIREBALL, lang.getGunFireballDescription());
        this.register(Gun.EXPLOSION, lang.getGunExplosionDescription());
        this.register(Gun.METEOR, lang.getGunMeteorDescription());

        // Note: Cow is missing from Pet, so I removed it.
    }

    public static EffectDescriptionRegistry getInstance() {
        return INSTANCE;
    }
}
