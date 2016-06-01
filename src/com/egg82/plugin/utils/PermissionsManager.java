package com.egg82.plugin.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import com.egg82.plugin.utils.interfaces.IPermissionsManager;

public class PermissionsManager implements IPermissionsManager {
	//vars
	private HashMap<String, Permission> permissions = new HashMap<String, Permission>();
	private PluginManager manager = null;
	
	private boolean initialized = false;
	
	//constructor
	public PermissionsManager() {
		
	}
	
	//public
	public void initialize(PluginManager manager) {
		if (manager == null || initialized) {
			return;
		}
		
		this.manager = manager;
		initialized = true;
	}
	public void destroy() {
		
	}
	
	public void addPermission(String permission) {
		if (permissions.containsKey(permission)) {
			return;
		}
		permissions.put(permission, new Permission(permission));
		manager.addPermission(permissions.get(permission));
	}
	public void removePermission(String permission) {
		manager.removePermission(permissions.get(permission));
		permissions.remove(permission);
	}
	
	public void clearPermissions() {
		Iterator<Entry<String, Permission>> i = permissions.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry<String, Permission> pair = (Map.Entry<String, Permission>) i.next();
			manager.removePermission(pair.getValue());
		}
		permissions.clear();
	}
	public boolean hasPermission(String permission) {
		return permissions.containsKey(permission);
	}
	
	public boolean playerHasPermission(Player player, String permission) {
		if (player == null || !permissions.containsKey(permission)) {
			return false;
		}
		return player.hasPermission(permissions.get(permission));
	}
	
	//private
	
}