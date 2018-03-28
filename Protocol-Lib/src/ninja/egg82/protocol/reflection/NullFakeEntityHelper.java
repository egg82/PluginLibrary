package ninja.egg82.protocol.reflection;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import ninja.egg82.protocol.core.IFakeLivingEntity;
import ninja.egg82.protocol.core.NullFakeLivingEntity;

public class NullFakeEntityHelper implements IFakeEntityHelper {
	//vars
	
	//constructor
	public NullFakeEntityHelper() {
		
	}
	
	//public
	public IFakeLivingEntity createEntity(Location loc, EntityType type) {
		return new NullFakeLivingEntity();
	}
	public IFakeLivingEntity toEntity(LivingEntity entity) {
		return new NullFakeLivingEntity();
	}
	
	public boolean isValidLibrary() {
		return false;
	}
	
	//private
	
}
