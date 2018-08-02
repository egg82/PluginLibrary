package ninja.egg82.bukkit.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.FlowerPot;
import org.bukkit.block.Jukebox;
import org.bukkit.inventory.InventoryHolder;
import ninja.egg82.bukkit.core.BlockData;
import ninja.egg82.bukkit.reflection.block.serialization.ISerializationHelper;
import ninja.egg82.patterns.ServiceLocator;

public final class BlockUtil {
	//vars
	
	//constructor
	public BlockUtil() {
		
	}
	
	//public
	public static Location getNearestAirBlock(Location l, int maxRadius) {
		if (l == null) {
			throw new IllegalArgumentException("l cannot be null.");
		}
		if (maxRadius < 1) {
			maxRadius = 1;
		}
		
		if (!Bukkit.isPrimaryThread() && !l.getWorld().isChunkLoaded(l.getBlockX() >> 4, l.getBlockZ() >> 4)) {
			// If we're not on the server thread and the chunk isn't loaded, don't even bother trying
			return LocationUtil.toBlockLocation(l);
		}
		
		// We don't want to modify the original Location, and we want nice block locations
		l = LocationUtil.toBlockLocation(l);
		
		if (l.getBlock().getType() == Material.AIR) {
			// Got lucky, the current block is air
			return l;
		}
		
		Location closest = null;
		// Move in X or Z last
		for (int x = maxRadius * -1; x < maxRadius; x++) {
			// Move in X or Z second-to-last
			for (int z = maxRadius * -1; z < maxRadius; z++) {
				// Move in Y first
				for (int y = maxRadius * -1; y < maxRadius; y++) {
					// Need a new loc each time so we don't screw up the math
					Location l2 = l.clone().add(x, y, z);
					if (l2.getBlock().getType() == Material.AIR && (closest == null || l2.distanceSquared(l) < closest.distanceSquared(l))) {
						// Found an air block!
						closest = l2;
					}
				}
			}
		}
		
		return (closest != null) ? closest : l;
	}
	public static Location getLowestAirBlock(Location l) {
		if (l == null) {
			throw new IllegalArgumentException("l cannot be null.");
		}
		
		if (!Bukkit.isPrimaryThread() && !l.getWorld().isChunkLoaded(l.getBlockX() >> 4, l.getBlockZ() >> 4)) {
			// If we're not on the server thread and the chunk isn't loaded, don't even bother trying
			return LocationUtil.toBlockLocation(l);
		}
		
		// We don't want to modify the original Location, and we want nice block locations
		l = LocationUtil.toBlockLocation(l);
		
		if (l.getBlock().getType() == Material.AIR) {
			// The block is air, so we scan downwards to find the last air block
			// Stop at 0 so we don't get stuck in an infinite loop
			while (l.getY() > 0 && l.getBlock().getType() == Material.AIR) {
				// Apparently adding negatives is faster than subtracting (citation needed)
				l.add(0.0d, -1.0d, 0.0d);
			}
			// We don't care if 0 is the "lowest" air block because technically that's correct
			// If the block isn't air, add 1 to it
			return (l.getBlock().getType() == Material.AIR) ? l : l.add(0.0d, 1.0d, 0.0d);
		}
		
		// The block isn't air, so we need to scan upwards to find the first air block
		while (l.getY() < l.getWorld().getMaxHeight() && l.getBlock().getType() != Material.AIR) {
			l.add(0.0d, 1.0d, 0.0d);
		}
		// We don't care if maxHeight is the "lowest" air block because technically that's correct
		return l;
	}
	public static Location getHighestSolidBlock(Location l) {
		if (l == null) {
			throw new IllegalArgumentException("l cannot be null.");
		}
		
		if (!Bukkit.isPrimaryThread() && !l.getWorld().isChunkLoaded(l.getBlockX() >> 4, l.getBlockZ() >> 4)) {
			// If we're not on the server thread and the chunk isn't loaded, don't even bother trying
			return LocationUtil.toBlockLocation(l);
		}
		
		// We don't want to modify the original Location, and we want nice block locations
		l = LocationUtil.toBlockLocation(l);
		
		if (!l.getBlock().getType().isSolid()) {
			// The block isn't solid, so we scan downwards to find the last non-solid block
			// Stop at 0 so we don't get stuck in an infinite loop
			while (l.getY() > 0 && !l.getBlock().getType().isSolid()) {
				// Apparently adding negatives is faster than subtracting (citation needed)
				l.add(0.0d, -1.0d, 0.0d);
			}
			// We don't care if 0 is the "highest" solid block because technically that's correct
			return l;
		}
		
		// The block is solid, so we need to scan upwards to find the first non-solid block
		while (l.getY() < l.getWorld().getMaxHeight() && l.getBlock().getType().isSolid()) {
			l.add(0.0d, 1.0d, 0.0d);
		}
		// We don't care if maxHeight is the "highest" solid block because technically that's correct
		// If the block isn't solid, subtract 1 from it
		return (l.getBlock().getType().isSolid()) ? l : l.add(0.0d, -1.0d, 0.0d);
	}
	
