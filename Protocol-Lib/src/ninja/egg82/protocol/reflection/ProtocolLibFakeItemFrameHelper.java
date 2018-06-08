package ninja.egg82.protocol.reflection;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.bukkit.BasePlugin;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.protocol.core.IFakeItemFrame;
import ninja.egg82.protocol.core.ProtocolLibFakeItemFrame;
import ninja.egg82.protocol.utils.ProtocolReflectUtil;

public class ProtocolLibFakeItemFrameHelper implements IFakeItemFrameHelper {
	//vars
	
	//constructor
	public ProtocolLibFakeItemFrameHelper() {
		String gameVersion = ServiceLocator.getService(BasePlugin.class).getGameVersion();
		ProtocolReflectUtil.reflect(gameVersion, "ninja.egg82.protocol.reflection.wrappers.itemFrame");
	}
	
	//public
	public IFakeItemFrame createItemFrame(Location loc, BlockFace facing) {
		return new ProtocolLibFakeItemFrame(loc, facing, null, Rotation.NONE);
	}
	public IFakeItemFrame createItemFrame(Location loc, BlockFace facing, ItemStack item) {
		return new ProtocolLibFakeItemFrame(loc, facing, item, Rotation.NONE);
	}
	public IFakeItemFrame createItemFrame(Location loc, BlockFace facing, ItemStack item, Rotation itemRotation) {
		return new ProtocolLibFakeItemFrame(loc, facing, item, itemRotation);
	}
	public IFakeItemFrame toItemFrame(ItemFrame frame) {
		return new ProtocolLibFakeItemFrame(frame);
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	
}
