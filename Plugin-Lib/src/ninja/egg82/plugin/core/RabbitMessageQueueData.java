package ninja.egg82.plugin.core;

public class RabbitMessageQueueData {
	//vars
	private volatile String queue = null;
	private volatile String routingKey = null;
	private volatile byte[] data = null;
	
	//constructor
	public RabbitMessageQueueData() {
		
	}
	
	//public
	public String getQueue() {
		return queue;
	}
	public void setQueue(String queue) {
		this.queue = queue;
	}
	
	public String getRoutingKey() {
		return routingKey;
	}
	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public void clear() {
		this.queue = null;
		this.routingKey = null;
		this.data = null;
	}
	
	//private
	
}
