package com.siliqon.cosmiccosmetics.guis.lib;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.siliqon.cosmiccosmetics.utils.Utils.mm;

public abstract class InventoryGUI {

    protected final CosmeticsPlugin plugin;

    protected InventoryGUI(CosmeticsPlugin plugin) {
        this.plugin = plugin;
    }

    public final void open(Player player) {
        Gui gui = createGui(player);
        gui.open(player);
    }

    protected abstract String getTitle(Player player);

    protected abstract Gui createGui(Player player);

    protected final GuiItem button(Player viewer, Function<Player, ItemStack> itemProvider, Consumer<Player> onClick) {
        return new GuiItem(itemProvider.apply(viewer), event -> {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player player) {
                onClick.accept(player);
            }
        });
    }

    protected final GuiItem staticItem(ItemStack stack) {
        return new GuiItem(stack, event -> event.setCancelled(true));
    }

    protected final Gui createMenu(int rows, String title, ItemStack background) {
        Gui gui = Gui.gui()
                .rows(rows)
                .title(mm(title))
                .create();

        gui.setDefaultClickAction(event -> event.setCancelled(true));
        for (int i = 0; i < rows * 9; i++) {
            gui.setItem(i, staticItem(background));
        }

        return gui;
    }

    protected final void setItem(Gui gui, int slot, GuiItem item) {
        if (slot < 0) {
            return;
        }

        gui.setItem(slot, item);
    }
}
