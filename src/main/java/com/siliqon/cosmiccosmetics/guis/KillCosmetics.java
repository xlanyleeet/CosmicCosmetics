package com.siliqon.cosmiccosmetics.guis;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import org.bukkit.entity.Player;

public class KillCosmetics extends EffectSelectionMenu {

    public KillCosmetics(CosmeticsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected EffectForm getForm() {
        return EffectForm.KILL;
    }

    @Override
    protected String getMenuTitle(Player player) {
        return plugin.getLang(player).getKillEffectsMenuName();
    }
}
