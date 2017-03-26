package ninja.egg82.plugin.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.handlers.PermissionsManager;

public class CommandUtil {
	//vars
	private static PermissionsManager permissionsManager = null;
	
	//constructor
	public CommandUtil() {
		
	}
	
	//public
	public static boolean isPlayer(CommandSender sender) {
		return (sender instanceof Player) ? true : false;
	}
	public static boolean hasPermission(CommandSender sender, String permission) {
		if (!isPlayer(sender)) {
			return true;
		}
		
		if (permissionsManager == null) {
			permissionsManager = (PermissionsManager) ServiceLocator.getService(PermissionsManager.class);
		}
		
		return permissionsManager.playerHasPermission((Player) sender, permission);
	}
	
	public static boolean isArrayOfAllowedLength(Object[] arr, int... allowedLengths) {
		for (int i = 0; i < allowedLengths.length; i++) {
			if (arr.length == allowedLengths[i]) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Player getPlayerByName(String name) {
		return getPlayerByName(name, true);
	}
	public static Player getPlayerByName(String name, boolean exact) {
		Player p = Bukkit.getPlayerExact(name);
		if (p == null && !exact) {
			p = Bukkit.getPlayer(name);
		}
		return p;
	}
	public static Player getPlayerByUuid(UUID uuid) {
		return Bukkit.getPlayer(uuid);
	}
	public static Player getPlayerByUuid(String uuid) {
		return Bukkit.getPlayer(UUID.fromString(uuid));
	}
	
	//private
	
}
