package ninja.egg82.plugin.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import gnu.trove.map.hash.THashMap;

public class PermissionsManager implements IPermissionsManager {
	//vars
	private THashMap<String, Permission> permissions = new THashMap<String, Permission>();
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
	
	public void addPermission(String permission) {
		try {
			manager.addPermission(permissions.computeIfAbsent(permission, (k) -> {
				return new Permission(permission);
			}));
		} catch (Exception ex) {
			
		}
	}
	public void removePermission(String permission) {
		permissions.computeIfPresent(permission, (k,v) -> {
			try {
				manager.removePermission(v);
			} catch (Exception ex) {
				
			}
			return null;
		});
	}
	
	public void clearPermissions() {
		Iterator<Entry<String, Permission>> i = permissions.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry<String, Permission> pair = (Map.Entry<String, Permission>) i.next();
			try {
				manager.removePermission(pair.getValue());
			} catch (Exception ex) {
				
			}
		}
		permissions.clear();
	}
	public boolean hasPermission(String permission) {
		return permissions.containsKey(permission);
	}
	
	public boolean playerHasPermission(Player player, String permission) {
		Permission p = permissions.get(permission);
		
		if (player == null || p == null) {
			return false;
		}
		return player.hasPermission(p);
	}
	
	//private
	
}