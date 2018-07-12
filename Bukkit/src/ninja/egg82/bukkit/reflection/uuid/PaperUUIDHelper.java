package ninja.egg82.bukkit.reflection.uuid;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;

import com.destroystokyo.paper.profile.PlayerProfile;

import ninja.egg82.bukkit.core.PlayerInfoContainer;
import ninja.egg82.enums.ExpirationPolicy;
import ninja.egg82.patterns.registries.ExpiringRegistry;
import ninja.egg82.patterns.registries.IRegistry;

public class PaperUUIDHelper implements IUUIDHelper {
	//vars
	private IRegistry<UUID, PlayerInfoContainer> uuidRegistry = new ExpiringRegistry<UUID, PlayerInfoContainer>(UUID.class, PlayerInfoContainer.class, 60L * 60L * 1000L, TimeUnit.MILLISECONDS, ExpirationPolicy.ACCESSED);
	private IRegistry<String, PlayerInfoContainer> nameRegistry = new ExpiringRegistry<String, PlayerInfoContainer>(String.class, PlayerInfoContainer.class, 60L * 60L * 1000L, TimeUnit.MILLISECONDS, ExpirationPolicy.ACCESSED);
	
	//constructor
	public PaperUUIDHelper() {
		
	}
	
	//public
	public PlayerInfoContainer getPlayer(UUID uuid) {
		PlayerInfoContainer info = uuidRegistry.getRegister(uuid);
		if (info == null) {
			PlayerProfile profile = Bukkit.createProfile(uuid);
			if (profile.complete(false)) {
				info = new PlayerInfoContainer(profile.getName(), profile.getId());
				uuidRegistry.setRegister(uuid, info);
			}
		}
		return info;
	}
	public boolean isCached(UUID uuid) {
		if (uuidRegistry.hasRegister(uuid)) {
			return true;
		}
		
		PlayerProfile profile = Bukkit.createProfile(uuid);
		return profile.isComplete();
	}
	public PlayerInfoContainer getPlayer(String name) {
		PlayerInfoContainer info = nameRegistry.getRegister(name);
		if (info == null) {
			PlayerProfile profile = Bukkit.createProfile(name);
			if (profile.complete(false)) {
				info = new PlayerInfoContainer(profile.getName(), profile.getId());
				nameRegistry.setRegister(name, info);
			}
		}
		return info;
	}
	public boolean isCached(String name) {
		if (nameRegistry.hasRegister(name)) {
			return true;
		}
		
		PlayerProfile profile = Bukkit.createProfile(name);
		return profile.isComplete();
	}
	
	//private
	
}
