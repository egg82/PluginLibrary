package ninja.egg82.plugin.handlers;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import com.koloboke.collect.map.hash.HashObjObjMap;
import com.koloboke.collect.map.hash.HashObjObjMaps;

import ninja.egg82.patterns.ServiceLocator;

public class PermissionsManager {
	//vars
	private HashObjObjMap<String, Permission> permissions = HashObjObjMaps.<String, Permission> newMutableMap();
	private PluginManager manager = (PluginManager) ServiceLocator.getService(PluginManager.class);
	
	//constructor
	public PermissionsManager() {
		
	}
	
	//public
	public synchronized void addPermission(String permission) {
		if (permission == null) {
			throw new IllegalArgumentException("permission cannot be null.");
		}
		
		if (permissions.containsKey(permission)) {
			return;
		}
		
		Permission p = new Permission(permission);
		permissions.put(permission, p);
		manager.addPermission(p);
	}
	public synchronized void removePermission(String permission) {
		if (permission == null) {
			throw new IllegalArgumentException("permission cannot be null.");
		}
		
		Permission p = permissions.get(permission);
		
		if (p == null) {
			return;
		}
		
		permissions.remove(permission);
		manager.removePermission(p);
	}
	public synchronized boolean hasPermission(String permission) {
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
			throw new IllegalArgumentException("player cannot be null.");
		}
		if (permission == null) {
			return false;
		}
		
		Permission p = permissions.get(permission);
		
		if (p == null) {
			return false;
		}
		return player.hasPermission(p);
	}
	
	//private
	
}