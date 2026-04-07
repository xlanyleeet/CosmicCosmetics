package com.siliqon.cosmiccosmetics.guis;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import org.bukkit.entity.Player;

public class BalloonsCosmetics extends EffectSelectionMenu {

    public BalloonsCosmetics(CosmeticsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected EffectForm getForm() {
        return EffectForm.BALLOONS;
    }

    @Override
    protected String getMenuTitle(Player player) {
        return plugin.getLang(player).getBalloonsEffectsMenuName();
    }
}
