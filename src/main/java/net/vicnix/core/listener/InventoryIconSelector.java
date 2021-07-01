package net.vicnix.core.listener;

import net.vicnix.core.VicnixCore;
import net.vicnix.core.menu.type.ChestMenu;
import net.vicnix.core.utils.VicnixIcon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class InventoryIconSelector extends ChestMenu {

    public InventoryIconSelector() {
        super(ChatColor.LIGHT_PURPLE + "Icon selector", 27);
    }

    @Override
    public void open(Player player) {
        this.update(player);

        super.open(player);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent ev) {
        Inventory clickedInventory = ev.getClickedInventory();

        Inventory topInventory = ev.getView().getTopInventory();

        if (!topInventory.equals(this.inventory)) {
            return;
        }

        if (topInventory.equals(clickedInventory)) {
            ItemStack item = ev.getCurrentItem();

            if (item == null || item.getType() == Material.AIR) return;

            Player player = (Player) ev.getWhoClicked();

            player.sendMessage(ChatColor.RED + "Format");
        } else if (!topInventory.equals(clickedInventory) && ev.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || ev.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            ev.setCancelled(true);
        }
    }

    private void update(Player player) {
        this.inventory.clear();

        for (VicnixIcon icon : VicnixIcon.values()) {
            ItemStack itemStack = new ItemStack(Material.PAPER);

            String displayName = icon.getFormat();

            if (icon.getFormat().equals(VicnixCore.getIconFormat(player.getUniqueId()))) {
                displayName += ChatColor.GREEN + ChatColor.BOLD.toString() + " - SELECTED";
            }

            itemStack.setItemMeta(getItemMeta(itemStack.getItemMeta(), displayName, null, null));

            this.inventory.addItem(itemStack);
        }

        player.updateInventory();
    }

    private ItemMeta getItemMeta(ItemMeta itemMeta, String displayName, String owner, List<String> lore) {
        itemMeta.setDisplayName(displayName);

        if (owner != null && itemMeta instanceof SkullMeta) {
            ((SkullMeta) itemMeta).setOwner(owner);
        }

        if (lore != null) {
            itemMeta.setLore(lore);
        }

        return itemMeta;
    }
}