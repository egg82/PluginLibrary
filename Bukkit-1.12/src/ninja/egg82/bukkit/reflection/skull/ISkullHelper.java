package ninja.egg82.bukkit.reflection.skull;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ISkullHelper {
	//functions
	ItemStack createSkull(UUID owner);
	ItemStack createSkull(String owner);
	ItemStack createSkull(Player owner);
	ItemStack createSkull(OfflinePlayer owner);
	
	OfflinePlayer getOwner(ItemStack stack);
}
