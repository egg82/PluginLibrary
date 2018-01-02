package ninja.egg82.permissions.reflection;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;

public class LuckPermissionsHelper implements IPermissionsHelper {
	//vars
	LuckPermsApi api = LuckPerms.getApi();
	
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
		User user = api.getUser(player.getUniqueId());
		
		// Shamelessly copied from LuckCommands ParentInfo.java
		List<Node> nodes = user.getOwnNodes();
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
	
	public boolean isValidLibrary() {
		return false;
	}
	
	//private
	
}
