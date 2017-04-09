package ninja.egg82.plugin.core;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

public class BlockData {
	//vars
	private ItemStack[] inventory = null;
	private BlockState state = null;
	private Material material = null;
	
	//constructor
	public BlockData(ItemStack[] inventory, BlockState state, Material material) {
		this.inventory = inventory;
		this.state = state;
		this.material = material;
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
	
	//private
	
}
