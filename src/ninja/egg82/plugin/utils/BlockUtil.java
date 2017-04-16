package ninja.egg82.plugin.utils;

import java.util.ArrayList;
import java.util.List;

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
import org.bukkit.material.MaterialData;

import ninja.egg82.plugin.core.BlockData;

public final class BlockUtil {
	//vars
	
	//constructor
	public BlockUtil() {
		
	}
	
	//public
	public static Location getTopAirBlock(Location l) {
		if (l == null) {
			throw new IllegalArgumentException("l cannot be null.");
		}
		
		l = l.clone();
		
		do {
			while (l.getBlock().getType() != Material.AIR) {
				l = l.add(0.0d, 1.0d, 0.0d);
			}
			while (l.add(0.0d, 1.0d, 0.0d).getBlock().getType() != Material.AIR) {
				
			} 
			l.subtract(0.0d, 1.0d, 0.0d);
		} while (l.getBlock().getType() != Material.AIR || l.add(0.0d, 1.0d, 0.0d).getBlock().getType() != Material.AIR);
		
		l.subtract(0.0d, 1.0d, 0.0d);
		
		while (l.subtract(0.0d, 1.0d, 0.0d).getBlock().getType() == Material.AIR) {
			
		}
		
		l.add(0.0d, 1.0d, 0.0d);
		
		return l;
	}
	
	public static BlockData getBlock(Location location) {
		if (location == null) {
			throw new IllegalArgumentException("location cannot be null.");
		}
		
		return getBlock(location.getBlock());
	}
	public static BlockData getBlock(Block block) {
		if (block == null) {
			throw new IllegalArgumentException("block cannot be null.");
		}
		
		return getBlock(block.getState());
	}
	public static BlockData getBlock(BlockState blockState) {
		if (blockState == null) {
			throw new IllegalArgumentException("blockState cannot be null.");
		}
		
		Material blockType = blockState.getType();
		
		if (blockState instanceof InventoryHolder) {
			return new BlockData(((InventoryHolder) blockState).getInventory().getContents(), blockState, blockType);
		} else if (blockType == Material.FLOWER_POT) {
			MaterialData currentItem = ((FlowerPot) blockState).getContents();
			return new BlockData((currentItem != null) ? new ItemStack[] {currentItem.toItemStack()} : null, blockState, blockType);
		} else if (blockType == Material.JUKEBOX) {
			Material currentItem = ((Jukebox) blockState).getPlaying();
			return new BlockData((currentItem != Material.AIR && currentItem != null) ? new ItemStack[] {new ItemStack(currentItem)} : null, blockState, blockType);
		}
		
		return new BlockData(null, blockState, blockType);
	}
	
