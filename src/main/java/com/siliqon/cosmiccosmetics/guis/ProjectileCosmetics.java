package com.siliqon.cosmiccosmetics.guis;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import org.bukkit.entity.Player;

public class ProjectileCosmetics extends EffectSelectionMenu {

    public ProjectileCosmetics(CosmeticsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected EffectForm getForm() {
        return EffectForm.PROJECTILE;
    }

    @Override
    protected String getMenuTitle(Player player) {
        return plugin.getLang(player).getProjectileEffectsMenuName();
    }
}
