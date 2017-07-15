package ninja.egg82.plugin.reflection.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.exceptions.ArgumentNullException;

public final class PlayerHelper_1_8 implements IPlayerHelper {
	//vars
	
	//constructor
	public PlayerHelper_1_8() {
		
	}
	
	//public
	@SuppressWarnings("deprecation")
	public ItemStack getItemInMainHand(Player player) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		return player.getItemInHand();
	}
	public ItemStack getItemInOffHand(Player player) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		return null;
	}
	@SuppressWarnings("deprecation")
	public void setItemInMainHand(Player player, ItemStack item) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		player.setItemInHand(item);
	}
	@SuppressWarnings("deprecation")
	public void setItemInOffHand(Player player, ItemStack item) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		if (player.getInventory().addItem(item).size() > 0) {
			player.setItemInHand(item);
		}
	}
	
	//private
	
}
