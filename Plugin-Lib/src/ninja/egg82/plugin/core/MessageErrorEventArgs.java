package ninja.egg82.plugin.core;

import ninja.egg82.patterns.events.EventArgs;
import ninja.egg82.plugin.enums.RoutingType;

public class MessageErrorEventArgs extends EventArgs {
	//vars
	public static final MessageErrorEventArgs EMPTY = new MessageErrorEventArgs(null, null, null, null);
	
	private String channelName = null;
	private RoutingType routingType = null;
	private String serverId = null;
	private byte[] data = null;
	private Exception error = null;
	
	//constructor
	public MessageErrorEventArgs(String channelName, RoutingType routingType, byte[] data, Exception error) {
		this.channelName = channelName;
		this.routingType = routingType;
		this.data = data;
		this.error = error;
	}
	public MessageErrorEventArgs(String channelName, RoutingType routingType, String serverId, byte[] data, Exception error) {
		this.channelName = channelName;
		this.routingType = routingType;
		this.serverId = serverId;
		this.data = data;
		this.error = error;
	}
	
	//public
	public String getChannelName() {
		return channelName;
	}
	public RoutingType getRoutingType() {
		return routingType;
	}
	public String getServerId() {
		return serverId;
	}
	public byte[] getData() {
		return data;
	}
	public Exception getError() {
		return error;
	}
	
	//private
	
}