	public static BlockData getBlock(Location location) {
		if (location == null) {
			throw new IllegalArgumentException("location cannot be null.");
		}
		
		if (!Bukkit.isPrimaryThread() && !location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
			return null;
		}
		
		return getBlock(location, location.getBlock().getState());
	}
	public static BlockData getBlock(Block block) {
		if (block == null) {
			throw new IllegalArgumentException("block cannot be null.");
		}
		
		return getBlock(block.getLocation(), block.getState());
	}
	@SuppressWarnings("deprecation")
	public static BlockData getBlock(Location location, BlockState blockState) {
		if (location == null) {
			throw new IllegalArgumentException("location cannot be null.");
		}
		if (blockState == null) {
			throw new IllegalArgumentException("blockState cannot be null.");
		}
		
		return new BlockData(LocationUtil.toBlockLocation(location), blockState.getType(), blockState.getRawData(), ServiceLocator.getService(ISerializationHelper.class).toCompressedBytes(blockState));
	}
	
	public static void setBlock(Block block, BlockData data, boolean updatePhysics) {
		if (block == null) {
			throw new IllegalArgumentException("block cannot be null.");
		}
		if (data == null) {
			throw new IllegalArgumentException("data cannot be null.");
		}
		
		setBlock(block.getLocation(), data, updatePhysics);
	}
	public static void setBlock(Location location, BlockData data, boolean updatePhysics) {
		if (location == null) {
			throw new IllegalArgumentException("location cannot be null.");
		}
		if (data == null) {
			throw new IllegalArgumentException("data cannot be null.");
		}
		
		ServiceLocator.getService(ISerializationHelper.class).fromCompressedBytes(location, data.getType(), data.getBlockData(), data.getCompressedData(), updatePhysics);
	}
	
