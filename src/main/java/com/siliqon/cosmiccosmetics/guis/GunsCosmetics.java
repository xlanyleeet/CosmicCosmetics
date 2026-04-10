package com.siliqon.cosmiccosmetics.guis;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import org.bukkit.entity.Player;

public class GunsCosmetics extends EffectSelectionMenu {

    public GunsCosmetics(CosmeticsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected EffectForm getForm() {
        return EffectForm.FUNGUN;
    }

    @Override
    protected String getMenuTitle(Player player) {
        return plugin.getLang(player).getGunsEffectsMenuName();
    }
}