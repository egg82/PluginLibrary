package ninja.egg82.protocol.reflection;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.protocol.core.IFakeItemFrame;

public interface IFakeItemFrameHelper {
	//functions
	IFakeItemFrame createItemFrame(Location loc, BlockFace facing);
	IFakeItemFrame createItemFrame(Location loc, BlockFace facing, ItemStack item);
	IFakeItemFrame toItemFrame(ItemFrame frame);
	
	boolean isValidLibrary();
}
