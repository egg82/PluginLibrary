package ninja.egg82.protocol.core;

import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public interface IFakeItemFrame extends IFakeEntity {
	//functions
	public ItemStack getItem();
	public void setItem(ItemStack item);
	public Rotation getRotation();
	public void setRotation(Rotation rotation);
	public BlockFace getFacingDirection();
}
