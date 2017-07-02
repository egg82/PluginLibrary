package ninja.egg82.plugin.reflection.protocol;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class ProtocolLibFakeEntityHelper implements IFakeEntityHelper {
	//vars
	
	//constructor
	public ProtocolLibFakeEntityHelper() {
		
	}
	
	//public
	public IFakeLivingEntity createEntity(Location loc, EntityType type) {
		return new ProtocolLibFakeLivingEntity(loc, type);
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	
}
