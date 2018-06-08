package ninja.egg82.protocol.core;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;

import ninja.egg82.concurrent.DynamicConcurrentDeque;
import ninja.egg82.concurrent.IConcurrentDeque;
import ninja.egg82.protocol.utils.ProtocolReflectUtil;

public abstract class ProtocolLibFakeEntity implements IFakeEntity {
	//vars
	protected UUID uuid = null;
	protected int id = -1;
	
	protected PacketContainer spawnPacket = null;
	protected PacketContainer destroyPacket = null;
	
	protected volatile Location currentLocation = null;
	
	protected IConcurrentDeque<UUID> players = new DynamicConcurrentDeque<UUID>();
	
	private FieldAccessor nextEntityId = Accessors.getFieldAccessor(MinecraftReflection.getEntityClass(), "entityCount", true);
	private static int currentEntityId = Integer.MAX_VALUE;
	
	private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
	
	//constructor
	public ProtocolLibFakeEntity() {
		
	}
	
	//public
	public boolean addPlayer(Player player) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		
		UUID uuid = player.getUniqueId();
		
		if (!players.contains(uuid)) {
			players.add(uuid);
			if (spawnPacket != null) {
				ProtocolReflectUtil.sendPacket(protocolManager, spawnPacket, player);
			}
			return true;
		}
		return false;
	}
	public boolean removePlayer(Player player) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		
		UUID uuid = player.getUniqueId();
		
		if (players.remove(uuid)) {
			if (destroyPacket != null) {
				ProtocolReflectUtil.sendPacket(protocolManager, destroyPacket, player);
			}
			return true;
		}
		return false;
	}
	public void removeAllPlayers() {
		if (destroyPacket != null) {
			for (UUID uuid : players) {
				ProtocolReflectUtil.sendPacket(protocolManager, destroyPacket, Bukkit.getPlayer(uuid));
			}
		}
		players.clear();
	}
	
	public Location getLocation() {
		return currentLocation.clone();
	}
	
	public int getId() {
		return id;
	}
	public UUID getUuid() {
		return uuid;
	}
	
	//private
	protected int getNextEntityId() {
		if (currentEntityId <= ((Integer) nextEntityId.get(null)).intValue()) {
			currentEntityId = Integer.MAX_VALUE;
		}
		
		currentEntityId--;
		
		return currentEntityId + 1;
	}
}
