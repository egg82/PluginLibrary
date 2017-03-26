package ninja.egg82.plugin.utils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public interface IPermissionsManager {
	//functions
	void initialize(PluginManager manager);
	void addPermission(String permission);
	void removePermission(String permission);
	void clearPermissions();
	boolean hasPermission(String permission);
	boolean playerHasPermission(Player player, String permission);
}