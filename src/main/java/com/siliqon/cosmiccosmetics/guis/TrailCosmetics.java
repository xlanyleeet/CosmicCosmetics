package com.siliqon.cosmiccosmetics.guis;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import org.bukkit.entity.Player;

public class TrailCosmetics extends EffectSelectionMenu {

    public TrailCosmetics(CosmeticsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected EffectForm getForm() {
        return EffectForm.TRAIL;
    }

    @Override
    protected String getMenuTitle(Player player) {
        return plugin.getLang(player).getTrailEffectsMenuName();
    }
}
