package ninja.egg82.protocol.core;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.enums.BukkitInitType;
import ninja.egg82.plugin.reflection.entity.IEntityHelper;
import ninja.egg82.plugin.utils.BlockUtil;
import ninja.egg82.plugin.utils.CommandUtil;
import ninja.egg82.plugin.utils.LocationUtil;
import ninja.egg82.protocol.reflection.wrappers.entityLiving.IPacketEntityLivingHelper;
import ninja.egg82.startup.InitRegistry;

public class ProtocolLibFakeLivingEntity implements IFakeLivingEntity {
	//vars
	private IPacketEntityLivingHelper packetHelper = ServiceLocator.getService(IPacketEntityLivingHelper.class);
	private PacketContainer spawnPacket = null;
	private PacketContainer teleportSpawnPacket = null;
	private PacketContainer destroyPacket = null;
	private FieldAccessor nextEntityId = Accessors.getFieldAccessor(MinecraftReflection.getEntityClass(), "entityCount", true);
	
	private String gameVersion = ServiceLocator.getService(InitRegistry.class).getRegister(BukkitInitType.GAME_VERSION, String.class);
	private IEntityHelper entityHelper = ServiceLocator.getService(IEntityHelper.class);
	
	private static int currentEntityId = Integer.MAX_VALUE;
	
	private Location currentLocation = null;
	private int id = -1;
	private UUID uuid = UUID.randomUUID();
	
	private long lastAttackTime = -1L;
	private double health = 20.0d;
	
	private IObjectPool<UUID> players = new DynamicObjectPool<UUID>();
	
	//constructor
	public ProtocolLibFakeLivingEntity(Location loc, EntityType type) {
		if (loc == null) {
			throw new ArgumentNullException("loc");
		}
		if (type == null) {
			throw new ArgumentNullException("type");
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
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		
		UUID uuid = player.getUniqueId();
		
		if (!players.contains(uuid)) {
			players.add(uuid);
			packetHelper.send(spawnPacket, player);
			// Bit of a hack since the spawn packet for 1.8 seems to always spawn at 0,0,0 regardless of input
			if (gameVersion == "1.8" || gameVersion == "1.8.1" || gameVersion == "1.8.3" || gameVersion == "1.8.8") {
				packetHelper.send(teleportSpawnPacket, player);
			}
		}
	}
	public void removePlayer(Player player) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		
		UUID uuid = player.getUniqueId();
		
		if (players.contains(uuid)) {
			players.remove(uuid);
			packetHelper.send(destroyPacket, player);
		}
	}
	public void removeAllPlayers() {
		for (UUID uuid : players) {
			packetHelper.send(destroyPacket, CommandUtil.getPlayerByUuid(uuid));
		}
		players.clear();
	}
	
	public void damage(double damage) {
		health -= damage;
		if (health < 0.0d) {
			health = 0.0d;
		}
		
		PacketContainer packet = null;
		
		if (health > 0.0d) {
			packet = packetHelper.hurt(id);
		} else {
			packet = packetHelper.death(id);
		}
		
		for (UUID uuid : players) {
			packetHelper.send(packet, CommandUtil.getPlayerByUuid(uuid));
		}
	}
	
	public double getHealth() {
		return health;
	}
	public void setHealth(double health) {
		if (health < 0.0d) {
			health = 0.0d;
		}
		this.health = health;
		
		if (health == 0.0d) {
			PacketContainer deathPacket = packetHelper.death(id);
			
			for (UUID uuid : players) {
				packetHelper.send(deathPacket, CommandUtil.getPlayerByUuid(uuid));
			}
		}
	}
	
