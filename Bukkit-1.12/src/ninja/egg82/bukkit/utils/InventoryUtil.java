package ninja.egg82.bukkit.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryUtil {
    // vars

    // constructor
    public InventoryUtil() {

    }

    // public
    public static Inventory getClickedInventory(InventoryClickEvent event) {
        if (event.getRawSlot() < 0) {
            return null;
        } else if (event.getView().getTopInventory() != null && event.getRawSlot() < event.getView().getTopInventory().getSize()) {
            return event.getView().getTopInventory();
        } else {
            return event.getView().getBottomInventory();
        }
    }

    public static Inventory getClickedInventory(InventoryDragEvent event) {
        if (!event.getRawSlots().iterator().hasNext() || event.getRawSlots().iterator().next().intValue() < 0) {
            return null;
        } else if (event.getView().getTopInventory() != null && event.getRawSlots().iterator().next().intValue() < event.getView().getTopInventory().getSize()) {
            return event.getView().getTopInventory();
        } else {
            return event.getView().getBottomInventory();
        }
    }

    public static boolean isPrevious(ItemStack item) {
        if (item == null) {
            return false;
        }

        return item.equals(getPreviousItem());
    }

    public static boolean isNext(ItemStack item) {
        if (item == null) {
            return false;
        }

        return item.equals(getNextItem());
    }

    public static boolean isClose(ItemStack item) {
        if (item == null) {
            return false;
        }

        return item.equals(getCloseItem());
    }

    public static void fillItems(Inventory inventory, ItemStack[] items, boolean hasPrevious, boolean hasNext) {
        if (inventory == null) {
            throw new IllegalArgumentException("inventory cannot be null.");
        }
        if (items == null || items.length == 0) {
            addClose(inventory);
            return;
        }

        int level = 0;
        int totalLevels = inventory.getSize() / 9;
        int current = 0;

        while (level < totalLevels - 2) {
            for (int i = current; i < Math.min(items.length, current + 9); i++) {
                if (items[i] == null) {
                    continue;
                }

                inventory.setItem(i, items[i].clone());
            }
            current += 9;
            level++;
        }

        for (int i = current; i < Math.min(items.length, current + 5); i++) {
            if (items[i] == null) {
                continue;
            }

            inventory.setItem(i + 2, items[i].clone());
        }
        current += 5;
        for (int i = current; i < Math.min(items.length, current + 5); i++) {
            if (items[i] == null) {
                continue;
            }

            inventory.setItem(i + 6, items[i].clone());
        }

        if (hasPrevious) {
            inventory.setItem(inventory.getSize() - 9, getPreviousItem());
        }
        if (hasNext) {
            inventory.setItem(inventory.getSize() - 1, getNextItem());
        }
    }

    // private
    private static ItemStack getPreviousItem() {
        ItemStack retVal = new ItemStack(Material.STONE_BUTTON, 1);
        ItemMeta meta = retVal.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<String>();
        } else {
            lore.clear();
        }
        lore.add(ChatColor.LIGHT_PURPLE + "Previous");
        meta.setLore(lore);
        retVal.setItemMeta(meta);
        return retVal;
    }

    private static ItemStack getNextItem() {
        ItemStack retVal = new ItemStack(Material.STONE_BUTTON, 1);
        ItemMeta meta = retVal.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<String>();
        } else {
            lore.clear();
        }
        lore.add(ChatColor.LIGHT_PURPLE + "Next");
        meta.setLore(lore);
        retVal.setItemMeta(meta);
        return retVal;
    }

    private static void addClose(Inventory inventory) {
        int middle = (int) Math.floor(inventory.getSize() / 2.0d) - 5;
        inventory.setItem(middle, getCloseItem());
    }

    private static ItemStack getCloseItem() {
        ItemStack retVal = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = retVal.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<String>();
        } else {
            lore.clear();
        }
        lore.add(ChatColor.LIGHT_PURPLE + "Close");
        meta.setLore(lore);
        retVal.setItemMeta(meta);
        return retVal;
    }
}
