package ninja.egg82.protocol.reflection;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.protocol.core.IFakeItemFrame;
import ninja.egg82.protocol.core.NullFakeItemFrame;

public class NullFakeItemFrameHelper implements IFakeItemFrameHelper {
	//vars
	
	//constructor
	public NullFakeItemFrameHelper() {
		
	}
	
	//public
	public IFakeItemFrame createItemFrame(Location loc, BlockFace facing) {
		return new NullFakeItemFrame();
	}
	public IFakeItemFrame createItemFrame(Location loc, BlockFace facing, ItemStack item) {
		return new NullFakeItemFrame();
	}
	public IFakeItemFrame toItemFrame(ItemFrame frame) {
		return new NullFakeItemFrame();
	}
	
	public boolean isValidLibrary() {
		return false;
	}
	
	//private
	
}
