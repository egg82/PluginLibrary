package ninja.egg82.plugin.reflection.protocol;

import java.util.ArrayDeque;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;

import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.reflection.entity.IEntityHelper;
import ninja.egg82.plugin.reflection.protocol.wrappers.entityLiving.IPacketEntityLivingHelper;
import ninja.egg82.plugin.utils.BlockUtil;
import ninja.egg82.plugin.utils.CommandUtil;
import ninja.egg82.plugin.utils.LocationUtil;
import ninja.egg82.startup.InitRegistry;

public class ProtocolLibFakeLivingEntity implements IFakeLivingEntity {
	//vars
	private IPacketEntityLivingHelper packetHelper = (IPacketEntityLivingHelper) ServiceLocator.getService(IPacketEntityLivingHelper.class);
	private PacketContainer spawnPacket = null;
	private PacketContainer teleportSpawnPacket = null;
	private PacketContainer destroyPacket = null;
	private FieldAccessor nextEntityId = Accessors.getFieldAccessor(MinecraftReflection.getEntityClass(), "entityCount", true);
	
	private String gameVersion = (String) ((IRegistry) ServiceLocator.getService(InitRegistry.class)).getRegister("game.version");
	private IEntityHelper entityHelper = (IEntityHelper) ServiceLocator.getService(IEntityHelper.class);
	
	private static int currentEntityId = Integer.MAX_VALUE;
	
	private Location currentLocation = null;
	private int id = -1;
	private UUID uuid = UUID.randomUUID();
	
	private long lastAttackTime = -1L;
	
	private ArrayDeque<String> players = new ArrayDeque<String>();
	
	//constructor
	public ProtocolLibFakeLivingEntity(Location loc, EntityType type) {
		if (loc == null) {
			throw new RuntimeException("loc cannot be null.");
		}
		if (type == null) {
			throw new RuntimeException("type cannot be null.");
		}
		
		currentLocation = loc.clone();
		if (currentEntityId <= (Integer) nextEntityId.get(null)) {
			currentEntityId = Integer.MAX_VALUE;
		}
		
		id = currentEntityId;
		currentEntityId--;
		
		spawnPacket = packetHelper.spawn(id, uuid, type, loc);
		teleportSpawnPacket = packetHelper.teleport(id, loc, (BlockUtil.getTopWalkableBlock(loc).getY() == loc.getY()) ? false : true);
		destroyPacket = packetHelper.destroy(id);
	}
	
	//public
	public void addPlayer(Player player) {
		String uuid = player.getUniqueId().toString();
		
		if (!players.contains(uuid)) {
			packetHelper.send(spawnPacket, player);
			// Bit of a hack since the spawn packet for 1.8 seems to always spawn at 0,0,0 regardless of input
			if (gameVersion == "1.8" || gameVersion == "1.8.1" || gameVersion == "1.8.3" || gameVersion == "1.8.8") {
				packetHelper.send(teleportSpawnPacket, player);
			}
			players.add(uuid);
		}
	}
	public void removePlayer(Player player) {
		String uuid = player.getUniqueId().toString();
		
		if (players.contains(uuid)) {
			packetHelper.send(destroyPacket, player);
			players.remove(uuid);
		}
	}
	public void removeAllPlayers() {
		for (String uuid : players) {
			packetHelper.send(destroyPacket, CommandUtil.getPlayerByUuid(uuid));
		}
		players.clear();
	}
	
