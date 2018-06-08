package ninja.egg82.protocol.reflection;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import ninja.egg82.bukkit.BasePlugin;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.protocol.core.IFakeLivingEntity;
import ninja.egg82.protocol.core.ProtocolLibFakeLivingEntity;
import ninja.egg82.protocol.utils.ProtocolReflectUtil;

public class ProtocolLibFakeEntityHelper implements IFakeEntityHelper {
	//vars
	
	//constructor
	public ProtocolLibFakeEntityHelper() {
		String gameVersion = ServiceLocator.getService(BasePlugin.class).getGameVersion();
		ProtocolReflectUtil.reflect(gameVersion, "ninja.egg82.protocol.reflection.wrappers.entityLiving");
	}
	
	//public
	public IFakeLivingEntity createEntity(Location loc, EntityType type) {
		return new ProtocolLibFakeLivingEntity(loc, type);
	}
	public IFakeLivingEntity toEntity(LivingEntity entity) {
		return new ProtocolLibFakeLivingEntity(entity);
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	
}
