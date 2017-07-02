package ninja.egg82.plugin.reflection.protocol;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class NullFakeEntityHelper implements IFakeEntityHelper {
	//vars
	
	//constructor
	public NullFakeEntityHelper() {
		
	}
	
	//public
	public IFakeLivingEntity createEntity(Location loc, EntityType type) {
		return null;
	}
	
	public boolean isValidLibrary() {
		return false;
	}
	
	//private
	
}
