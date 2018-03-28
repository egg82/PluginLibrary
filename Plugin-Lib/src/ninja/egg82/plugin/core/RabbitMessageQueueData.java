package ninja.egg82.plugin.core;

public class RabbitMessageQueueData {
	//vars
	private String queue = null;
	private String routingKey = null;
	private byte[] data = null;
	
	//constructor
	public RabbitMessageQueueData(String queue, String routingKey, byte[] data) {
		this.queue = queue;
		this.routingKey = routingKey;
		this.data = data;
	}
	
	//public
	public String getQueue() {
		return queue;
	}
	public String getRoutingKey() {
		return routingKey;
	}
	public byte[] getData() {
		return data;
	}
	
	//private
	
}
