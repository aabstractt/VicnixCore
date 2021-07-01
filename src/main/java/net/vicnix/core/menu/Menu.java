package net.vicnix.core.menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

public interface Menu extends InventoryHolder {

    default void open(Player player) {
        player.openInventory(this.getInventory());
    }

    void onInventoryClick(InventoryClickEvent event);

    default void onInventoryDrag(InventoryDragEvent ev) {
        if (InventoryUtil.clickedTopInventory(ev)) {
            ev.setCancelled(true);
        }
    }

    default void onInventoryClose(InventoryCloseEvent ev) {
    }
}
