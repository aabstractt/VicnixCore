package net.vicnix.core.menu.type;

import lombok.Getter;
import net.vicnix.core.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public abstract class ChestMenu implements Menu {

    @Getter
    protected final Inventory inventory;

    public ChestMenu(String title, int size) {
        this.inventory = Bukkit.createInventory(this, size, title.length() > 32 ? title.substring(0, 32) : title);
    }
}