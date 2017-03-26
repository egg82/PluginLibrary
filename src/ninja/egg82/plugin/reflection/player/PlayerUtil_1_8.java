package ninja.egg82.plugin.reflection.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtil_1_8 implements IPlayerUtil {
	//vars
	
	//constructor
	public PlayerUtil_1_8() {
		
	}
	
	//public
	@SuppressWarnings("deprecation")
	public ItemStack getItemInMainHand(Player player) {
		if (player == null) {
			return null;
		}
		
		return player.getItemInHand();
	}
	@SuppressWarnings("deprecation")
	public void setItemInMainHand(Player player, ItemStack item) {
		if (player == null) {
			return;
		}
		
		player.setItemInHand(item);
	}
	
	//private
	
}
