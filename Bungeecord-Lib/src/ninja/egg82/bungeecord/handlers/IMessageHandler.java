package ninja.egg82.bungeecord.handlers;

import ninja.egg82.bungeecord.commands.AsyncMessageCommand;
import ninja.egg82.bungeecord.enums.MessageHandlerType;

public interface IMessageHandler {
	//functions
	String getSenderId();
	void setSenderId(String senderId);
	
	void createChannel(String channelName);
	void destroyChannel(String channelName);
	
	void sendToServer(String serverId, String channelName, byte[] data);
	void broadcastToBungee(String channelName, byte[] data);
	void broadcastToBukkit(String channelName, byte[] data);
	
	int addMessagesFromPackage(String packageName);
	int addMessagesFromPackage(String packageName, boolean recursive);
	
	boolean addCommand(Class<? extends AsyncMessageCommand> clazz);
	boolean removeCommand(Class<? extends AsyncMessageCommand> clazz);
	
	void clearCommands();
	void clearChannels();
	void destroy();
	
	MessageHandlerType getType();
}
