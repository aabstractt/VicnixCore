package net.vicnix.core.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

public final class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent ev) {
        InventoryHolder holder = ev.getInventory().getHolder();

        if (holder instanceof Menu) {
            ((Menu) holder).onInventoryClick(ev);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent ev) {
        InventoryHolder holder = ev.getInventory().getHolder();

        if (holder instanceof Menu) {
            ((Menu) holder).onInventoryDrag(ev);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent ev) {
        InventoryHolder holder = ev.getInventory().getHolder();

        if (holder instanceof Menu) {
            ((Menu) holder).onInventoryClose(ev);
        }
    }
}