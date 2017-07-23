package ninja.egg82.plugin.reflection.protocol;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import ninja.egg82.plugin.core.protocol.IFakeLivingEntity;
import ninja.egg82.plugin.core.protocol.ProtocolLibFakeLivingEntity;

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
