package ninja.egg82.bukkit.core.messaging;

public class BukkitMessageQueueData {
	//vars
	private String channel = null;
	private byte[] data = null;
	
	//constructor
	public BukkitMessageQueueData(String channel, byte[] data) {
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