	public static List<BlockData> getBlocks(Location center, int xRadius, int yRadius, int zRadius) {
		if (center == null) {
			throw new IllegalArgumentException("center cannot be null.");
		}
		
		int minX = center.getBlockX() - xRadius;
		int maxX = center.getBlockX() + xRadius;
		int minY = center.getBlockY() - yRadius;
		int maxY = center.getBlockY() + yRadius;
		int minZ = center.getBlockZ() - zRadius;
		int maxZ = center.getBlockZ() + zRadius;
		
		Location currentLocation = new Location(center.getWorld(), 0.0d, 0.0d, 0.0d);
		ArrayList<BlockData> blocks = new ArrayList<BlockData>();
		
		for (int x = minX; x <= maxX; x++) {
			currentLocation.setX(x);
			for (int z = minZ; z <= maxZ; z++) {
				currentLocation.setZ(z);
				for (int y = minY; y <= maxY; y++) {
					currentLocation.setY(y);
					blocks.add(getBlock(currentLocation));
				}
			}
		}
		
		return blocks;
	}
	public static void setBlocks(List<BlockData> blocks, Location center, int xRadius, int yRadius, int zRadius, boolean updatePhysics) {
		if (blocks == null) {
			throw new IllegalArgumentException("blocks cannot be null.");
		}
		if (blocks.size() != (xRadius * 2 + 1) * (yRadius * 2 + 1) * (zRadius * 2 + 1)) {
			throw new RuntimeException("blocks is not the correct length for the radii specified. Expected " + ((xRadius * 2 + 1) * (yRadius * 2 + 1) * (zRadius * 2 + 1)) + ", got " + blocks.size() + ".");
		}
		if (center == null) {
			throw new IllegalArgumentException("center cannot be null.");
		}
		
		int minX = center.getBlockX() - xRadius;
		int maxX = center.getBlockX() + xRadius;
		int minY = center.getBlockY() - yRadius;
		int maxY = center.getBlockY() + yRadius;
		int minZ = center.getBlockZ() - zRadius;
		int maxZ = center.getBlockZ() + zRadius;
		
		Location currentLocation = new Location(center.getWorld(), 0.0d, 0.0d, 0.0d);
		
		int i = 0;
		
		for (int x = minX; x <= maxX; x++) {
			currentLocation.setX(x);
			for (int z = minZ; z <= maxZ; z++) {
				currentLocation.setZ(z);
				for (int y = minY; y <= maxY; y++) {
					currentLocation.setY(y);
					setBlock(currentLocation, blocks.get(i), updatePhysics);
					i++;
				}
			}
		}
	}
	public static void clearBlock(Location location, Material clearMaterial, boolean updatePhysics) {
		if (location == null) {
			throw new IllegalArgumentException("location cannot be null.");
		}
		
		clearBlock(location.getBlock(), clearMaterial, updatePhysics);
	}
	public static void clearBlock(Block block, Material clearMaterial, boolean updatePhysics) {
		if (block == null) {
			throw new IllegalArgumentException("block cannot be null.");
		}
		if (clearMaterial == null) {
			clearMaterial = Material.AIR;
		}
		
		BlockState state = block.getState();
		clearInventory(state);
		state.setType(clearMaterial);
		state.update(true, updatePhysics);
	}
	public static void clearBlocks(Location center, Material clearMaterial, int xRadius, int yRadius, int zRadius, boolean updatePhysics) {
		if (center == null) {
			throw new IllegalArgumentException("center cannot be null.");
		}
		if (clearMaterial == null) {
			clearMaterial = Material.AIR;
		}
		
		int minX = center.getBlockX() - xRadius;
		int maxX = center.getBlockX() + xRadius;
		int minY = center.getBlockY() - yRadius;
		int maxY = center.getBlockY() + yRadius;
		int minZ = center.getBlockZ() - zRadius;
		int maxZ = center.getBlockZ() + zRadius;
		
		Location currentLocation = new Location(center.getWorld(), 0.0d, 0.0d, 0.0d);
		
		BlockState blockState = null;
		
		for (int x = minX; x <= maxX; x++) {
			currentLocation.setX(x);
			for (int z = minZ; z <= maxZ; z++) {
				currentLocation.setZ(z);
				for (int y = minY; y <= maxY; y++) {
					currentLocation.setY(y);
					
					blockState = currentLocation.getBlock().getState();
					
					clearInventory(blockState);
					blockState.setType(clearMaterial);
					
					blockState.update(true, updatePhysics);
				}
			}
		}
	}
	
	//private
	private static void clearInventory(BlockState state) {
		if (state instanceof InventoryHolder) {
			InventoryHolder holder = (InventoryHolder) state;
			holder.getInventory().clear();
		}
		
		if (state instanceof FlowerPot) {
			((FlowerPot) state).setContents(null);
		} else if (state instanceof Jukebox) {
			((Jukebox) state).setPlaying(null);
		}
	}
}