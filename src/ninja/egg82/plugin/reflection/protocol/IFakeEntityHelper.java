package ninja.egg82.plugin.reflection.protocol;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public interface IFakeEntityHelper {
	//functions
	IFakeLivingEntity createEntity(Location loc, EntityType type);
	
	boolean isValidLibrary();
}
