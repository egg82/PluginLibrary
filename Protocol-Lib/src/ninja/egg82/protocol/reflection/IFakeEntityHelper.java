package ninja.egg82.protocol.reflection;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import ninja.egg82.protocol.core.IFakeLivingEntity;

public interface IFakeEntityHelper {
	//functions
	IFakeLivingEntity createEntity(Location loc, EntityType type);
	
	boolean isValidLibrary();
}
