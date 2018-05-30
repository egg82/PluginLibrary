package ninja.egg82.permissions.reflection;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PEXPermissionsHelper implements IPermissionsHelper {
	//vars
	
	//constructor
	public PEXPermissionsHelper() {
		
	}
	
	//public
	public boolean hasGroup(UUID playerUuid, Collection<String> groups, boolean caseSensitive) {
		return hasGroup(playerUuid, groups, caseSensitive, false);
	}
	public boolean hasGroup(UUID playerUuid, Collection<String> groups, boolean caseSensitive, boolean expensive) {
		SortedSet<String> actualGroups = getGroups(playerUuid, expensive);
		
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
	public SortedSet<String> getGroups(UUID playerUuid) {
		return getGroups(playerUuid, false);
	}
	public SortedSet<String> getGroups(UUID playerUuid, boolean expensive) {
		if (playerUuid == null) {
			return new TreeSet<String>();
		}
		
		PermissionUser user = PermissionsEx.getUser(playerUuid.toString());
		return new TreeSet<String>(user.getParentIdentifiers());
	}
	
	public String getPrefix(UUID playerUuid) {
		return getPrefix(playerUuid, false);
	}
	public String getPrefix(UUID playerUuid, boolean expensive) {
		if (playerUuid == null) {
			return "";
		}
		
		PermissionUser user = PermissionsEx.getUser(playerUuid.toString());
		return user.getPrefix();
	}
	public String getSuffix(UUID playerUuid) {
		return getSuffix(playerUuid, false);
	}
	public String getSuffix(UUID playerUuid, boolean expensive) {
		if (playerUuid == null) {
			return "";
		}
		
		PermissionUser user = PermissionsEx.getUser(playerUuid.toString());
		return user.getSuffix();
	}
	
	public boolean hasPermission(UUID playerUuid, String permission) {
		return hasPermission(playerUuid, permission, false);
	}
	public boolean hasPermission(UUID playerUuid, String permission, boolean expensive) {
		if (playerUuid == null) {
			return false;
		}
		
		PermissionUser user = PermissionsEx.getUser(playerUuid.toString());
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
