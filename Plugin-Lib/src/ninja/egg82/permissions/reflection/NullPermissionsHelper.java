package ninja.egg82.permissions.reflection;

import java.util.List;

import org.bukkit.entity.Player;

public class NullPermissionsHelper implements IPermissionsHelper {
	//vars
	
	//constructor
	public NullPermissionsHelper() {
		
	}
	
	//public
	public boolean hasGroup(Player player, List<String> groups, boolean caseSensitive) {
		return false;
	}
	public List<String> getGroups(Player player) {
		return null;
	}
	
	public boolean isValidLibrary() {
		return false;
	}
	
	//private
	
}
