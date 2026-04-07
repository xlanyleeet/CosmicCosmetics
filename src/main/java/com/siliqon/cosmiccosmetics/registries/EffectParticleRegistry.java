package com.siliqon.cosmiccosmetics.registries;

import com.siliqon.cosmiccosmetics.custom.Registry;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import org.bukkit.Particle;

public class EffectParticleRegistry extends Registry<EffectType, Particle> {
    private static final EffectParticleRegistry INSTANCE = new EffectParticleRegistry();

    public void populate() {
        this.register(EffectType.SPLASH, Particle.SPLASH);
        this.register(EffectType.ENDER, Particle.DRAGON_BREATH);
        this.register(EffectType.FLAME, Particle.FLAME);
        this.register(EffectType.CHARM, Particle.HAPPY_VILLAGER);
        this.register(EffectType.LOVE, Particle.HEART);
        this.register(EffectType.BLOSSOM, Particle.CHERRY_LEAVES);
        this.register(EffectType.ENCHANTED, Particle.ENCHANT);
        this.register(EffectType.TEARS, Particle.FALLING_WATER);
        this.register(EffectType.MUSICAL, Particle.NOTE);
        this.register(EffectType.RAINBOW, Particle.DUST);
        this.register(EffectType.CLOUDY, Particle.EFFECT);
        this.register(EffectType.CAPE_CRIMSON, Particle.DUST);
        this.register(EffectType.CAPE_AZURE, Particle.DUST);
        this.register(EffectType.CAPE_EMERALD, Particle.DUST);
        this.register(EffectType.CAPE_VIOLET, Particle.DUST);
        this.register(EffectType.CAPE_RAINBOW, Particle.DUST);
        this.register(EffectType.CAPE_GOLD, Particle.DUST);
        this.register(EffectType.CAPE_ICE, Particle.DUST);
        this.register(EffectType.CAPE_VOID, Particle.DUST);
        this.register(EffectType.CAPE_SUNSET, Particle.DUST);
        this.register(EffectType.BALLOONS, Particle.DUST);
        this.register(EffectType.BALLOON_RED, Particle.DUST);
        this.register(EffectType.BALLOON_BLUE, Particle.DUST);
        this.register(EffectType.BALLOON_GREEN, Particle.DUST);
        this.register(EffectType.BALLOON_YELLOW, Particle.DUST);
        this.register(EffectType.BALLOON_PURPLE, Particle.DUST);
        this.register(EffectType.BALLOON_ORANGE, Particle.DUST);
        this.register(EffectType.BALLOON_CYAN, Particle.DUST);
        this.register(EffectType.BALLOON_PINK, Particle.DUST);
    }

    public static EffectParticleRegistry getInstance() {
        return INSTANCE;
    }
}
