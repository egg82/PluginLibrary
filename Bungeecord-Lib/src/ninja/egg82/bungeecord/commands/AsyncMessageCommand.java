package ninja.egg82.bungeecord.commands;

import ninja.egg82.bungeecord.enums.SenderType;
import ninja.egg82.patterns.Command;

public abstract class AsyncMessageCommand extends Command {
	//vars
	protected String sender = null;
	protected SenderType senderType = null;
	protected String channelName = null;
	protected byte[] data = null;
	
	//constructor
	public AsyncMessageCommand() {
		super();
	}
	
	//public
	public final String getSender() {
		return sender;
	}
	public final void setSender(String sender) {
		this.sender = sender;
	}
	
	public final SenderType getSenderType() {
		return senderType;
	}
	public final void setSenderType(SenderType senderType) {
		this.senderType = senderType;
	}
	
	public final String getChannelName() {
		return channelName;
	}
	public final void setChannelName(String name) {
		this.channelName = name;
	}
	
	public final byte[] getData() {
		return data;
	}
	public final void setData(byte[] data) {
		this.data = data;
	}
	
	//private
	
}