	public static void setBlock(Block block, BlockData data) {
		if (block == null) {
			throw new IllegalArgumentException("block cannot be null.");
		}
		
		setBlock(block.getLocation(), data);
	}
	public static void setBlock(Location location, BlockData data) {
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
			setBlockData(blockState, data.getState());
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
		
		blockState.update(true, true);
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
	public static void setBlocks(List<BlockData> blocks, Location center, int xRadius, int yRadius, int zRadius) {
		if (blocks == null) {
			throw new IllegalArgumentException("blocks cannot be null.");
		}
		if (blocks.size() != (xRadius * 2 + 1) * (yRadius * 2 + 1) * (zRadius * 2 + 1)) {
			throw new RuntimeException("blocks is not the correct length.");
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
					setBlock(currentLocation, blocks.get(i));
					i++;
				}
			}
		}
	}
	public static void clearBlocks(Location center, Material clearMaterial, int xRadius, int yRadius, int zRadius) {
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
					
					blockState.update(true, true);
				}
			}
		}
	}
	
	public static void breakNaturally(BlockState state, Location location, GameMode gameMode, ItemStack tool) {
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
			items = (currentItem != null) ? new ItemStack[] {currentItem.toItemStack()} : null;
		} else if (blockType == Material.JUKEBOX) {
			Material currentItem = ((Jukebox) state).getPlaying();
			items = (currentItem != Material.AIR && currentItem != null) ? new ItemStack[] {new ItemStack(currentItem)} : null;
		}
		
		if (gameMode == GameMode.CREATIVE) {
			World blockWorld = location.getWorld();
			
			if (items != null) {
				for (int i = 0; i < items.length; i++) {
					blockWorld.dropItemNaturally(location, items[i]);
				}
			}
			
			setBlock(location, new BlockData(null, null, Material.AIR));
		} else {
			setBlock(location, new BlockData(items, state, blockType));
			location.getBlock().breakNaturally(tool);
		}
	}
	
	//private
	private static void clearInventory(BlockState block) {
		if (block == null) {
			throw new IllegalArgumentException("block cannot be null.");
		}
		
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
		if (block == null) {
			throw new IllegalArgumentException("block cannot be null.");
		}
		if (data == null) {
			throw new IllegalArgumentException("data cannot be null.");
		}
		
		Material type = block.getType();
		
		block.setData(data.getData());
		
		if (type == Material.STANDING_BANNER || type == Material.WALL_BANNER) {
			Banner b1 = (Banner) block;
			Banner b2 = (Banner) data;
			b1.setBaseColor(b2.getBaseColor());
			b1.setPatterns(b2.getPatterns());
		} else if (type == Material.BEACON) {
			((Beacon) block).getInventory().setContents(((Beacon) data).getInventory().getContents());
		} else if (type == Material.BREWING_STAND) {
			BrewingStand b1 = (BrewingStand) block;
			BrewingStand b2 = (BrewingStand) data;
			b1.getInventory().setContents(b2.getInventory().getContents());
			b1.setBrewingTime(b2.getBrewingTime());
			b1.setFuelLevel(b2.getFuelLevel());
		} else if (type == Material.CHEST) {
			((Chest) block).getBlockInventory().setContents(((Chest) data).getBlockInventory().getContents());
		} else if (type == Material.COMMAND) {
			CommandBlock b1 = (CommandBlock) block;
			CommandBlock b2 = (CommandBlock) data;
			b1.setName(b2.getName());
			b1.setCommand(b2.getCommand());
		} else if (type == Material.FURNACE || type == Material.BURNING_FURNACE) {
			Furnace b1 = (Furnace) block;
			Furnace b2 = (Furnace) data;
			b1.setBurnTime(b2.getBurnTime());
			b1.setCookTime(b2.getCookTime());
			b1.getInventory().setContents(b2.getInventory().getContents());
		} else if (type == Material.MOB_SPAWNER) {
			CreatureSpawner b1 = (CreatureSpawner) block;
			CreatureSpawner b2 = (CreatureSpawner) data;
			b1.setSpawnedType(b2.getSpawnedType());
			b1.setDelay(b2.getDelay());
			b1.setSpawnedType(b2.getSpawnedType());
		} else if (type == Material.DISPENSER) {
			((Dispenser) block).getInventory().setContents(((Dispenser) data).getInventory().getContents());
		} else if (type == Material.DROPPER) {
			((Dropper) block).getInventory().setContents(((Dropper) data).getInventory().getContents());
		} else if (type == Material.END_GATEWAY) {
			EndGateway b1 = (EndGateway) block;
			EndGateway b2 = (EndGateway) data;
			b1.setExactTeleport(b2.isExactTeleport());
			b1.setExitLocation(b2.getExitLocation());
		} else if (type == Material.FLOWER_POT) {
			((FlowerPot) block).setContents(((FlowerPot) data).getContents());
		} else if (type == Material.HOPPER) {
			((Hopper) block).getInventory().setContents(((Hopper) data).getInventory().getContents());
		} else if (type == Material.JUKEBOX) {
			((Jukebox) block).setPlaying(((Jukebox) data).getPlaying());
		} else if (type == Material.NOTE_BLOCK) {
			((NoteBlock) block).setNote(((NoteBlock) data).getNote());
		} else if (type == Material.SIGN_POST || type == Material.WALL_SIGN) {
			Sign b1 = (Sign) block;
			String[] lines = ((Sign) data).getLines();
			for (int j = 0; j < lines.length; j++) {
				b1.setLine(j, lines[j]);
			}
		} else if (type == Material.SKULL) {
			Skull b1 = (Skull) block;
			Skull b2 = (Skull) data;
			b1.setOwningPlayer(b2.getOwningPlayer());
			b1.setRotation(b2.getRotation());
			b1.setSkullType(b2.getSkullType());
		}
	}
}