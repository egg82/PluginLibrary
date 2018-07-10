package ninja.egg82.bukkit.reflection.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IPlayerHelper {
	//functions
	ItemStack getItemInMainHand(Player player);
	ItemStack getItemInOffHand(Player player);
	void setItemInMainHand(Player player, ItemStack item);
	void setItemInOffHand(Player player, ItemStack item);
	
	void hidePlayer(Player player, Player playerToHide);
	void showPlayer(Player player, Player playerToShow);
	
	int getPing(Player player);
	
	boolean supportsOffhand();
}
