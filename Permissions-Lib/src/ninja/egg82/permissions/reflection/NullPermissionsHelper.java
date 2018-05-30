package ninja.egg82.permissions.reflection;

import java.util.Collection;
import java.util.SortedSet;
import java.util.UUID;

public class NullPermissionsHelper implements IPermissionsHelper {
	//vars
	
	//constructor
	public NullPermissionsHelper() {
		
	}
	
	//public
	public boolean hasGroup(UUID playerUuid, Collection<String> groups, boolean caseSensitive) {
		return hasGroup(playerUuid, groups, caseSensitive, false);
	}
	public boolean hasGroup(UUID playerUuid, Collection<String> groups, boolean caseSensitive, boolean expensive) {
		return false;
	}
	public SortedSet<String> getGroups(UUID playerUuid) {
		return getGroups(playerUuid, false);
	}
	public SortedSet<String> getGroups(UUID playerUuid, boolean expensive) {
		return null;
	}
	
	public String getPrefix(UUID playerUuid) {
		return getPrefix(playerUuid, false);
	}
	public String getPrefix(UUID playerUuid, boolean expensive) {
		return null;
	}
	public String getSuffix(UUID playerUuid) {
		return getSuffix(playerUuid, false);
	}
	public String getSuffix(UUID playerUuid, boolean expensive) {
		return null;
	}
	
	public boolean hasPermission(UUID playerUuid, String permission) {
		return hasPermission(playerUuid, permission, false);
	}
	public boolean hasPermission(UUID playerUuid, String permission, boolean expensive) {
		return false;
	}
	
	/*public Set<Boolean2Pair<String>> getPermissions(Player player) {
		return getPermissions(player, false);
	}
	public Set<Boolean2Pair<String>> getPermissions(OfflinePlayer player, boolean expensive) {
		return null;
	}
	public void setPermissions(OfflinePlayer player, Collection<Boolean2Pair<String>> permissions) {
		setPermissions(player, permissions, false);
	}
	public void setPermissions(OfflinePlayer player, Collection<Boolean2Pair<String>> permissions, boolean replaceTemp) {
		
	}*/
	
	public boolean isValidLibrary() {
		return false;
	}
	
	//private
	
}
