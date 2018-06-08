package ninja.egg82.bukkit.core;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

public class BlockData {
	//vars
	private ItemStack[] inventory = null;
	private BlockState state = null;
	private Material material = null;
	private Location location = null;
	
	//constructor
	public BlockData(ItemStack[] inventory, BlockState state, Material material, Location location) {
		this.inventory = inventory;
		this.state = state;
		this.material = material;
		this.location = location;
	}
	
	//public
	public ItemStack[] getInventory() {
		return inventory;
	}
	public BlockState getState() {
		return state;
	}
	public Material getMaterial() {
		return material;
	}
	public Location getLocation() {
		return location;
	}
	
	//private
	
}
