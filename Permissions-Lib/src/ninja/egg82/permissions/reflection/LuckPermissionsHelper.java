package ninja.egg82.permissions.reflection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.PermissionData;
import me.lucko.luckperms.api.caching.UserData;

public class LuckPermissionsHelper implements IPermissionsHelper {
	//vars
	private LuckPermsApi api = null;
	
	//constructor
	public LuckPermissionsHelper() {
		
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
		
		if (api == null) {
			api = LuckPerms.getApi();
		}
		
		if (expensive && !api.isUserLoaded(playerUuid)) {
			// Load a user even if offline
			api.getUserManager().loadUser(playerUuid);
		}
		
		User user = api.getUser(playerUuid);
		
		if (user == null) {
			// No storage data for user, even offline
			return new TreeSet<String>();
		}
		
		List<Node> nodes = new ArrayList<Node>(user.getOwnNodes());
		for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
			Node n = i.next();
			if (!n.isGroupNode() || !n.getValue()) {
				i.remove();
			}
		}
		
		if (nodes.isEmpty()) {
			return new TreeSet<String>();
		}
		
		SortedSet<String> retVal = new TreeSet<String>();
		
		for (Node n : nodes) {
			retVal.add(n.getGroupName());
		}
		
		return retVal;
	}
	
	public String getPrefix(UUID playerUuid) {
		return getPrefix(playerUuid, false);
	}
	public String getPrefix(UUID playerUuid, boolean expensive) {
		if (playerUuid == null) {
			return "";
		}
		
		if (api == null) {
			api = LuckPerms.getApi();
		}
		
		if (expensive && !api.isUserLoaded(playerUuid)) {
			// Load a user even if offline
			api.getUserManager().loadUser(playerUuid);
		}
		
		User user = api.getUser(playerUuid);
		
		if (user == null) {
			// No storage data for user, even offline
			return "";
		}
		
		UserData data = user.getCachedData();
		Optional<Contexts> contexts = api.getContextForUser(user);
		MetaData meta = null;
		if (contexts.isPresent()) {
			meta = data.getMetaData(contexts.get());
		} else {
			meta = data.getMetaData(Contexts.global());
		}
		return meta.getPrefix();
	}
	public String getSuffix(UUID playerUuid) {
		return getSuffix(playerUuid, false);
	}
	public String getSuffix(UUID playerUuid, boolean expensive) {
		if (playerUuid == null) {
			return "";
		}
		
		if (api == null) {
			api = LuckPerms.getApi();
		}
		
		if (expensive && !api.isUserLoaded(playerUuid)) {
			// Load a user even if offline
			api.getUserManager().loadUser(playerUuid);
		}
		
		User user = api.getUser(playerUuid);
		
		if (user == null) {
			// No storage data for user, even offline
			return "";
		}
		
		UserData data = user.getCachedData();
		Optional<Contexts> contexts = api.getContextForUser(user);
		MetaData meta = null;
		if (contexts.isPresent()) {
			meta = data.getMetaData(contexts.get());
		} else {
			meta = data.getMetaData(Contexts.global());
		}
		return meta.getSuffix();
	}
	
	public boolean hasPermission(UUID playerUuid, String permission) {
		return hasPermission(playerUuid, permission, false);
	}
	public boolean hasPermission(UUID playerUuid, String permission, boolean expensive) {
		if (playerUuid == null) {
			return false;
		}
		
		if (api == null) {
			api = LuckPerms.getApi();
		}
		
		if (expensive && !api.isUserLoaded(playerUuid)) {
			// Load a user even if offline
			api.getUserManager().loadUser(playerUuid);
		}
		
		User user = api.getUser(playerUuid);
		
		if (user == null) {
			// No storage data for user, even offline
			return false;
		}
		
		UserData data = user.getCachedData();
		Optional<Contexts> contexts = api.getContextForUser(user);
		PermissionData perms = null;
		if (contexts.isPresent()) {
			perms = data.getPermissionData(contexts.get());
		} else {
			perms = data.getPermissionData(Contexts.global());
		}
		return perms.getPermissionValue(permission).asBoolean();
	}
	
	/*public Set<Boolean2Pair<String>> getPermissions(Player player) {
		return getPermissions(player, false);
	}
	public Set<Boolean2Pair<String>> getPermissions(OfflinePlayer player, boolean expensive) {
		if (player == null) {
			return new HashSet<Boolean2Pair<String>>();
		}
		
		if (api == null) {
			api = LuckPerms.getApi();
		}
		
		UUID uuid = player.getUniqueId();
		
		if (expensive && !player.isOnline()) {
			// Load a user even if offline
			api.getUserManager().loadUser(uuid);
		}
		
		User user = api.getUser(uuid);
		
		if (user == null) {
			// No storage data for user, even offline
			return new HashSet<Boolean2Pair<String>>();
		}
		
		Set<Boolean2Pair<String>> retVal = new HashSet<Boolean2Pair<String>>();
		for (Node n : user.getOwnNodes()) {
			if (n.isGroupNode() || n.isMeta() || n.isPrefix() || n.isSuffix()) {
				continue;
			}
			retVal.add(new Boolean2Pair<String>(n.getPermission(), n.getValuePrimitive()));
		}
		
		return retVal;
		
	}
	public void setPermissions(OfflinePlayer player, Collection<Boolean2Pair<String>> permissions) {
		if (player == null) {
			return;
		}
		
		if (api == null) {
			api = LuckPerms.getApi();
		}
		
		UUID uuid = player.getUniqueId();
		
		if (!player.isOnline()) {
			// Load a user even if offline
			api.getUserManager().loadUser(uuid);
		}
		
		User user = api.getUser(uuid);
		
		if (user == null) {
			// No storage data for user, even offline
			return;
		}
		
		if (replaceTemp) {
			user.clearNodes();
			for (Boolean2Pair<String> p : permissions) {
				user.setPermission(api.buildNode(p.getLeft()).setNegated(p.getRight()));
			}
		} else {
			List<Node> tempNodes = new ArrayList<Node>(user.);
			for (Iterator<Node> i = tempNodes.iterator(); i.hasNext();) {
				Node n = i.next();
				if (!n.isTemporary()) {
					i.remove();
				}
			}
			user.clearNodes();
		}
		
		try {
			user.refreshCachedData().get();
		} catch (Exception ex) {
			
		}
	}*/
	
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	
}
