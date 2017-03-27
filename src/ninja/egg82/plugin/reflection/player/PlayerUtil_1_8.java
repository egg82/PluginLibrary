package ninja.egg82.plugin.reflection.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class PlayerUtil_1_8 implements IPlayerUtil {
	//vars
	
	//constructor
	public PlayerUtil_1_8() {
		
	}
	
	//public
	@SuppressWarnings("deprecation")
	public ItemStack getItemInMainHand(Player player) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		return player.getItemInHand();
	}
	@SuppressWarnings("deprecation")
	public ItemStack getItemInOffHand(Player player) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		return player.getItemInHand();
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
