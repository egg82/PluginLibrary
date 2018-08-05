package ninja.egg82.bukkit.reflection.block.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import ninja.egg82.analytics.exceptions.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;

public class SerializationHelper_1_13 implements ISerializationHelper {
	//vars
	
	//constructor
	public SerializationHelper_1_13() {
		
	}
	
	//public
	public Block fromCompressedBytes(Location loc, Material type, byte blockData, byte[] data, boolean updatePhysics) {
		loc.getBlock().setType(type, updatePhysics);
		
		if (data != null) {
			try (ByteArrayInputStream stream = new ByteArrayInputStream(data); GZIPInputStream gzip = new GZIPInputStream(stream); BukkitObjectInputStream in = new BukkitObjectInputStream(gzip)) {
				loc.getBlock().setBlockData(Bukkit.createBlockData(in.readUTF()));
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
		try (GZIPOutputStream gzip = new GZIPOutputStream(stream) {{def.setLevel(compressionLevel);}}; BukkitObjectOutputStream out = new BukkitObjectOutputStream(gzip)) {
			out.writeUTF(state.getBlockData().getAsString());
		} catch (Exception ex) {
			IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
			if (handler != null) {
				handler.sendException(ex);
			}
			ex.printStackTrace();
			return null;
		}
		return stream.toByteArray();
	}
	
	//private
	
}
