package ninja.egg82.plugin.handlers;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public final class PermissionsManager {
	//vars
	private ConcurrentHashMap<String, Permission> permissions = new ConcurrentHashMap<String, Permission>();
	private PluginManager manager = Bukkit.getServer().getPluginManager();
	
	//constructor
	public PermissionsManager() {
		
	}
	
	//public
	public boolean addPermission(String permission) {
		if (permission == null) {
			throw new ArgumentNullException("permission");
		}
		
		permission = permission.toLowerCase();
		if (permissions.containsKey(permission)) {
			return false;
		}
		
		Permission p = new Permission(permission);
		p = CollectionUtil.putIfAbsent(permissions, permission, p);
		manager.addPermission(p);
		return true;
	}
	public boolean removePermission(String permission) {
		if (permission == null) {
			throw new ArgumentNullException("permission");
		}
		
		permission = permission.toLowerCase();
		Permission p = permissions.remove(permission);
		
		if (p != null) {
			manager.removePermission(p);
			return true;
		} else {
			return false;
		}
	}
	public boolean hasPermission(String permission) {
		if (permission == null) {
			return false;
		}
		
		permission = permission.toLowerCase();
		return permissions.containsKey(permission);
	}
	public void clear() {
		permissions.forEach((k, v) -> {
			manager.removePermission(v);
		});
		permissions.clear();
	}
	
	public int addPermissionsFromClass(Class<?> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		int numPermissions = 0;
		
		Object[] enums = ReflectUtil.getStaticFields(clazz);
		String[] permissions = Arrays.copyOf(enums, enums.length, String[].class);
		for (String p : permissions) {
			if (addPermission(p)) {
				numPermissions++;
			}
		}
		
		return numPermissions;
	}
	
	public boolean playerHasPermission(Player player, String permission) {
		if (player == null) {
			return false;
		}
		if (permission == null) {
			return false;
		}
		
		permission = permission.toLowerCase();
		Permission p = permissions.get(permission);
		
		if (p == null) {
			return player.hasPermission(permission);
		}
		return player.hasPermission(p);
	}
	
	//private
	
}