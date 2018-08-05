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
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.SkullType;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import ninja.egg82.analytics.exceptions.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;

@SuppressWarnings("deprecation")
public class SerializationHelper_1_8 implements ISerializationHelper {
	//vars
	
	//constructor
	public SerializationHelper_1_8() {
		
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
		if (newState instanceof Banner) {
			Banner banner = (Banner) newState;
			banner.setBaseColor(DyeColor.getByColor((Color) in.readObject()));
			for (int i = in.readInt(); i > 0; i--) {
				banner.addPattern((Pattern) in.readObject());
			}
		} else if (newState instanceof CommandBlock) {
			CommandBlock command = (CommandBlock) newState;
			command.setCommand(in.readUTF());
			command.setName(in.readUTF());
		} else if (newState instanceof CreatureSpawner) {
			CreatureSpawner spawner = (CreatureSpawner) newState;
			spawner.setSpawnedType(EntityType.valueOf(in.readUTF()));
			spawner.setDelay(in.readInt());
		} else if (newState instanceof Jukebox) {
			Jukebox jukebox = (Jukebox) newState;
			jukebox.setPlaying(Material.valueOf(in.readUTF()));
		} else if (newState instanceof NoteBlock) {
			NoteBlock noteBlock = (NoteBlock) newState;
			int octave = in.readInt();
			Tone tone = Tone.valueOf(in.readUTF());
			boolean isSharped = in.readBoolean();
			noteBlock.setNote(new Note(octave, tone, isSharped));
		} else if (newState instanceof Sign) {
			Sign sign = (Sign) newState;
			int lines = in.readInt();
			for (int i = 0; i < lines; i++) {
				sign.setLine(i, in.readUTF());
			}
		} else if (newState instanceof Skull) {
			Skull skull = (Skull) newState;
			skull.setRotation(BlockFace.valueOf(in.readUTF()));
			skull.setSkullType(SkullType.valueOf(in.readUTF()));
			boolean hasOwner = in.readBoolean();
			if (hasOwner) {
				skull.setOwner(in.readUTF());
			}
		} else if (newState instanceof InventoryHolder) {
			InventoryHolder inventoryHolder = (InventoryHolder) newState;
			
			List<ItemStack> items = new ArrayList<ItemStack>();
			for (int i = in.readInt(); i > 0; i--) {
				items.add((ItemStack) in.readObject());
			}
			inventoryHolder.getInventory().setContents(items.toArray(new ItemStack[0]));
		}
		
		if (!(newState instanceof InventoryHolder)) {
			// Don't update InventoryHolder- it clears the inv
			newState.update(true, updatePhysics);
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
		if (state instanceof Banner) {
			Banner banner = (Banner) state;
			out.writeObject(banner.getBaseColor().getColor());
			List<Pattern> patterns = banner.getPatterns();
			out.writeInt(patterns.size());
			for (Pattern pattern : patterns) {
				out.writeObject(pattern);
			}
			return true;
		} else if (state instanceof CommandBlock) {
			CommandBlock command = (CommandBlock) state;
			if (command.getCommand().isEmpty() && (command.getName().equals("@"))) {
				return false;
			}
			out.writeUTF(command.getCommand());
			out.writeUTF(command.getName());
			return true;
		} else if (state instanceof CreatureSpawner) {
			CreatureSpawner spawner = (CreatureSpawner) state;
			out.writeUTF(spawner.getSpawnedType().name());
			out.writeInt(spawner.getDelay());
			return true;
		} else if (state instanceof Jukebox) {
			Jukebox jukebox = (Jukebox) state;
			if (!jukebox.isPlaying()) {
				return false;
			}
			out.writeUTF(jukebox.getPlaying().name());
			return true;
		} else if (state instanceof NoteBlock) {
			NoteBlock noteBlock = (NoteBlock) state;
			Note note = noteBlock.getNote();
			out.writeInt(note.getOctave());
			out.writeUTF(note.getTone().name());
			out.writeBoolean(note.isSharped());
			return true;
		} else if (state instanceof Sign) {
			Sign sign = (Sign) state;
			String[] lines = sign.getLines();
			if (lines.length == 0) {
				return false;
			}
			out.writeInt(lines.length);
			for (String line : lines) {
				out.writeUTF(line);
			}
			return true;
		} else if (state instanceof Skull) {
			Skull skull = (Skull) state;
			out.writeUTF(skull.getRotation().name());
			out.writeUTF(skull.getSkullType().name());
			out.writeBoolean(skull.hasOwner());
			if (skull.hasOwner()) {
				out.writeUTF(skull.getOwner());
			}
			return true;
		} else if (state instanceof InventoryHolder) {
			InventoryHolder inventoryHolder = (InventoryHolder) state;
			ItemStack[] items = (inventory != null) ? inventory : inventoryHolder.getInventory().getContents();
			if (items.length == 0) {
				return false;
			}
			out.writeInt(items.length);
			for (ItemStack i : items) {
				out.writeObject(i);
			}
			return true;
		}
		return false;
	}
	
	//private
	
}
