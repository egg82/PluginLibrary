package ninja.egg82.bukkit.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;

public class ItemStackSerializationUtil {
	//vars
	private static final Encoder encoder = Base64.getEncoder();
	private static final Decoder decoder = Base64.getDecoder();
	
	//constructor
	public ItemStackSerializationUtil() {
		
	}
	
	//public
	public static ItemStack[] fromBase64(String encoded) {
		if (encoded == null) {
			throw new IllegalArgumentException("encoded cannot be null.");
		}
		
		byte[] bytes = null;
		try {
			bytes = decoder.decode(encoded);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw new RuntimeException("Could not get ItemStack array from Base64 string.", ex);
		}
		
		return fromCompressedBytes(bytes);
	}
	public static String toBase64(ItemStack[] items) {
		return toBase64(items, Deflater.DEFAULT_COMPRESSION);
	}
	public static String toBase64(ItemStack[] items, int compressionLevel) {
		if (items == null) {
			throw new IllegalArgumentException("items cannot be null.");
		}
		
		return encoder.encodeToString(toCompressedBytes(items, compressionLevel));
	}
	
	public static ItemStack[] fromCompressedBytes(byte[] bytes) {
		if (bytes == null) {
			throw new IllegalArgumentException("bytes cannot be null.");
		}
		
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		List<ItemStack> retVal = new ArrayList<ItemStack>();
		try (GZIPInputStream gzip = new GZIPInputStream(stream); BukkitObjectInputStream in = new BukkitObjectInputStream(gzip)) {
			for (int i = in.readInt(); i > 0; i--) {
				retVal.add((ItemStack) in.readObject());
			}
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw new RuntimeException("Could not get ItemStack array compressed bytes.", ex);
		}
		return retVal.toArray(new ItemStack[0]);
	}
	public static byte[] toCompressedBytes(ItemStack[] items) {
		return toCompressedBytes(items, Deflater.DEFAULT_COMPRESSION);
	}
	public static byte[] toCompressedBytes(ItemStack[] items, int compressionLevel) {
		if (items == null) {
			throw new IllegalArgumentException("items cannot be null.");
		}
		if (compressionLevel < -1) {
			throw new IllegalArgumentException("compressionLevel must be between -1 and " + Deflater.BEST_COMPRESSION);
		}
		if (compressionLevel > Deflater.BEST_COMPRESSION) {
			throw new IllegalArgumentException("compressionLevel must be between -1 and " + Deflater.BEST_COMPRESSION);
		}
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try (GZIPOutputStream gzip = new GZIPOutputStream(stream) {{def.setLevel(compressionLevel);}}; BukkitObjectOutputStream out = new BukkitObjectOutputStream(gzip)) {
			out.writeInt(items.length);
			for (ItemStack i : items) {
				out.writeObject(i);
			}
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw new RuntimeException("Could not get compressed bytes form ItemStack array.", ex);
		}
		
		return stream.toByteArray();
	}
	
	//private
	
}
