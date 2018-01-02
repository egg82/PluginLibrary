package ninja.egg82.permissions.reflection;

import java.util.List;

import org.bukkit.entity.Player;

public interface IPermissionsHelper {
	//functions
	boolean hasGroup(Player player, List<String> groups, boolean caseSensitive);
	List<String> getGroups(Player player);
	
	boolean isValidLibrary();
}
