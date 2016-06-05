package ninja.egg82.plugin.utils.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public interface IPermissionsManager {
	void initialize(PluginManager manager);
	void destroy();
	void addPermission(String permission);
	void removePermission(String permission);
	void clearPermissions();
	boolean hasPermission(String permission);
	boolean playerHasPermission(Player player, String permission);
}