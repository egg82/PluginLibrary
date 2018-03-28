package ninja.egg82.bungeecord.reflection.offlineplayer;

import java.net.InetAddress;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;

import ninja.egg82.exceptions.ArgumentNullException;

public class RedisBungeeHelper implements IRedisBungeeHelper {
	//vars
	private RedisBungeeAPI api = RedisBungee.getApi();
	
	//constructor
	public RedisBungeeHelper() {
		
	}
	
	//public
	public String getName(UUID playerUuid) {
		return getName(playerUuid, true);
	}
	public String getName(UUID playerUuid, boolean expensive) {
		if (playerUuid == null) {
			throw new ArgumentNullException("playerUuid");
		}
		
		return api.getNameFromUuid(playerUuid, expensive);
	}
	public UUID getUuid(String playerName) {
		return getUuid(playerName, true);
	}
	public UUID getUuid(String playerName, boolean expensive) {
		if (playerName == null) {
			throw new ArgumentNullException("playerName");
		}
		
		return api.getUuidFromName(playerName, expensive);
	}
	public InetAddress getIp(UUID playerUuid) {
		if (playerUuid == null) {
			throw new ArgumentNullException("playerUuid");
		}
		
		return api.getPlayerIp(playerUuid);
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	
}
