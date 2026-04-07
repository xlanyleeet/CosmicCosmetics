package com.siliqon.cosmiccosmetics.registries;

import com.siliqon.cosmiccosmetics.custom.Registry;
import com.siliqon.cosmiccosmetics.enums.EffectType;

public class EffectDensityRegistry extends Registry<EffectType, Integer> {
    private static final EffectDensityRegistry INSTANCE = new EffectDensityRegistry();

    public void populate() {
        this.register(EffectType.SPLASH, 7);
        this.register(EffectType.ENDER, 7);
        this.register(EffectType.FLAME, 7);
        this.register(EffectType.CHARM, 7);
        this.register(EffectType.LOVE, 2);
        this.register(EffectType.BLOSSOM, 7);
        this.register(EffectType.ENCHANTED, 15);
        this.register(EffectType.TEARS, 7);
        this.register(EffectType.MUSICAL, 4);
        this.register(EffectType.RAINBOW, 7);
        this.register(EffectType.CLOUDY, 5);
        this.register(EffectType.CAPE_CRIMSON, 1);
        this.register(EffectType.CAPE_AZURE, 1);
        this.register(EffectType.CAPE_EMERALD, 1);
        this.register(EffectType.CAPE_VIOLET, 1);
        this.register(EffectType.CAPE_RAINBOW, 1);
        this.register(EffectType.CAPE_GOLD, 1);
        this.register(EffectType.CAPE_ICE, 1);
        this.register(EffectType.CAPE_VOID, 1);
        this.register(EffectType.CAPE_SUNSET, 1);
        this.register(EffectType.BALLOONS, 1);
        this.register(EffectType.BALLOON_RED, 1);
        this.register(EffectType.BALLOON_BLUE, 1);
        this.register(EffectType.BALLOON_GREEN, 1);
        this.register(EffectType.BALLOON_YELLOW, 1);
        this.register(EffectType.BALLOON_PURPLE, 1);
        this.register(EffectType.BALLOON_ORANGE, 1);
        this.register(EffectType.BALLOON_CYAN, 1);
        this.register(EffectType.BALLOON_PINK, 1);

    }

    public static EffectDensityRegistry getInstance() {
        return INSTANCE;
    }
}
