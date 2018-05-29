package ninja.egg82.permissions.reflection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import ninja.egg82.patterns.tuples.pair.Boolean2Pair;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PEXPermissionsHelper implements IPermissionsHelper {
	//vars
	
	//constructor
	public PEXPermissionsHelper() {
		
	}
	
	//public
	public boolean hasGroup(Player player, Collection<String> groups, boolean caseSensitive) {
		return hasGroup(player, groups, caseSensitive, false);
	}
	public boolean hasGroup(OfflinePlayer player, Collection<String> groups, boolean caseSensitive, boolean expensive) {
		Set<String> actualGroups = getGroups(player, expensive);
		
		if (caseSensitive) {
			for (String g : groups) {
				if (actualGroups.contains(g)) {
					return true;
				}
			}
		} else {
			for (String g : groups) {
				for (String g2 : actualGroups) {
					if (g2.equalsIgnoreCase(g)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	public Set<String> getGroups(Player player) {
		return getGroups(player, false);
	}
	public Set<String> getGroups(OfflinePlayer player, boolean expensive) {
		PermissionUser user = PermissionsEx.getUser(player.getUniqueId().toString());
		return new HashSet<String>(user.getParentIdentifiers());
	}
	
	public String getPrefix(Player player) {
		return getPrefix(player, false);
	}
	public String getPrefix(OfflinePlayer player, boolean expensive) {
		PermissionUser user = PermissionsEx.getUser(player.getUniqueId().toString());
		return user.getPrefix();
	}
	public String getSuffix(Player player) {
		return getSuffix(player, false);
	}
	public String getSuffix(OfflinePlayer player, boolean expensive) {
		PermissionUser user = PermissionsEx.getUser(player.getUniqueId().toString());
		return user.getSuffix();
	}
	
	public boolean hasPermission(Player player, String permission) {
		return hasPermission(player, permission, false);
	}
	public boolean hasPermission(OfflinePlayer player, String permission, boolean expensive) {
		PermissionUser user = PermissionsEx.getUser(player.getUniqueId().toString());
		return user.has(permission);
	}
	
	/*public Set<Boolean2Pair<String>> getPermissions(Player player) {
		return getPermissions(player, false);
	}
	public Set<Boolean2Pair<String>> getPermissions(OfflinePlayer player, boolean expensive) {
		
	}
	public void setPermissions(OfflinePlayer player, Collection<Boolean2Pair<String>> permissions) {
		setPermissions(player, permissions, false);
	}
	public void setPermissions(OfflinePlayer player, Collection<Boolean2Pair<String>> permissions, boolean replaceTemp) {
		
	}*/
	
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	
}
