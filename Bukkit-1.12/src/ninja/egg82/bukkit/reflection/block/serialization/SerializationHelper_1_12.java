package ninja.egg82.bukkit.reflection.block.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Bed;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import ninja.egg82.analytics.exceptions.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;

public class SerializationHelper_1_12 implements ISerializationHelper {
	//vars
	private SerializationHelper_1_11 down = new SerializationHelper_1_11();
	
	//constructor
	public SerializationHelper_1_12() {
		
	}
	
	//public
	@SuppressWarnings("deprecation")
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
		if (newState instanceof Bed) {
			Bed bed = (Bed) newState;
			bed.setColor(DyeColor.getByColor((Color) in.readObject()));
			newState.update(true, updatePhysics);
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
		if (state instanceof Bed) {
			Bed bed = (Bed) state;
			out.writeObject(bed.getColor().getColor());
			return true;
		}
		
		return down.toCompressedBytes(state, inventory, out);
	}
	
	//private
	
}
