package ninja.egg82.protocol.reflection;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.enums.BukkitInitType;
import ninja.egg82.protocol.core.IFakeItemFrame;
import ninja.egg82.protocol.core.ProtocolLibFakeItemFrame;
import ninja.egg82.protocol.utils.ProtocolReflectUtil;
import ninja.egg82.startup.InitRegistry;

public class ProtocolLibFakeItemFrameHelper implements IFakeItemFrameHelper {
	//vars
	
	//constructor
	public ProtocolLibFakeItemFrameHelper() {
		String gameVersion = ServiceLocator.getService(InitRegistry.class).getRegister(BukkitInitType.GAME_VERSION, String.class);
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
