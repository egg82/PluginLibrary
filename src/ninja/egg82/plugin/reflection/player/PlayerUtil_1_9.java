package ninja.egg82.plugin.reflection.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.plugin.reflection.player.interfaces.IPlayerUtil;

public class PlayerUtil_1_9 implements IPlayerUtil {
	//vars
	
	//constructor
	public PlayerUtil_1_9() {
		
	}
	
	//public
	public ItemStack getItemInMainHand(Player player) {
		if (player == null) {
			return null;
		}
		
		return player.getInventory().getItemInMainHand();
	}
	public void setItemInMainHand(Player player, ItemStack item) {
		if (player == null) {
			return;
		}
		
		player.getInventory().setItemInMainHand(item);
	}
	
	//private
	
}
