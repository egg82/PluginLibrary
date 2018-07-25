package ninja.egg82.bukkit.core;

import java.util.UUID;

public class PlayerInfoContainer {
	//vars
	private String name = null;
	private UUID uuid = null;
	
	//constructor
	public PlayerInfoContainer(String name, UUID uuid) {
		this.name = name;
		this.uuid = uuid;
	}
	
	//public
	public String getName() {
		return name;
	}
	public UUID getUuid() {
		return uuid;
	}
	
	//private
	
}
