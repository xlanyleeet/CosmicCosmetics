package com.siliqon.cosmiccosmetics.registries;

import com.siliqon.cosmiccosmetics.custom.Registry;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import org.bukkit.Material;

public class EffectMaterialRegistry extends Registry<EffectType, Material> {
    private static final EffectMaterialRegistry INSTANCE = new EffectMaterialRegistry();

    public void populate() {
        this.register(EffectType.SPLASH, Material.WATER_BUCKET);
        this.register(EffectType.ENDER, Material.DRAGON_EGG);
        this.register(EffectType.FLAME, Material.FLINT_AND_STEEL);
        this.register(EffectType.CHARM, Material.EMERALD);
        this.register(EffectType.LOVE, Material.RED_DYE);
        this.register(EffectType.BLOSSOM, Material.CHERRY_LEAVES);
        this.register(EffectType.ENCHANTED, Material.ENCHANTED_BOOK);
        this.register(EffectType.TEARS, Material.CRYING_OBSIDIAN);
        this.register(EffectType.MUSICAL, Material.NOTE_BLOCK);
        this.register(EffectType.RAINBOW, Material.MAGENTA_DYE);
        this.register(EffectType.CLOUDY, Material.SNOW_BLOCK);
        this.register(EffectType.CAPE_CRIMSON, Material.RED_BANNER);
        this.register(EffectType.CAPE_AZURE, Material.BLUE_BANNER);
        this.register(EffectType.CAPE_EMERALD, Material.GREEN_BANNER);
        this.register(EffectType.CAPE_VIOLET, Material.PURPLE_BANNER);
        this.register(EffectType.CAPE_RAINBOW, Material.WHITE_BANNER);
        this.register(EffectType.CAPE_GOLD, Material.YELLOW_BANNER);
        this.register(EffectType.CAPE_ICE, Material.LIGHT_BLUE_BANNER);
        this.register(EffectType.CAPE_VOID, Material.BLACK_BANNER);
        this.register(EffectType.CAPE_SUNSET, Material.ORANGE_BANNER);
        this.register(EffectType.BALLOONS, Material.FIREWORK_ROCKET);
        this.register(EffectType.BALLOON_RED, Material.RED_WOOL);
        this.register(EffectType.BALLOON_BLUE, Material.BLUE_WOOL);
        this.register(EffectType.BALLOON_GREEN, Material.LIME_WOOL);
        this.register(EffectType.BALLOON_YELLOW, Material.YELLOW_WOOL);
        this.register(EffectType.BALLOON_PURPLE, Material.PURPLE_WOOL);
        this.register(EffectType.BALLOON_ORANGE, Material.ORANGE_WOOL);
        this.register(EffectType.BALLOON_CYAN, Material.CYAN_WOOL);
        this.register(EffectType.BALLOON_PINK, Material.PINK_WOOL);
        this.register(EffectType.PET_CAT, Material.COD);
        this.register(EffectType.PET_WOLF, Material.BONE);
        this.register(EffectType.PET_RABBIT, Material.CARROT);
        this.register(EffectType.PET_FOX, Material.SWEET_BERRIES);
        this.register(EffectType.PET_PIG, Material.PORKCHOP);
        this.register(EffectType.PET_SHEEP, Material.WHITE_WOOL);
    }

    public static EffectMaterialRegistry getInstance() {
        return INSTANCE;
    }
}
