package ninja.egg82.bungeecord.reflection.offlineplayer;

import java.net.InetAddress;
import java.util.UUID;

public interface IRedisBungeeHelper {
	//functions
	String getName(UUID playerUuid);
	UUID getUuid(String playerName);
	InetAddress getIp(UUID playerUuid);
	
	boolean isValidLibrary();
}
