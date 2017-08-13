package ninja.egg82.plugin.reflection.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IPlayerHelper {
	//functions
	ItemStack getItemInMainHand(Player player);
	ItemStack getItemInOffHand(Player player);
	void setItemInMainHand(Player player, ItemStack item);
	void setItemInOffHand(Player player, ItemStack item);
}
