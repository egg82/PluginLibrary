package ninja.egg82.bungeecord.core;

public class BungeeMessageQueueData {
	//vars
	private String channel = null;
	private byte[] data = null;
	
	//constructor
	public BungeeMessageQueueData(String channel, byte[] data) {
		this.channel = channel;
		this.data = data;
	}
	
	//public
	public String getChannel() {
		return channel;
	}
	public byte[] getData() {
		return data;
	}
	
	//private
	
}
