package ninja.egg82.protocol.reflection;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.enums.SpigotInitType;
import ninja.egg82.plugin.utils.VersionUtil;
import ninja.egg82.protocol.core.IFakeLivingEntity;
import ninja.egg82.protocol.core.ProtocolLibFakeLivingEntity;
import ninja.egg82.startup.InitRegistry;

public class ProtocolLibFakeEntityHelper implements IFakeEntityHelper {
	//vars
	
	//constructor
	public ProtocolLibFakeEntityHelper() {
		String gameVersion = ServiceLocator.getService(InitRegistry.class).getRegister(SpigotInitType.GAME_VERSION, String.class);
		reflect(gameVersion, "ninja.egg82.protocol.reflection.wrappers.entityLiving");
	}
	
	//public
	public IFakeLivingEntity createEntity(Location loc, EntityType type) {
		return new ProtocolLibFakeLivingEntity(loc, type);
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	private void reflect(String version, String pkg) {
		reflect(version, pkg, true);
	}
	private void reflect(String version, String pkg, boolean lazyInitialize) {
		Class<?> bestMatch = VersionUtil.getBestMatch(version, pkg);
		
		if (bestMatch != null) {
			ServiceLocator.provideService(bestMatch, lazyInitialize);
		}
	}
}
