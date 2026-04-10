package com.siliqon.cosmiccosmetics.registries;

import com.siliqon.cosmiccosmetics.custom.Registry;
import com.siliqon.cosmiccosmetics.enums.Cape;
import com.siliqon.cosmiccosmetics.enums.Halo;
import com.siliqon.cosmiccosmetics.enums.Trail;

public class EffectDensityRegistry extends Registry<Enum<?>, Integer> {
    private static final EffectDensityRegistry INSTANCE = new EffectDensityRegistry();

    public void populate() {
        this.register(Halo.SPLASH, 7);
        this.register(Halo.ENDER, 7);
        this.register(Halo.FLAME, 7);
        this.register(Halo.CHARM, 7);
        this.register(Halo.LOVE, 2);
        this.register(Halo.BLOSSOM, 7);
        this.register(Halo.ENCHANTED, 15);
        this.register(Halo.TEARS, 7);
        this.register(Halo.MUSICAL, 4);
        this.register(Halo.RAINBOW, 7);
        this.register(Halo.CROWN, 1);

        this.register(Trail.ENDER, 7);
        this.register(Trail.CHARM, 7);
        this.register(Trail.LOVE, 2);
        this.register(Trail.BLOSSOM, 7);
        this.register(Trail.MUSICAL, 4);
        this.register(Trail.RAINBOW, 7);
        this.register(Trail.CLOUDY, 5);
        this.register(Trail.SHADOW_FOOTPRINTS, 2);
        this.register(Trail.SNOW_FOOTPRINTS, 2);
        this.register(Trail.FLAME_HELIX, 2);
        this.register(Trail.REDSTONE_HELIX, 2);
        this.register(Trail.SOUL_FIRE_HELIX, 2);

        this.register(Cape.CAPE_CRIMSON, 1);
        this.register(Cape.CAPE_AZURE, 1);
        this.register(Cape.CAPE_EMERALD, 1);
        this.register(Cape.CAPE_VIOLET, 1);
        this.register(Cape.CAPE_RAINBOW, 1);
        this.register(Cape.CAPE_GOLD, 1);
        this.register(Cape.CAPE_ICE, 1);
        this.register(Cape.CAPE_VOID, 1);
        this.register(Cape.CAPE_SUNSET, 1);

    }

    public static EffectDensityRegistry getInstance() {
        return INSTANCE;
    }
}
