package ninja.egg82.protocol.core;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.plugin.utils.CommandUtil;
import ninja.egg82.protocol.utils.ProtocolReflectUtil;

public abstract class ProtocolLibFakeEntity implements IFakeEntity {
	//vars
	protected UUID uuid = null;
	protected int id = -1;
	
	protected PacketContainer spawnPacket = null;
	protected PacketContainer destroyPacket = null;
	
	protected volatile Location currentLocation = null;
	
	protected IObjectPool<UUID> players = new DynamicObjectPool<UUID>();
	
	private FieldAccessor nextEntityId = Accessors.getFieldAccessor(MinecraftReflection.getEntityClass(), "entityCount", true);
	private static int currentEntityId = Integer.MAX_VALUE;
	
	//constructor
	public ProtocolLibFakeEntity() {
		
	}
	
	//public
	public boolean addPlayer(Player player) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		
		UUID uuid = player.getUniqueId();
		
		if (!players.contains(uuid)) {
			players.add(uuid);
			if (spawnPacket != null) {
				ProtocolReflectUtil.sendPacket(spawnPacket, player);
			}
			return true;
		}
		return false;
	}
	public boolean removePlayer(Player player) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		
		UUID uuid = player.getUniqueId();
		
		if (players.remove(uuid)) {
			if (destroyPacket != null) {
				ProtocolReflectUtil.sendPacket(destroyPacket, player);
			}
			return true;
		}
		return false;
	}
	public void removeAllPlayers() {
		if (destroyPacket != null) {
			for (UUID uuid : players) {
				ProtocolReflectUtil.sendPacket(destroyPacket, CommandUtil.getPlayerByUuid(uuid));
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
		if (currentEntityId <= (Integer) nextEntityId.get(null)) {
			currentEntityId = Integer.MAX_VALUE;
		}
		
		currentEntityId--;
		
		return currentEntityId + 1;
	}
}
