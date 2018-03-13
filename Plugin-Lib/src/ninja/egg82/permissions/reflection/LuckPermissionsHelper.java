package ninja.egg82.permissions.reflection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;

public class LuckPermissionsHelper implements IPermissionsHelper {
	//vars
	LuckPermsApi api = null;
	
	//constructor
	public LuckPermissionsHelper() {
		
	}
	
	//public
	public boolean hasGroup(Player player, List<String> groups, boolean caseSensitive) {
		List<String> actualGroups = getGroups(player);
		
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
	public List<String> getGroups(Player player) {
		if (player == null) {
			return new ArrayList<String>();
		}
		
		if (api == null) {
			api = LuckPerms.getApi();
		}
		
		UUID uuid = player.getUniqueId();
		
		// Load a user even if offline
		//api.getStorage().loadUser(uuid);
		
		User user = api.getUser(uuid);
		
		if (user == null) {
			// No storage data for user, even offline
			return new ArrayList<String>();
		}
		
		// Shamelessly copied from LuckCommands ParentInfo.java
		List<Node> nodes = new ArrayList<Node>(user.getOwnNodes());
		nodes.removeIf(node -> !node.isGroupNode() || !node.getValuePrimitive());
		
		if (nodes.isEmpty()) {
			return new ArrayList<String>();
		}
		
		List<String> retVal = new ArrayList<String>();
		
		for (Node n : nodes) {
			retVal.add(n.getGroupName());
		}
		
		return retVal;
	}
	
	public String getPrefix(Player player) {
		if (player == null) {
			return "";
		}
		
		if (api == null) {
			api = LuckPerms.getApi();
		}
		
		UUID uuid = player.getUniqueId();
		
		//api.getStorage().loadUser(uuid);
		
		User user = api.getUser(uuid);
		
		if (user == null) {
			// No storage data for user, even offline
			return "";
		}
		
		UserData data = user.getCachedData();
		MetaData meta = data.getMetaData(api.getContextsForPlayer(player));
		return meta.getPrefix();
	}
	public String getSuffix(Player player) {
		if (player == null) {
			return "";
		}
		
		if (api == null) {
			api = LuckPerms.getApi();
		}
		
		UUID uuid = player.getUniqueId();
		
		//api.getStorage().loadUser(uuid);
		
		User user = api.getUser(uuid);
		
		if (user == null) {
			// No storage data for user, even offline
			return "";
		}
		
		UserData data = user.getCachedData();
		MetaData meta = data.getMetaData(api.getContextsForPlayer(player));
		return meta.getSuffix();
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	
}
