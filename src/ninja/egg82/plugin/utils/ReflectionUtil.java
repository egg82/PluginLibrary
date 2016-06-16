package ninja.egg82.plugin.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ninja.egg82.utils.Util;

public class ReflectionUtil {
	//vars
	
	//constructor
	public ReflectionUtil() {
		
	}
	
	//public
	@SuppressWarnings("deprecation")
	public static ItemStack getItemInMainHand(Player player) {
		if (player == null) {
			return null;
		}
		
		PlayerInventory inv = player.getInventory();
		
		if (Util.getMethod("getItemInMainHand", inv) != null) {
			return inv.getItemInMainHand();
		} else {
			return player.getItemInHand();
		}
	}
	@SuppressWarnings("deprecation")
	public static void setItemInMainHand(Player player, ItemStack item) {
		if (player == null) {
			return;
		}
		
		PlayerInventory inv = player.getInventory();
		
		if (Util.getMethod("setItemInMainHand", inv) != null) {
			inv.setItemInMainHand(item);
		} else {
			player.setItemInHand(item);
		}
	}
	
	//private
	
}
