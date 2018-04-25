package ninja.egg82.permissions.reflection;

import java.util.Collection;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import ninja.egg82.patterns.tuples.pair.Boolean2Pair;

public class NullPermissionsHelper implements IPermissionsHelper {
	//vars
	
	//constructor
	public NullPermissionsHelper() {
		
	}
	
	//public
	public boolean hasGroup(Player player, Collection<String> groups, boolean caseSensitive) {
		return hasGroup(player, groups, caseSensitive, false);
	}
	public boolean hasGroup(OfflinePlayer player, Collection<String> groups, boolean caseSensitive, boolean expensive) {
		return false;
	}
	public Set<String> getGroups(Player player) {
		return getGroups(player, false);
	}
	public Set<String> getGroups(OfflinePlayer player, boolean expensive) {
		return null;
	}
	
	public String getPrefix(Player player) {
		return getPrefix(player, false);
	}
	public String getPrefix(OfflinePlayer player, boolean expensive) {
		return null;
	}
	public String getSuffix(Player player) {
		return getSuffix(player, false);
	}
	public String getSuffix(OfflinePlayer player, boolean expensive) {
		return null;
	}
	
	public Set<Boolean2Pair<String>> getPermissions(Player player) {
		return getPermissions(player, false);
	}
	public Set<Boolean2Pair<String>> getPermissions(OfflinePlayer player, boolean expensive) {
		return null;
	}
	public void setPermissions(OfflinePlayer player, Collection<Boolean2Pair<String>> permissions) {
		setPermissions(player, permissions, false);
	}
	public void setPermissions(OfflinePlayer player, Collection<Boolean2Pair<String>> permissions, boolean replaceTemp) {
		
	}
	
	public boolean isValidLibrary() {
		return false;
	}
	
	//private
	
}
