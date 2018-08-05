package ninja.egg82.bukkit.reflection.block.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Comparator;
import org.bukkit.block.DaylightDetector;
import org.bukkit.block.EnchantingTable;
import org.bukkit.block.EnderChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import ninja.egg82.analytics.exceptions.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;

@SuppressWarnings("deprecation")
public class SerializationHelper_1_11 implements ISerializationHelper {
	//vars
	private SerializationHelper_1_10 down = new SerializationHelper_1_10();
	
	//constructor
	public SerializationHelper_1_11() {
		
	}
	
	//public
	public Block fromCompressedBytes(Location loc, Material type, byte blockData, byte[] data, boolean updatePhysics) {
		loc.getBlock().setType(type, updatePhysics);
		loc.getBlock().setData(blockData, updatePhysics);
		BlockState newState = loc.getBlock().getState();
		
		if (data != null) {
			try (ByteArrayInputStream stream = new ByteArrayInputStream(data); GZIPInputStream gzip = new GZIPInputStream(stream); BukkitObjectInputStream in = new BukkitObjectInputStream(gzip)) {
				fromCompressedBytes(newState, in, updatePhysics);
			} catch (Exception ex) {
				IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
				if (handler != null) {
					handler.sendException(ex);
				}
				ex.printStackTrace();
			}
		}
		
		return loc.getBlock();
	}
	public void fromCompressedBytes(BlockState newState, BukkitObjectInputStream in, boolean updatePhysics) throws IOException, ClassNotFoundException {
		if (newState instanceof Comparator) {
			// Do nothing
		} else if (newState instanceof DaylightDetector) {
			// Do nothing
		} else if (newState instanceof EnchantingTable) {
			EnchantingTable enchantingTable = (EnchantingTable) newState;
			enchantingTable.setCustomName(in.readUTF());
			newState.update(true, updatePhysics);
		} else if (newState instanceof EnderChest) {
			// Do nothing
		} else if (newState instanceof ShulkerBox) {
			ShulkerBox shulkerBox = (ShulkerBox) newState;
			shulkerBox.setData(new MaterialData(newState.getType(), DyeColor.getByColor((Color) in.readObject()).getDyeData())); // Only way to do this is by using deprecated functions
			boolean hasCustomName = in.readBoolean();
			if (hasCustomName) {
				shulkerBox.setCustomName(in.readUTF());
			}
			boolean isLocked = in.readBoolean();
			if (isLocked) {
				shulkerBox.setLock(in.readUTF());
			}
			
			List<ItemStack> items = new ArrayList<ItemStack>();
			for (int i = in.readInt(); i > 0; i--) {
				items.add((ItemStack) in.readObject());
			}
			shulkerBox.getInventory().setContents(items.toArray(new ItemStack[0]));
			// Don't update InventoryHolder- it clears the inv
		} else {
			down.fromCompressedBytes(newState, in, updatePhysics);
		}
	}
	public byte[] toCompressedBytes(BlockState state) {
		return toCompressedBytes(state, null, Deflater.DEFAULT_COMPRESSION);
	}
	public byte[] toCompressedBytes(BlockState state, ItemStack[] inventory) {
		return toCompressedBytes(state, inventory, Deflater.DEFAULT_COMPRESSION);
	}
	public byte[] toCompressedBytes(BlockState state, int compressionLevel) {
		return toCompressedBytes(state, null, compressionLevel);
	}
	public byte[] toCompressedBytes(BlockState state, ItemStack[] inventory, int compressionLevel) {
		if (state == null) {
			throw new IllegalArgumentException("state cannot be null.");
		}
		if (compressionLevel < -1) {
			throw new IllegalArgumentException("compressionLevel must be between -1 and " + Deflater.BEST_COMPRESSION);
		}
		if (compressionLevel > Deflater.BEST_COMPRESSION) {
			throw new IllegalArgumentException("compressionLevel must be between -1 and " + Deflater.BEST_COMPRESSION);
		}
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		boolean hasData = false;
		try (GZIPOutputStream gzip = new GZIPOutputStream(stream) {{def.setLevel(compressionLevel);}}; BukkitObjectOutputStream out = new BukkitObjectOutputStream(gzip)) {
			hasData = toCompressedBytes(state, inventory, out);
		} catch (Exception ex) {
			IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
			if (handler != null) {
				handler.sendException(ex);
			}
			ex.printStackTrace();
			return null;
		}
		return (hasData) ? stream.toByteArray() : null;
	}
	public boolean toCompressedBytes(BlockState state, ItemStack[] inventory, BukkitObjectOutputStream out) throws IOException {
		if (state instanceof Comparator) {
			// Do nothing
			return false;
		} else if (state instanceof DaylightDetector) {
			// Do nothing
			return false;
		} else if (state instanceof EnchantingTable) {
			EnchantingTable enchantingTable = (EnchantingTable) state;
			if (enchantingTable.getCustomName() == null) {
				return false;
			}
			out.writeUTF(enchantingTable.getCustomName());
			return true;
		} else if (state instanceof EnderChest) {
			// Do nothing
			return false;
		} else if (state instanceof ShulkerBox) {
			ShulkerBox shulkerBox = (ShulkerBox) state;
			out.writeObject(shulkerBox.getColor().getColor());
			out.writeBoolean((shulkerBox.getCustomName() != null) ? true : false);
			if (shulkerBox.getCustomName() != null) {
				out.writeUTF(shulkerBox.getCustomName());
			}
			out.writeBoolean(shulkerBox.isLocked());
			if (shulkerBox.isLocked()) {
				out.writeUTF(shulkerBox.getLock());
			}
			
			ItemStack[] items = (inventory != null) ? inventory : shulkerBox.getInventory().getContents();
			out.writeInt(items.length);
			for (ItemStack i : items) {
				out.writeObject(i);
			}
			return true;
		}
		
		return down.toCompressedBytes(state, inventory, out);
	}
	
	//private
	
}
