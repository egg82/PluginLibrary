package ninja.egg82.plugin.reflection.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class PlayerUtil_1_9 implements IPlayerUtil {
	//vars
	
	//constructor
	public PlayerUtil_1_9() {
		
	}
	
	//public
	public ItemStack getItemInMainHand(Player player) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		return player.getInventory().getItemInMainHand();
	}
	public ItemStack getItemInOffHand(Player player) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		return player.getInventory().getItemInOffHand();
	}
	public void setItemInMainHand(Player player, ItemStack item) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		player.getInventory().setItemInMainHand(item);
	}
	public void setItemInOffHand(Player player, ItemStack item) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		player.getInventory().setItemInOffHand(item);
	}
	
	//private
	
}
