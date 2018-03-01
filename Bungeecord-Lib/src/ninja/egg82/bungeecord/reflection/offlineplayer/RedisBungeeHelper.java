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
		if (playerUuid == null) {
			throw new ArgumentNullException("playerUuid");
		}
		
		return api.getNameFromUuid(playerUuid);
	}
	public UUID getUuid(String playerName) {
		if (playerName == null) {
			throw new ArgumentNullException("playerName");
		}
		
		return api.getUuidFromName(playerName);
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
