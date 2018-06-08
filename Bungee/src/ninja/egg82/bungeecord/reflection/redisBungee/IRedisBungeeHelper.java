package ninja.egg82.bungeecord.reflection.redisBungee;

import java.net.InetAddress;
import java.util.UUID;

public interface IRedisBungeeHelper {
	//functions
	String getName(UUID playerUuid);
	String getName(UUID playerUuid, boolean expensive);
	UUID getUuid(String playerName);
	UUID getUuid(String playerName, boolean expensive);
	InetAddress getIp(UUID playerUuid);
	
	boolean isValidLibrary();
}
