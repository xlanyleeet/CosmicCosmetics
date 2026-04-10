package com.siliqon.cosmiccosmetics.guis;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import org.bukkit.entity.Player;

public class GlowCosmetics extends EffectSelectionMenu {

    public GlowCosmetics(CosmeticsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected EffectForm getForm() {
        return EffectForm.GLOW;
    }

    @Override
    protected String getMenuTitle(Player player) {
        return plugin.getLang(player).getGlowEffectsMenuName();
    }
}
