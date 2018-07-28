package ninja.egg82.bukkit.reflection.uuid;

import java.util.UUID;

import ninja.egg82.bukkit.core.PlayerInfoContainer;

public interface IUUIDHelper {
	//functions
	PlayerInfoContainer getPlayer(UUID playerUuid, boolean expensive);
	PlayerInfoContainer getPlayer(String playerName, boolean expensive);
}
