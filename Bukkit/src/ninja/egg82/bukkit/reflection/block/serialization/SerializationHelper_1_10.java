package ninja.egg82.bukkit.reflection.block.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.FlowerPot;
import org.bukkit.block.Skull;
import org.bukkit.material.MaterialData;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import ninja.egg82.bukkit.utils.CommandUtil;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.utils.UUIDUtil;

public class SerializationHelper_1_10 implements ISerializationHelper {
	//vars
	private SerializationHelper_1_9 down = new SerializationHelper_1_9();
	
	//constructor
	public SerializationHelper_1_10() {
		
	}
	
	//public
	@SuppressWarnings("deprecation")
	public void fromCompressedBytes(Location loc, Material type, byte blockData, byte[] data, boolean updatePhysics) {
		if (data == null) {
			throw new IllegalArgumentException("data cannot be null.");
		}
		
		loc.getBlock().setType(type, updatePhysics);
		loc.getBlock().setData(blockData, updatePhysics);
		BlockState newState = loc.getBlock().getState();
		
		try (ByteArrayInputStream stream = new ByteArrayInputStream(data); GZIPInputStream gzip = new GZIPInputStream(stream); BukkitObjectInputStream in = new BukkitObjectInputStream(gzip)) {
			fromCompressedBytes(newState, in, updatePhysics);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			ex.printStackTrace();
		}
	}
	@SuppressWarnings("deprecation")
	public void fromCompressedBytes(BlockState newState, BukkitObjectInputStream in, boolean updatePhysics) throws IOException, ClassNotFoundException {
		if (newState instanceof FlowerPot) {
			FlowerPot flowerPot = (FlowerPot) newState;
			flowerPot.setContents(new MaterialData(Material.valueOf(in.readUTF()), in.readByte()));
			newState.update(true, updatePhysics);
		} else if (newState instanceof Skull) {
			Skull skull = (Skull) newState;
			skull.setRotation(BlockFace.valueOf(in.readUTF()));
			skull.setSkullType(SkullType.valueOf(in.readUTF()));
			boolean hasOwner = in.readBoolean();
			if (hasOwner) {
				skull.setOwningPlayer(CommandUtil.getOfflinePlayerByUuid(UUIDUtil.readUuid(in)));
			}
			newState.update(true, updatePhysics);
		} else {
			down.fromCompressedBytes(newState, in, updatePhysics);
		}
	}
	public byte[] toCompressedBytes(BlockState state) {
		if (state == null) {
			throw new IllegalArgumentException("state cannot be null.");
		}
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try (GZIPOutputStream gzip = new GZIPOutputStream(stream); BukkitObjectOutputStream out = new BukkitObjectOutputStream(gzip)) {
			toCompressedBytes(state, out);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			ex.printStackTrace();
			return null;
		}
		return stream.toByteArray();
	}
	@SuppressWarnings("deprecation")
	public void toCompressedBytes(BlockState state, BukkitObjectOutputStream out) throws IOException {
		if (state instanceof FlowerPot) {
			FlowerPot flowerPot = (FlowerPot) state;
			MaterialData data = flowerPot.getContents();
			out.writeUTF(data.getItemType().name());
			out.writeByte(data.getData());
		} else if (state instanceof Skull) {
			Skull skull = (Skull) state;
			out.writeUTF(skull.getRotation().name());
			out.writeUTF(skull.getSkullType().name());
			out.writeBoolean(skull.hasOwner());
			if (skull.hasOwner()) {
				out.write(UUIDUtil.toBytes(skull.getOwningPlayer().getUniqueId()));
			}
		} else {
			down.toCompressedBytes(state, out);
		}
	}
	
	//private
	
}
