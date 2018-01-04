package ninja.egg82.bungeecord.core;

import com.rabbitmq.client.AMQP.BasicProperties;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import ninja.egg82.bungeecord.BasePlugin;
import ninja.egg82.bungeecord.enums.SenderType;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.events.EventHandler;

public class MessageServer {
	//vars
	private Connection conn = null;
	
	private IObjectPool<String> channels = new DynamicObjectPool<String>();
	private volatile boolean valid = true;
	
	private EventHandler<MessageEventArgs> message = new EventHandler<MessageEventArgs>();
	
	private String personalId = ServiceLocator.getService(BasePlugin.class).getServerId();
	private BasicProperties props = null;
	private Channel channel = null;
	
	private String exchangeName = "ninja-egg82-plugin-broadcast";
	
	//constructor
	public MessageServer(String ip, int port) {
		if (ip == null) {
			valid = false;
			throw new ArgumentNullException("ip");
		}
		if (port <= 0 || port > 65535) {
			valid = false;
			throw new IllegalArgumentException("port cannot be <= 0 or > 65535");
		}
		
		props = new BasicProperties.Builder().replyTo(personalId).type(SenderType.BUNGEE.name()).deliveryMode(2).build();
		
		ConnectionFactory factory = new ConnectionFactory();
		
		factory.setHost(ip);
		factory.setPort(port);
		factory.setAutomaticRecoveryEnabled(true);
		
		try {
			// SSL with cert trust
			ConnectionFactory sslFactory = factory.clone();
			sslFactory.useSslProtocol("TLSv1.2");
			conn = sslFactory.newConnection();
			channel = conn.createChannel();
			channel.exchangeDeclare(exchangeName, "direct", true);
		} catch (Exception ex) {
			try {
				// SSL without cert trust
				ConnectionFactory sslFactory = factory.clone();
				sslFactory.useSslProtocol();
				conn = sslFactory.newConnection();
				channel = conn.createChannel();
				channel.exchangeDeclare(exchangeName, "direct", true);
			} catch (Exception ex2) {
				try {
					// Plaintext
					conn = factory.newConnection();
					channel = conn.createChannel();
					channel.exchangeDeclare(exchangeName, "direct", true);
				} catch (Exception ex3) {
					valid = false;
					ServiceLocator.getService(IExceptionHandler.class).silentException(ex3);
					throw new RuntimeException("Cannot create messaging channel.", ex3);
				}
			}
		}
	}
	public void finalize() {
		destroy();
	}
	
	//public
	public final EventHandler<MessageEventArgs> onMessage() {
		return message;
	}
	
	public String getPersonalId() {
		return personalId;
	}
	
	public void createChannel(String channelName) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		
		if (!valid) {
			throw new IllegalStateException("connection has been closed or was never able to be opened.");
		}
		
		if (channels.contains(channelName)) {
			return;
		}
		
		String queueName = personalId + "-" + channelName;
		
		try {
			channel.queueDeclareNoWait(queueName, true, false, false, null);
			// Create a queue that takes all bungee broadcasts
			channel.queueBind(queueName, exchangeName, channelName + "-bungee");
			// Create a queue specifically for this server
			channel.queueBind(queueName, exchangeName, channelName + "-" + personalId);
			channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) {
					message.invoke(this, new MessageEventArgs(properties.getReplyTo(), SenderType.valueOf(properties.getType()), channelName, body));
				}
			});
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw new RuntimeException("Cannot create channel.", ex);
		}
		
		channels.add(channelName);
	}
	public void destroyChannel(String channelName) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		
		if (!valid) {
			throw new IllegalStateException("connection has been closed or was never able to be opened.");
		}
		
		if (!channels.remove(channelName)) {
			return;
		}
		
		try {
			channel.queueDeleteNoWait(personalId + "-" + channelName, false, false);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw new RuntimeException("Cannot destroy channel.", ex);
		}
	}
	
	public void sendToServer(String channelName, String serverId, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (serverId == null) {
			throw new ArgumentNullException("serverId");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		
		if (!valid) {
			throw new IllegalStateException("connection has been closed or was never able to be opened.");
		}
		
		if (!channels.contains(channelName)) {
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		try {
			channel.basicPublish(exchangeName, channelName + "-" + serverId, props, data);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
		}
	}
	public void broadcastToBungee(String channelName, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		
		if (!valid) {
			throw new IllegalStateException("connection has been closed or was never able to be opened.");
		}
		
		if (!channels.contains(channelName)) {
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		try {
			channel.basicPublish(exchangeName, channelName + "-bungee", props, data);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
		}
	}
	public void broadcastToBukkit(String channelName, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			throw new ArgumentNullException("data");
		}
		
		if (!valid) {
			throw new IllegalStateException("connection has been closed or was never able to be opened.");
		}
		
		if (!channels.contains(channelName)) {
			throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
		}
		
		try {
			channel.basicPublish(exchangeName, channelName + "-bukkit", props, data);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
		}
	}
	
	public void destroy() {
		valid = false;
		
		clear();
		
		try {
			channel.exchangeDelete(exchangeName, true);
			channel.close();
			conn.close();
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
		}
	}
	public void clear() {
		for (String c : channels) {
			destroyChannel(c);
		}
	}
	
	//private
	
}
