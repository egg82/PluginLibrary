package ninja.egg82.protocol.commands;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.Command;

public abstract class ProtocolEventCommand extends Command implements PacketListener {
	//vars
	private ListeningWhitelist sendingWhitelist = null;
	private ListeningWhitelist receivingWhitelist = null;
	
	private PacketType packetType = null;
	private ListenerPriority priority = null;
	protected PacketEvent event = null;
	
	private JavaPlugin plugin = ServiceLocator.getService(JavaPlugin.class);
	
	//constructor
	public ProtocolEventCommand(PacketType packetType, ListenerPriority priority) {
		super();
		this.packetType = packetType;
		this.priority = priority;
		
		ConnectionSide side = packetType.getSender().toSide();
		
		if (side.isForServer()) {
			sendingWhitelist = ListeningWhitelist.newBuilder()
				.priority(priority)
				.types(packetType)
				.gamePhase(GamePhase.PLAYING)
				.options(new ListenerOptions[0])
				.build();
		} else {
			sendingWhitelist = ListeningWhitelist.EMPTY_WHITELIST;
		}
		if (side.isForClient()) {
			receivingWhitelist = ListeningWhitelist.newBuilder()
				.priority(priority)
				.types(packetType)
				.gamePhase(GamePhase.PLAYING)
				.options(new ListenerOptions[0])
				.build();
		} else {
			receivingWhitelist = ListeningWhitelist.EMPTY_WHITELIST;
		}
	}
	
	//public
	public PacketType getPacketType() {
		return packetType;
	}
	public ListenerPriority getPriority() {
		return priority;
	}
	
	public void onPacketSending(PacketEvent event) {
		if (event.getPacketType() != packetType) {
			return;
		}
		
		this.event = event;
		start();
	}
	public void onPacketReceiving(PacketEvent event) {
		if (event.getPacketType() != packetType) {
			return;
		}
		
		this.event = event;
		start();
	}
	
	public ListeningWhitelist getSendingWhitelist() {
		return sendingWhitelist;
	}
	public ListeningWhitelist getReceivingWhitelist() {
		return receivingWhitelist;
	}
	public Plugin getPlugin() {
		return plugin;
	}
	
	//private
	
}
