package com.siliqon.cosmiccosmetics.guis.lib;

import org.bukkit.entity.Player;

public class GUIManager {

    public void openGUI(InventoryGUI gui, Player player) {
        gui.open(player);
    }
}
