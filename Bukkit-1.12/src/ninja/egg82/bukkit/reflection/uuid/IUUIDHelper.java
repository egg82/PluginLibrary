package ninja.egg82.bukkit.reflection.uuid;

import java.util.UUID;

import ninja.egg82.bukkit.core.PlayerInfoContainer;

public interface IUUIDHelper {
	//functions
	PlayerInfoContainer getPlayer(UUID uuid);
	boolean isCached(UUID uuid);
	PlayerInfoContainer getPlayer(String name);
	boolean isCached(String name);
}
