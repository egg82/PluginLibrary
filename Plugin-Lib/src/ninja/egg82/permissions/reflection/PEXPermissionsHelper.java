package ninja.egg82.permissions.reflection;

import java.util.List;

import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PEXPermissionsHelper implements IPermissionsHelper {
	//vars
	
	//constructor
	public PEXPermissionsHelper() {
		
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
		PermissionUser user = PermissionsEx.getUser(player);
		return user.getParentIdentifiers();
	}
	
	public String getPrefix(Player player) {
		PermissionUser user = PermissionsEx.getUser(player);
		return user.getPrefix();
	}
	public String getSuffix(Player player) {
		PermissionUser user = PermissionsEx.getUser(player);
		return user.getSuffix();
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	
}
