package com.siliqon.cosmiccosmetics.registries;

import com.siliqon.cosmiccosmetics.custom.Registry;
import com.siliqon.cosmiccosmetics.enums.Cape;
import com.siliqon.cosmiccosmetics.enums.Halo;
import com.siliqon.cosmiccosmetics.enums.Trail;
import org.bukkit.Particle;

public class EffectParticleRegistry extends Registry<Enum<?>, Particle> {
    private static final EffectParticleRegistry INSTANCE = new EffectParticleRegistry();

    public void populate() {
        this.register(Halo.SPLASH, Particle.SPLASH);
        this.register(Halo.ENDER, Particle.DRAGON_BREATH);
        this.register(Halo.FLAME, Particle.FLAME);
        this.register(Halo.CHARM, Particle.HAPPY_VILLAGER);
        this.register(Halo.LOVE, Particle.HEART);
        this.register(Halo.BLOSSOM, Particle.CHERRY_LEAVES);
        this.register(Halo.ENCHANTED, Particle.ENCHANT);
        this.register(Halo.TEARS, Particle.FALLING_WATER);
        this.register(Halo.MUSICAL, Particle.NOTE);
        this.register(Halo.RAINBOW, Particle.DUST);
        this.register(Halo.CROWN, Particle.DUST);

        this.register(Trail.ENDER, Particle.DRAGON_BREATH);
        this.register(Trail.CHARM, Particle.HAPPY_VILLAGER);
        this.register(Trail.LOVE, Particle.HEART);
        this.register(Trail.BLOSSOM, Particle.CHERRY_LEAVES);
        this.register(Trail.MUSICAL, Particle.NOTE);
        this.register(Trail.RAINBOW, Particle.DUST);
        this.register(Trail.CLOUDY, Particle.EFFECT);
        this.register(Trail.SHADOW_FOOTPRINTS, Particle.LARGE_SMOKE);
        this.register(Trail.SNOW_FOOTPRINTS, Particle.SNOWFLAKE);
        this.register(Trail.FLAME_HELIX, Particle.FLAME);
        this.register(Trail.REDSTONE_HELIX, Particle.DUST);
        this.register(Trail.SOUL_FIRE_HELIX, Particle.SOUL_FIRE_FLAME);

        this.register(Cape.CAPE_CRIMSON, Particle.DUST);
        this.register(Cape.CAPE_AZURE, Particle.DUST);
        this.register(Cape.CAPE_EMERALD, Particle.DUST);
        this.register(Cape.CAPE_VIOLET, Particle.DUST);
        this.register(Cape.CAPE_RAINBOW, Particle.DUST);
        this.register(Cape.CAPE_GOLD, Particle.DUST);
        this.register(Cape.CAPE_ICE, Particle.DUST);
        this.register(Cape.CAPE_VOID, Particle.DUST);
        this.register(Cape.CAPE_SUNSET, Particle.DUST);
    }

    public static EffectParticleRegistry getInstance() {
        return INSTANCE;
    }
}
