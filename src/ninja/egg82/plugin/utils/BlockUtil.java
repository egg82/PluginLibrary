package ninja.egg82.plugin.utils;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
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

public class BlockUtil {
	//vars
	
	//constructor
	public BlockUtil() {
		
	}
	
	//public
	public static Location getTopAirBlock(Location l) {
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
	
	public static ArrayList<ItemStack[]> getYLineBlockInventory(Location top, int endY) {
		ArrayList<ItemStack[]> s = new ArrayList<ItemStack[]>();
		BlockState state = null;
		Material type = null;
		
		do {
			state = top.getBlock().getState();
			type = state.getType();
			
			if (state instanceof InventoryHolder) {
				InventoryHolder holder = (InventoryHolder) state;
				s.add(holder.getInventory().getContents());
			} else if (type == Material.FLOWER_POT) {
				MaterialData contents = ((FlowerPot) state).getContents();
				if (contents != null) {
					s.add(new ItemStack[]{contents.toItemStack()});
				} else {
					s.add(null);
				}
			} else if (type == Material.JUKEBOX) {
				Material playing = ((Jukebox) state).getPlaying();
				if (playing != null) {
					s.add(new ItemStack[]{new ItemStack(playing, 1)});
				} else {
					s.add(null);
				}
			} else {
				s.add(null);
			}
		} while (top.subtract(0.0d, 1.0d, 0.0d).getBlockY() >= endY);
		
		return s;
	}
	public static void setYLineBlockInventory(Location top, ArrayList<ItemStack[]> inv) {
		BlockState state = null;
		Material type = null;
		ItemStack[] stack = null;
		
		for (int i = 0; i < inv.size(); i++) {
			state = top.getBlock().getState();
			type = state.getType();
			
			if (state instanceof InventoryHolder) {
				InventoryHolder holder = (InventoryHolder) state;
				holder.getInventory().setContents(inv.get(i));
			} else if (type == Material.FLOWER_POT) {
				stack = inv.get(i);
				if (stack != null) {
					((FlowerPot) state).setContents(stack[0].getData());
				}
			} else if (type == Material.JUKEBOX) {
				stack = inv.get(i);
				if (stack != null) {
					((Jukebox) state).setPlaying(stack[0].getType());
				}
			}
			
			top.subtract(0.0d, 1.0d, 0.0d);
		}
	}
	
	public static BlockState[] getYLineBlockState(Location top, int endY) {
		BlockState[] d = new BlockState[top.getBlockY() - endY + 1];
		int i = 0;
		
		do {
			d[i] = top.getBlock().getState();
			i++;
		} while (top.subtract(0.0d, 1.0d, 0.0d).getBlockY() >= endY);
		
		return d;
	}
	public static void setYLineBlockState(Location top, BlockState[] data) {
		for (int i = 0; i < data.length; i++) {
			setBlockData(top.getBlock().getState(), data[i]);
			top.subtract(0.0d, 1.0d, 0.0d);
		}
	}
	
	public static Material[] removeYLineBlocks(Location top, int endY) {
		Material[] b = new Material[top.getBlockY() - endY + 1];
		int i = 0;
		Block block = null;
		
		do {
			block = top.getBlock();
			b[i] = block.getType();
			clearBlockInventory(block.getState());
			block.setType(Material.AIR);
			i++;
		} while (top.subtract(0.0d, 1.0d, 0.0d).getBlockY() >= endY);
		
		return b;
	}
	public static void addYLineBlocks(Location top, Material[] blocks) {
		for (int i = 0; i < blocks.length; i++) {
			top.getBlock().setType(blocks[i]);
			top.subtract(0.0d, 1.0d, 0.0d);
		}
	}
	
	public static void clearBlockInventory(BlockState block) {
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
		
		block.update(true);
	}
	
	//private
	private static void setBlockData(BlockState block, BlockState data) {
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
		
		block.update(true);
	}
}