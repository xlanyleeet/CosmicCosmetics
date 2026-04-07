package com.siliqon.cosmiccosmetics.guis;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import org.bukkit.entity.Player;

public class PetsCosmetics extends EffectSelectionMenu {

    public PetsCosmetics(CosmeticsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected EffectForm getForm() {
        return EffectForm.PETS;
    }

    @Override
    protected String getMenuTitle(Player player) {
        return plugin.getLang(player).getPetsEffectsMenuName();
    }
}
