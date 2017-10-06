package ninja.egg82.bungeecord.core;

import java.util.UUID;

import ninja.egg82.enums.ExpirationPolicy;
import ninja.egg82.patterns.ExpiringRegistry;

public class OfflinePlayerRegistry extends ExpiringRegistry<UUID> {
	//vars
	
	//constructor
	public OfflinePlayerRegistry() {
		super(UUID.class, 300000L, ExpirationPolicy.ACCESSED);
	}
	
	//public
	
	//private
	
}
