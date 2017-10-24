package ninja.egg82.protocol.core;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IFakeEntity {
	//functions
	boolean addPlayer(Player player);
	boolean removePlayer(Player player);
	void removeAllPlayers();
	
	Location getLocation();
	
	int getId();
	UUID getUuid();
	
	void destroy();
}
