package com.siliqon.cosmiccosmetics.registries;

import com.siliqon.cosmiccosmetics.custom.Registry;
import com.siliqon.cosmiccosmetics.enums.Cape;
import com.siliqon.cosmiccosmetics.enums.Halo;
import com.siliqon.cosmiccosmetics.enums.Pet;
import com.siliqon.cosmiccosmetics.enums.Gun;
import com.siliqon.cosmiccosmetics.enums.Trail;
import com.siliqon.cosmiccosmetics.enums.Glow;
import org.bukkit.Material;

public class EffectMaterialRegistry extends Registry<Enum<?>, Material> {
    private static final EffectMaterialRegistry INSTANCE = new EffectMaterialRegistry();

    public void populate() {
        this.register(Halo.SPLASH, Material.WATER_BUCKET);
        this.register(Halo.ENDER, Material.DRAGON_EGG);
        this.register(Halo.FLAME, Material.FLINT_AND_STEEL);
        this.register(Halo.CHARM, Material.EMERALD);
        this.register(Halo.LOVE, Material.RED_DYE);
        this.register(Halo.BLOSSOM, Material.CHERRY_LEAVES);
        this.register(Halo.ENCHANTED, Material.ENCHANTED_BOOK);
        this.register(Halo.TEARS, Material.CRYING_OBSIDIAN);
        this.register(Halo.MUSICAL, Material.NOTE_BLOCK);
        this.register(Halo.RAINBOW, Material.MAGENTA_DYE);
        this.register(Halo.CROWN, Material.GOLD_NUGGET);

        this.register(Trail.ENDER, Material.DRAGON_EGG);
        this.register(Trail.CHARM, Material.EMERALD);
        this.register(Trail.LOVE, Material.RED_DYE);
        this.register(Trail.BLOSSOM, Material.CHERRY_LEAVES);
        this.register(Trail.MUSICAL, Material.NOTE_BLOCK);
        this.register(Trail.RAINBOW, Material.MAGENTA_DYE);
        this.register(Trail.CLOUDY, Material.SNOW_BLOCK);
        this.register(Trail.SHADOW_FOOTPRINTS, Material.BLACK_WOOL);
        this.register(Trail.SNOW_FOOTPRINTS, Material.POWDER_SNOW_BUCKET);

        this.register(Trail.FLAME_HELIX, Material.FIRE_CHARGE);
        this.register(Trail.REDSTONE_HELIX, Material.REDSTONE_BLOCK);
        this.register(Trail.SOUL_FIRE_HELIX, Material.SOUL_CAMPFIRE);

        this.register(Cape.CAPE_CRIMSON, Material.RED_BANNER);
        this.register(Cape.CAPE_AZURE, Material.BLUE_BANNER);
        this.register(Cape.CAPE_EMERALD, Material.GREEN_BANNER);
        this.register(Cape.CAPE_VIOLET, Material.PURPLE_BANNER);
        this.register(Cape.CAPE_RAINBOW, Material.WHITE_BANNER);
        this.register(Cape.CAPE_GOLD, Material.YELLOW_BANNER);
        this.register(Cape.CAPE_ICE, Material.LIGHT_BLUE_BANNER);
        this.register(Cape.CAPE_VOID, Material.BLACK_BANNER);
        this.register(Cape.CAPE_SUNSET, Material.ORANGE_BANNER);

        this.register(Pet.PET_CAT, Material.COD);
        this.register(Pet.PET_WOLF, Material.BONE);
        this.register(Pet.PET_RABBIT, Material.CARROT);
        this.register(Pet.PET_FOX, Material.SWEET_BERRIES);
        this.register(Pet.PET_PIG, Material.PORKCHOP);
        this.register(Pet.PET_SHEEP, Material.WHITE_WOOL);
        this.register(Pet.PET_CHICKEN, Material.CHICKEN);
        this.register(Pet.PET_SLIME, Material.SLIME_BALL);
        this.register(Pet.PET_TURTLE, Material.TURTLE_SCUTE);
        this.register(Pet.PET_BEE, Material.HONEYCOMB);
        this.register(Pet.PET_FROG, Material.FROG_SPAWN_EGG);

        this.register(Gun.SNOWBALL, Material.SNOWBALL);
        this.register(Gun.FIREBALL, Material.FIRE_CHARGE);
        this.register(Gun.EXPLOSION, Material.TNT);
        this.register(Gun.METEOR, Material.MAGMA_BLOCK);

        this.register(Glow.BLACK, Material.BLACK_DYE);
        this.register(Glow.DARK_BLUE, Material.BLUE_DYE);
        this.register(Glow.DARK_GREEN, Material.GREEN_DYE);
        this.register(Glow.DARK_AQUA, Material.CYAN_DYE);
        this.register(Glow.DARK_RED, Material.RED_DYE);
        this.register(Glow.DARK_PURPLE, Material.PURPLE_DYE);
        this.register(Glow.GOLD, Material.ORANGE_DYE);
        this.register(Glow.GRAY, Material.LIGHT_GRAY_DYE);
        this.register(Glow.DARK_GRAY, Material.GRAY_DYE);
        this.register(Glow.BLUE, Material.LIGHT_BLUE_DYE);
        this.register(Glow.GREEN, Material.LIME_DYE);
        this.register(Glow.AQUA, Material.LIGHT_BLUE_DYE);
        this.register(Glow.RED, Material.RED_DYE);
        this.register(Glow.LIGHT_PURPLE, Material.PINK_DYE);
        this.register(Glow.YELLOW, Material.YELLOW_DYE);
        this.register(Glow.WHITE, Material.WHITE_DYE);
    }

    public static EffectMaterialRegistry getInstance() {
        return INSTANCE;
    }
}
