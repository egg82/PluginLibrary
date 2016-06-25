package ninja.egg82.plugin.reflection.player.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IPlayerUtil {
	ItemStack getItemInMainHand(Player player);
	void setItemInMainHand(Player player, ItemStack item);
}
