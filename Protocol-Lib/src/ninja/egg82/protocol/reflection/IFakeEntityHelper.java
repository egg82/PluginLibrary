package ninja.egg82.protocol.reflection;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import ninja.egg82.protocol.core.IFakeLivingEntity;

public interface IFakeEntityHelper {
	//functions
	IFakeLivingEntity createEntity(Location loc, EntityType type);
	IFakeLivingEntity toEntity(LivingEntity entity);
	
	boolean isValidLibrary();
}