	public void lookTo(Location loc) {
		if (loc == null) {
			throw new RuntimeException("loc cannot be null.");
		}
		
		double dX = currentLocation.getX() - loc.getX();
		double dY = (currentLocation.getY() + 1.0d) - loc.getY();
		double dZ = currentLocation.getZ() - loc.getZ();
		float yaw = (float) (Math.toDegrees(Math.atan2(dZ, dX)) + 90.0d);
		float pitch = (float) (((Math.atan2(fastSqrt(dZ * dZ + dX * dX), dY) / Math.PI) - 0.5d) * -90.0d);
		
		PacketContainer lookPacket = packetHelper.look(id, yaw, pitch);
		PacketContainer headLookPacket = packetHelper.headRotation(id, yaw);
		
		currentLocation.setPitch(pitch);
		currentLocation.setYaw(yaw);
		
		for (String uuid : players) {
			packetHelper.send(lookPacket, CommandUtil.getPlayerByUuid(uuid));
			packetHelper.send(headLookPacket, CommandUtil.getPlayerByUuid(uuid));
		}
	}
	public void moveTo(Location loc) {
		if (loc == null) {
			throw new RuntimeException("loc cannot be null.");
		}
		
		PacketContainer movePacket = packetHelper.move(id, currentLocation, loc, (BlockUtil.getTopWalkableBlock(loc).getY() == loc.getY()) ? false : true);
		
		currentLocation = LocationUtil.makeEqualXYZ(loc, currentLocation);
		
		for (String uuid : players) {
			packetHelper.send(movePacket, CommandUtil.getPlayerByUuid(uuid));
		}
	}
	public void teleportTo(Location loc) {
		if (loc == null) {
			throw new RuntimeException("loc cannot be null.");
		}
		
		PacketContainer teleportPacket = packetHelper.teleport(id, loc, (BlockUtil.getTopWalkableBlock(loc).getY() == loc.getY()) ? false : true);
		
		currentLocation = loc.clone();
		
		for (String uuid : players) {
			packetHelper.send(teleportPacket, CommandUtil.getPlayerByUuid(uuid));
		}
	}
	public Location getLocation() {
		return currentLocation.clone();
	}
	
	public void animate(int animationId) {
		PacketContainer animatePacket = packetHelper.animate(id, animationId);
		for (String uuid : players) {
			packetHelper.send(animatePacket, CommandUtil.getPlayerByUuid(uuid));
		}
	}
	public void attack(Damageable entity, double damage) {
		long currentTime = System.currentTimeMillis();
		
		if (currentTime - lastAttackTime >= 1000L) {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
					return;
				}
			}
			
			entityHelper.damage(entity, DamageCause.ENTITY_ATTACK, 1.0d);
			entity.setVelocity(entity.getLocation().toVector().subtract(currentLocation.toVector()).normalize().setY(0.5d).multiply(0.5d));
			lastAttackTime = currentTime;
		}
	}
	
	public void collide(ArrayDeque<IFakeLivingEntity> entities) {
		for (IFakeLivingEntity e : entities) {
			if (currentLocation.distanceSquared(e.getLocation()) < 0.5625d) { //0.75^2
				moveTo(currentLocation.clone().subtract(e.getLocation().toVector().subtract(currentLocation.toVector()).multiply(0.25d)));
				e.moveTo(e.getLocation().subtract(currentLocation.toVector().subtract(e.getLocation().toVector()).multiply(0.25d)));
			}
		}
	}
	public void collide(IFakeLivingEntity entity) {
		if (currentLocation.distanceSquared(entity.getLocation()) < 0.5625d) { //0.75^2
			moveTo(currentLocation.clone().subtract(entity.getLocation().toVector().subtract(currentLocation.toVector()).multiply(0.25d)));
			entity.moveTo(entity.getLocation().subtract(currentLocation.toVector().subtract(entity.getLocation().toVector()).multiply(0.25d)));
		}
	}
	
	public int getId() {
		return id;
	}
	public UUID getUuid() {
		return uuid;
	}
	
	public void destroy() {
		removeAllPlayers();
	}
	
	//private
	private double fastSqrt(double in) {
		// Fast but inaccurate square root
		double retVal = Double.longBitsToDouble(((Double.doubleToLongBits(in) - (1L << 52)) >> 1) + (1L << 61));
		
		// Newton's method for improving accuracy at the cost of speed. 2 iterations will be slower than Math.sqrt()
		// So we only use 1 iteration
		retVal = (retVal + in / retVal) / 2.0d;
		
		return retVal;
	}
}