	public void lookTo(Location loc) {
		if (loc == null) {
			throw new ArgumentNullException("loc");
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
		
		for (UUID uuid : players) {
			packetHelper.send(lookPacket, CommandUtil.getPlayerByUuid(uuid));
			packetHelper.send(headLookPacket, CommandUtil.getPlayerByUuid(uuid));
		}
	}
	public void moveTo(Location loc) {
		if (loc == null) {
			throw new ArgumentNullException("loc");
		}
		
		PacketContainer movePacket = packetHelper.move(id, currentLocation, loc, (BlockUtil.getTopWalkableBlock(loc).getY() == loc.getY()) ? false : true);
		
		currentLocation = LocationUtil.makeEqualXYZ(loc, currentLocation);
		
		for (UUID uuid : players) {
			packetHelper.send(movePacket, CommandUtil.getPlayerByUuid(uuid));
		}
	}
	public void teleportTo(Location loc) {
		if (loc == null) {
			throw new ArgumentNullException("loc");
		}
		
		PacketContainer teleportPacket = packetHelper.teleport(id, loc, (BlockUtil.getTopWalkableBlock(loc).getY() == loc.getY()) ? false : true);
		
		currentLocation = loc.clone();
		
		for (UUID uuid : players) {
			packetHelper.send(teleportPacket, CommandUtil.getPlayerByUuid(uuid));
		}
	}
	public Location getLocation() {
		return currentLocation.clone();
	}
	
	public void animate(int animationId) {
		PacketContainer animatePacket = packetHelper.animate(id, animationId);
		for (UUID uuid : players) {
			packetHelper.send(animatePacket, CommandUtil.getPlayerByUuid(uuid));
		}
	}
	public void attack(Damageable entity, double damage) {
		if (entity == null) {
			throw new ArgumentNullException("entity");
		}
		
		long currentTime = System.currentTimeMillis();
		
		if (currentTime - lastAttackTime >= 1000L) {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
					return;
				}
			}
			
			entityHelper.damage(entity, DamageCause.ENTITY_ATTACK, 1.0d);
			Vector v = entity.getLocation().toVector().subtract(currentLocation.toVector()).normalize().setY(0.5d).multiply(0.5d);
			if (LocationUtil.isFinite(v)) {
				entity.setVelocity(v);
			}
			lastAttackTime = currentTime;
		}
	}
	
	public void collideF(Collection<IFakeLivingEntity> entities) {
		if (entities == null) {
			throw new ArgumentNullException("entities");
		}
		
		for (IFakeLivingEntity e : entities) {
			if (currentLocation.distanceSquared(e.getLocation()) < 0.5625d) { //0.75^2
				moveTo(currentLocation.clone().subtract(e.getLocation().toVector().subtract(currentLocation.toVector()).multiply(0.25d)));
				e.moveTo(e.getLocation().subtract(currentLocation.toVector().subtract(e.getLocation().toVector()).multiply(0.25d)));
			}
		}
	}
	public void collideE(Collection<Entity> entities) {
		if (entities == null) {
			throw new ArgumentNullException("entities");
		}
		
		for (Entity e : entities) {
			if (currentLocation.distanceSquared(e.getLocation()) < 0.5625d) { //0.75^2
				moveTo(currentLocation.clone().subtract(e.getLocation().toVector().subtract(currentLocation.toVector()).multiply(0.25d)));
				Vector v = currentLocation.toVector().subtract(e.getLocation().toVector()).multiply(0.25d);
				if (LocationUtil.isFinite(v)) {
					e.setVelocity(v);
				}
			}
		}
	}
	public void collide(IFakeLivingEntity entity) {
		if (entity == null) {
			throw new ArgumentNullException("entity");
		}
		
		if (currentLocation.distanceSquared(entity.getLocation()) < 0.5625d) { //0.75^2
			moveTo(currentLocation.clone().subtract(entity.getLocation().toVector().subtract(currentLocation.toVector()).multiply(0.25d)));
			entity.moveTo(entity.getLocation().subtract(currentLocation.toVector().subtract(entity.getLocation().toVector()).multiply(0.25d)));
		}
	}
	public void collide(Entity entity) {
		if (entity == null) {
			throw new ArgumentNullException("entity");
		}
		
		if (currentLocation.distanceSquared(entity.getLocation()) < 0.5625d) { //0.75^2
			moveTo(currentLocation.clone().subtract(entity.getLocation().toVector().subtract(currentLocation.toVector()).multiply(0.25d)));
			Vector v = currentLocation.toVector().subtract(entity.getLocation().toVector()).multiply(0.25d);
			if (LocationUtil.isFinite(v)) {
				entity.setVelocity(v);
			}
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
