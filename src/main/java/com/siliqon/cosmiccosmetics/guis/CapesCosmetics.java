package com.siliqon.cosmiccosmetics.guis;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import org.bukkit.entity.Player;

public class CapesCosmetics extends EffectSelectionMenu {

    public CapesCosmetics(CosmeticsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected EffectForm getForm() {
        return EffectForm.CAPES;
    }

    @Override
    protected String getMenuTitle(Player player) {
        return plugin.getLang(player).getCapesEffectsMenuName();
    }
}
