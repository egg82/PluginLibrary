package ninja.egg82.bukkit.reflection.skin;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import ninja.egg82.bukkit.mineskin.data.Skin;

public interface ISkinHelper {
	//functions
	ItemStack getSkull(UUID playerUuid);
	ItemStack getSkull(UUID playerUuid, boolean expensive);
	ItemStack getSkull(UUID playerUuid, int amount, boolean expensive);
	
	Skin getSkin(UUID playerUuid, boolean expensive);
}
