package ninja.egg82.permissions.reflection;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import ninja.egg82.patterns.tuples.pair.Boolean2Pair;

public interface IPermissionsHelper {
	//functions
	boolean hasGroup(Player player, Collection<String> groups, boolean caseSensitive);
	boolean hasGroup(OfflinePlayer player, Collection<String> groups, boolean caseSensitive, boolean expensive);
	Set<String> getGroups(Player player);
	Set<String> getGroups(OfflinePlayer player, boolean expensive);
	
	String getPrefix(Player player);
	String getPrefix(OfflinePlayer player, boolean expensive);
	String getSuffix(Player player);
	String getSuffix(OfflinePlayer player, boolean expensive);
	
	boolean hasPermission(Player player, String permission);
	boolean hasPermission(OfflinePlayer player, String permission, boolean expensive);
	
	/*Set<Boolean2Pair<String>> getPermissions(Player player);
	Set<Boolean2Pair<String>> getPermissions(OfflinePlayer player, boolean expensive);
	void setTempPermissions(OfflinePlayer player, Collection<Boolean2Pair<String>> permissions, long expireTime, TimeUnit expireTimeUnit);*/
	
	boolean isValidLibrary();
}
