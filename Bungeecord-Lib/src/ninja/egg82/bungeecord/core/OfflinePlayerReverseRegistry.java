package ninja.egg82.bungeecord.core;

import ninja.egg82.enums.ExpirationPolicy;
import ninja.egg82.patterns.ExpiringRegistry;

public class OfflinePlayerReverseRegistry extends ExpiringRegistry<String> {
	//vars
	
	//constructor
	public OfflinePlayerReverseRegistry() {
		super(String.class, 300000L, ExpirationPolicy.ACCESSED);
	}
	
	//public
	
	//private
	
}
