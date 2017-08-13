package ninja.egg82.plugin.reflection.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.exceptions.ArgumentNullException;

public final class PlayerHelper_1_9 implements IPlayerHelper {
	//vars
	
	//constructor
	public PlayerHelper_1_9() {
		
	}
	
	//public
	public ItemStack getItemInMainHand(Player player) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		return player.getInventory().getItemInMainHand();
	}
	public ItemStack getItemInOffHand(Player player) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		return player.getInventory().getItemInOffHand();
	}
	public void setItemInMainHand(Player player, ItemStack item) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		player.getInventory().setItemInMainHand(item);
	}
	public void setItemInOffHand(Player player, ItemStack item) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		player.getInventory().setItemInOffHand(item);
	}
	
	//private
	
}
