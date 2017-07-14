package ninja.egg82.plugin.reflection.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class PlayerHelper_1_8 implements IPlayerHelper {
	//vars
	
	//constructor
	public PlayerHelper_1_8() {
		
	}
	
	//public
	@SuppressWarnings("deprecation")
	public ItemStack getItemInMainHand(Player player) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		return player.getItemInHand();
	}
	public ItemStack getItemInOffHand(Player player) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		return null;
	}
	@SuppressWarnings("deprecation")
	public void setItemInMainHand(Player player, ItemStack item) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		player.setItemInHand(item);
	}
	@SuppressWarnings("deprecation")
	public void setItemInOffHand(Player player, ItemStack item) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		player.setItemInHand(item);
	}
	
	//private
	
}
