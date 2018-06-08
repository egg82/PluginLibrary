package ninja.egg82.bungeecord.core;

import java.io.Closeable;

import net.md_5.bungee.api.config.ServerInfo;
import ninja.egg82.concurrent.FixedConcurrentDeque;
import ninja.egg82.concurrent.IConcurrentDeque;

public class BungeeMessageSender implements Closeable {
	//vars
	
	// Server info/connection
	private ServerInfo info = null;
	
	// Message backlog/queue - for storing messages in case of disconnect. We put this here instead of in the wrappers so one server doesn't screw over another if it has no players
	private IConcurrentDeque<BungeeMessageQueueData> backlog = new FixedConcurrentDeque<BungeeMessageQueueData>(150);
	
	//constructor
	public BungeeMessageSender(ServerInfo info) {
		this.info = info;
	}
	
	//public
	public ServerInfo getInfo() {
		return info;
	}
	
	public void submit(String channelName, byte[] message) {
		// Grab a new data object
		BungeeMessageQueueData messageData = new BungeeMessageQueueData(channelName, message);
		// Add the new data to the send queue, tossing the oldest messages if needed
		while (!backlog.offerLast(messageData) && !backlog.isEmpty()) {
			backlog.pollFirst();
		}
	}
	
	public void sendAll() {
		while (!backlog.isEmpty()) {
			if (info.getPlayers().isEmpty()) {
				// No players on the server to send messages with. Break out of the loop
				break;
			}
			
			// Grab the oldest data first
			BungeeMessageQueueData first = backlog.pollFirst();
			if (first == null) {
				// Data is null, which means we prematurely reached the end of the queue
				break;
			}
			
			// Try to push the data to the Bungee queue
			try {
				info.sendData(first.getChannel(), first.getData());
			} catch (Exception ex) {
				// Message send failed. Re-add it to the front of the backlog
				backlog.addFirst(first);
			}
		}
	}
	
	public void close() {
		backlog.clear();
	}
	
	//private
	
}
