package ninja.egg82.bukkit.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.EndGateway;
import org.bukkit.block.FlowerPot;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;

import ninja.egg82.bukkit.core.BlockData;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
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
			return new BlockData(null, null, null, LocationUtil.toBlockLocation(location));
		}
		
		return getBlock(location, location.getBlock().getState());
	}
	public static BlockData getBlock(Block block) {
		if (block == null) {
			throw new IllegalArgumentException("block cannot be null.");
		}
		
		return getBlock(block.getLocation(), block.getState());
	}
	public static BlockData getBlock(Location location, BlockState blockState) {
		if (location == null) {
			throw new IllegalArgumentException("location cannot be null.");
		}
		if (blockState == null) {
			throw new IllegalArgumentException("blockState cannot be null.");
		}
		
		location = LocationUtil.toBlockLocation(location);
		
		Material blockType = blockState.getType();
		
		if (blockState instanceof InventoryHolder) {
			return new BlockData(((InventoryHolder) blockState).getInventory().getContents(), blockState, blockType, location);
		} else if (blockType == Material.FLOWER_POT) {
			MaterialData currentItem = ((FlowerPot) blockState).getContents();
			return new BlockData((currentItem != null) ? new ItemStack[] {currentItem.toItemStack(1)} : null, blockState, blockType, location);
		} else if (blockType == Material.JUKEBOX) {
			Material currentItem = ((Jukebox) blockState).getPlaying();
			return new BlockData((currentItem != Material.AIR && currentItem != null) ? new ItemStack[] {new ItemStack(currentItem)} : null, blockState, blockType, location);
		}
		
		return new BlockData(null, blockState, blockType, location);
	}
	
	public static void setBlock(Block block, BlockData data, boolean updateBlock) {
		if (block == null) {
			throw new IllegalArgumentException("block cannot be null.");
		}
		if (data == null) {
			throw new IllegalArgumentException("data cannot be null.");
		}
		
		setBlock(block.getLocation(), data, updateBlock);
	}
	public static void setBlock(Location location, BlockData data, boolean updateBlock) {
		if (location == null) {
			throw new IllegalArgumentException("location cannot be null.");
		}
		if (data == null) {
			throw new IllegalArgumentException("data cannot be null.");
		}
		
		BlockState blockState = location.getBlock().getState();
		Material blockType = blockState.getType();
		
		clearInventory(blockState);
		blockState.setType(data.getMaterial());
		
		if (data.getState() != null) {
			try {
				setBlockData(blockState, data.getState());
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
				return;
			}
		}
		
		if (data.getInventory() != null) {
			if (blockState instanceof InventoryHolder) {
				((InventoryHolder) blockState).getInventory().setContents(data.getInventory());
			} else if (blockType == Material.FLOWER_POT) {
				((FlowerPot) blockState).setContents(data.getInventory()[0].getData());
			} else if (blockType == Material.JUKEBOX) {
				((Jukebox) blockState).setPlaying(data.getInventory()[0].getType());
			}
		}
		
		blockState.update(true, updateBlock);
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
	public static void setBlocks(List<BlockData> blocks, Location center, int xRadius, int yRadius, int zRadius, boolean updateBlocks) {
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
					setBlock(currentLocation, blocks.get(i), updateBlocks);
					i++;
				}
			}
		}
	}
	public static void clearBlocks(Location center, Material clearMaterial, int xRadius, int yRadius, int zRadius, boolean updateBlocks) {
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
					
					blockState.update(true, updateBlocks);
				}
			}
		}
	}
	
	public static void breakNaturally(BlockState state, Location location, GameMode gameMode, ItemStack tool, boolean updateBlock) {
		if (state == null) {
			throw new IllegalArgumentException("state cannot be null.");
		}
		if (location == null) {
			throw new IllegalArgumentException("location cannot be null.");
		}
		if (gameMode == null) {
			throw new IllegalArgumentException("gameMode cannot be null.");
		}
		
		Material blockType = state.getType();
		ItemStack[] items = null;
		
		if (state instanceof InventoryHolder) {
			items = ((InventoryHolder) state).getInventory().getContents();
		} else if (blockType == Material.FLOWER_POT) {
			MaterialData currentItem = ((FlowerPot) state).getContents();
			items = (currentItem != null) ? new ItemStack[] {currentItem.toItemStack(1)} : null;
		} else if (blockType == Material.JUKEBOX) {
			Material currentItem = ((Jukebox) state).getPlaying();
			items = (currentItem != Material.AIR && currentItem != null) ? new ItemStack[] {new ItemStack(currentItem)} : null;
		}
		
		if (gameMode == GameMode.CREATIVE) {
			World blockWorld = location.getWorld();
			
			if (items != null) {
				for (int i = 0; i < items.length; i++) {
					if (items[i] != null) {
						blockWorld.dropItemNaturally(location, items[i]);
					}
				}
			}
			
			setBlock(location, new BlockData(null, null, Material.AIR, null), updateBlock);
		} else {
			setBlock(location, new BlockData(items, state, blockType, null), updateBlock);
			location.getBlock().breakNaturally(tool);
		}
	}
	
	//private
	private static void clearInventory(BlockState block) {
		Material type = block.getType();
		
		if (block instanceof InventoryHolder) {
			InventoryHolder holder = (InventoryHolder) block;
			holder.getInventory().clear();
		}
		
		if (type == Material.FLOWER_POT) {
			((FlowerPot) block).setContents(null);
		} else if (type == Material.JUKEBOX) {
			((Jukebox) block).setPlaying(null);
		}
	}
	
	private static void setBlockData(BlockState block, BlockState data) {
		Material type = block.getType();
		String typeName = type.name();
		
		try {
			block.setData(data.getData());
		} catch (Exception ex) {
			
		}
		
		if (block instanceof InventoryHolder && data instanceof InventoryHolder) {
			((InventoryHolder) block).getInventory().setContents(((InventoryHolder) data).getInventory().getContents());
		}
		
		if ((type == Material.STANDING_BANNER || type == Material.WALL_BANNER) && block instanceof Banner) {
			Banner b1 = (Banner) block;
			Banner b2 = (Banner) data;
			b1.setBaseColor(b2.getBaseColor());
			b1.setPatterns(b2.getPatterns());
		} else if (type == Material.BEACON && block instanceof Beacon) {
			((Beacon) block).getInventory().setContents(((Beacon) data).getInventory().getContents());
		} else if (type == Material.BREWING_STAND && block instanceof BrewingStand) {
			BrewingStand b1 = (BrewingStand) block;
			BrewingStand b2 = (BrewingStand) data;
			b1.getInventory().setContents(b2.getInventory().getContents());
			b1.setBrewingTime(b2.getBrewingTime());
			b1.setFuelLevel(b2.getFuelLevel());
		} else if (type == Material.CHEST && block instanceof Chest) {
			((Chest) block).getBlockInventory().setContents(((Chest) data).getBlockInventory().getContents());
		} else if (type == Material.COMMAND && block instanceof CommandBlock) {
			CommandBlock b1 = (CommandBlock) block;
			CommandBlock b2 = (CommandBlock) data;
			b1.setName(b2.getName());
			b1.setCommand(b2.getCommand());
		} else if ((type == Material.FURNACE || type == Material.BURNING_FURNACE) && block instanceof Furnace) {
			Furnace b1 = (Furnace) block;
			Furnace b2 = (Furnace) data;
			b1.setBurnTime(b2.getBurnTime());
			b1.setCookTime(b2.getCookTime());
			b1.getInventory().setContents(b2.getInventory().getContents());
		} else if (type == Material.MOB_SPAWNER && block instanceof CreatureSpawner) {
			CreatureSpawner b1 = (CreatureSpawner) block;
			CreatureSpawner b2 = (CreatureSpawner) data;
			b1.setSpawnedType(b2.getSpawnedType());
			b1.setDelay(b2.getDelay());
			b1.setSpawnedType(b2.getSpawnedType());
		} else if (type == Material.DISPENSER && block instanceof Dispenser) {
			((Dispenser) block).getInventory().setContents(((Dispenser) data).getInventory().getContents());
		} else if (type == Material.DROPPER && block instanceof Dropper) {
			((Dropper) block).getInventory().setContents(((Dropper) data).getInventory().getContents());
		} else if (typeName.equals("END_GATEWAY") && block instanceof EndGateway) {
			EndGateway b1 = (EndGateway) block;
			EndGateway b2 = (EndGateway) data;
			b1.setExactTeleport(b2.isExactTeleport());
			b1.setExitLocation(b2.getExitLocation());
		} else if (type == Material.FLOWER_POT && block instanceof FlowerPot) {
			((FlowerPot) block).setContents(((FlowerPot) data).getContents());
		} else if (type == Material.HOPPER && block instanceof Hopper) {
			((Hopper) block).getInventory().setContents(((Hopper) data).getInventory().getContents());
		} else if (type == Material.JUKEBOX && block instanceof Jukebox) {
			((Jukebox) block).setPlaying(((Jukebox) data).getPlaying());
		} else if (type == Material.NOTE_BLOCK && block instanceof NoteBlock) {
			((NoteBlock) block).setNote(((NoteBlock) data).getNote());
		} else if ((type == Material.SIGN_POST || type == Material.WALL_SIGN) && block instanceof Sign) {
			Sign b1 = (Sign) block;
			String[] lines = ((Sign) data).getLines();
			for (int j = 0; j < lines.length; j++) {
				b1.setLine(j, lines[j]);
			}
		} else if (type == Material.SKULL && block instanceof Skull) {
			Skull b1 = (Skull) block;
			Skull b2 = (Skull) data;
			if (b2.getOwningPlayer() != null) {
				b1.setOwningPlayer(b2.getOwningPlayer());
			}
			b1.setRotation(b2.getRotation());
			b1.setSkullType(b2.getSkullType());
		} else if (type == Material.CROPS && block instanceof Crops) {
			Crops b1 = (Crops) block;
			Crops b2 = (Crops) data;
			b1.setState(b2.getState());
		}
	}
}