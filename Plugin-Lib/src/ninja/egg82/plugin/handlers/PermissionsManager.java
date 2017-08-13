package ninja.egg82.plugin.handlers;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import ninja.egg82.exceptions.ArgumentNullException;

public final class PermissionsManager {
	//vars
	private HashMap<String, Permission> permissions = new HashMap<String, Permission>();
	private PluginManager manager = Bukkit.getServer().getPluginManager();
	
	//constructor
	public PermissionsManager() {
		
	}
	
	//public
	public synchronized void addPermission(String permission) {
		if (permission == null) {
			throw new ArgumentNullException("permission");
		}
		
		permission = permission.toLowerCase();
		if (permissions.containsKey(permission)) {
			return;
		}
		
		Permission p = new Permission(permission);
		permissions.put(permission, p);
		manager.addPermission(p);
	}
	public synchronized void removePermission(String permission) {
		if (permission == null) {
			throw new ArgumentNullException("permission");
		}
		
		permission = permission.toLowerCase();
		Permission p = permissions.get(permission);
		
		if (p == null) {
			return;
		}
		
		permissions.remove(permission);
		manager.removePermission(p);
	}
	public synchronized boolean hasPermission(String permission) {
		if (permission == null) {
			return false;
		}
		
		permission = permission.toLowerCase();
		return permissions.containsKey(permission);
	}
	public synchronized void clear() {
		permissions.forEach((k, v) -> {
			manager.removePermission(v);
		});
		permissions.clear();
	}
	
	public synchronized boolean playerHasPermission(Player player, String permission) {
		if (player == null) {
			return false;
		}
		if (permission == null) {
			return false;
		}
		
		permission = permission.toLowerCase();
		Permission p = permissions.get(permission);
		
		if (p == null) {
			return false;
		}
		return player.hasPermission(p);
	}
	
	//private
	
}