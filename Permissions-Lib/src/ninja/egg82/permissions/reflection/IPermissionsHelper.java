package ninja.egg82.permissions.reflection;

import java.util.Collection;
import java.util.SortedSet;
import java.util.UUID;

public interface IPermissionsHelper {
	//functions
	boolean hasGroup(UUID playerUuid, Collection<String> groups, boolean caseSensitive);
	boolean hasGroup(UUID playerUuid, Collection<String> groups, boolean caseSensitive, boolean expensive);
	SortedSet<String> getGroups(UUID playerUuid);
	SortedSet<String> getGroups(UUID playerUuid, boolean expensive);
	
	String getPrefix(UUID playerUuid);
	String getPrefix(UUID playerUuid, boolean expensive);
	String getSuffix(UUID playerUuid);
	String getSuffix(UUID playerUuid, boolean expensive);
	
	boolean hasPermission(UUID playerUuid, String permission);
	boolean hasPermission(UUID playerUuid, String permission, boolean expensive);
	
	/*Set<Boolean2Pair<String>> getPermissions(UUID playerUuid);
	Set<Boolean2Pair<String>> getPermissions(UUID playerUuid, boolean expensive);
	void setTempPermissions(UUID playerUuid, Collection<Boolean2Pair<String>> permissions, long expireTime, TimeUnit expireTimeUnit);*/
	
	boolean isValidLibrary();
}
