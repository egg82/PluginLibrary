package ninja.egg82.bungeecord.core;

import ninja.egg82.bungeecord.enums.SenderType;
import ninja.egg82.patterns.events.EventArgs;

public class MessageEventArgs extends EventArgs {
	//vars
	public static final MessageEventArgs EMPTY = new MessageEventArgs(null, null, null, null);
	
	private String sender = null;
	private SenderType senderType = null;
	private String channelName = null;
	private byte[] data = null;
	
	//constructor
	public MessageEventArgs(String sender, SenderType senderType, String channelName, byte[] data) {
		this.sender = sender;
		this.senderType = senderType;
		this.channelName = channelName;
		this.data = data;
	}
	
	//public
	public String getSender() {
		return sender;
	}
	public SenderType getSenderType() {
		return senderType;
	}
	public String getChannelName() {
		return channelName;
	}
	public byte[] getData() {
		return data;
	}
	
	//private
	
}
