package ninja.egg82.protocol.reflection;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import ninja.egg82.protocol.core.IFakeLivingEntity;

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
