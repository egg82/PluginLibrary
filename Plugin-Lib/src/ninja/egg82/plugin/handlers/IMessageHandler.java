package ninja.egg82.plugin.handlers;

import ninja.egg82.plugin.commands.MessageCommand;
import ninja.egg82.plugin.enums.MessageHandlerType;

public interface IMessageHandler {
	//functions
	String getSenderId();
	
	void createChannel(String channelName);
	void destroyChannel(String channelName);
	
	void sendToServer(String serverId, String channelName, byte[] data);
	void broadcastToBungee(String channelName, byte[] data);
	void broadcastToBukkit(String channelName, byte[] data);
	
	int addMessagesFromPackage(String packageName);
	int addMessagesFromPackage(String packageName, boolean recursive);
	
	boolean addCommand(Class<MessageCommand> clazz);
	boolean removeCommand(Class<MessageCommand> clazz);
	
	void clearCommands();
	void clearChannels();
	void destroy();
	
	MessageHandlerType getType();
}
