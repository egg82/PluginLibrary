package ninja.egg82.plugin.core.sender;

import java.util.UUID;

public interface Sender {
	//functions
	String getName();
	UUID getUuid();
	
	boolean isOpped();
	boolean isConsole();
	
	void sendMessage(String message);
	boolean hasPermission(String permission);
	
	Object getHandle();
}
