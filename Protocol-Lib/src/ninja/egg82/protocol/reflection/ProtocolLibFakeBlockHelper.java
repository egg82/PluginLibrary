package ninja.egg82.protocol.reflection;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

import ninja.egg82.bukkit.BasePlugin;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.primitive.shorts.ShortArrayList;
import ninja.egg82.primitive.shorts.ShortList;
import ninja.egg82.protocol.reflection.wrappers.block.IPacketBlockHelper;
import ninja.egg82.protocol.utils.ProtocolReflectUtil;

public class ProtocolLibFakeBlockHelper implements IFakeBlockHelper {
	//vars
	private IPacketBlockHelper packetHelper = null;
	private int maxDistance = Bukkit.getViewDistance() * 16;
	
	private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
	
	//constructor
	public ProtocolLibFakeBlockHelper() {
		String gameVersion = ServiceLocator.getService(BasePlugin.class).getGameVersion();
		ProtocolReflectUtil.reflect(gameVersion, "ninja.egg82.protocol.reflection.wrappers.block");
		packetHelper = ServiceLocator.getService(IPacketBlockHelper.class);
	}
	
	//public
	public void updateBlock(Player player, Location blockLocation, Material newMaterial) {
		updateBlock(player, blockLocation, newMaterial, (short) 0);
	}
	public void updateBlock(Player player, Location blockLocation, Material newMaterial, short newMetadata) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		if (blockLocation == null) {
			throw new IllegalArgumentException("blockLocation cannot be null.");
		}
		if (!blockLocation.getWorld().equals(player.getWorld()) || blockLocation.distanceSquared(player.getLocation()) > maxDistance * maxDistance) {
			return;
		}
		
		if (blockLocation.getWorld().getName().equals(player.getWorld().getName())) {
			ProtocolReflectUtil.sendPacket(protocolManager, packetHelper.blockChange(blockLocation, newMaterial, newMetadata), player);
		}
	}
	public void updateBlock(Player[] players, Location blockLocation, Material newMaterial) {
		updateBlock(players, blockLocation, newMaterial, (short) 0);
	}
	public void updateBlock(Player[] players, Location blockLocation, Material newMaterial, short newMetadata) {
		if (players == null) {
			throw new IllegalArgumentException("players cannot be null.");
		}
		if (players.length == 0) {
			return;
		} else if (players.length == 1) {
			updateBlock(players[0], blockLocation, newMaterial, newMetadata);
			return;
		}
		
		for (int i = 0; i < players.length; i++) {
			updateBlock(players[i], blockLocation, newMaterial, newMetadata);
		}
	}
	
	public void updateBlocks(Player player, Location[] blockLocations, Material newMaterial) {
		updateBlocks(player, blockLocations, newMaterial, (short) 0);
	}
	public void updateBlocks(Player player, Location[] blockLocations, Material newMaterial, short newMetadata) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		if (blockLocations == null) {
			throw new IllegalArgumentException("blockLocations cannot be null.");
		}
		if (newMaterial == null) {
			throw new IllegalArgumentException("newMaterial cannot be null.");
		}
		
		ArrayList<PacketContainer> packets = new ArrayList<PacketContainer>();
		ArrayList<Location> tempLocs = new ArrayList<Location>(Arrays.asList(blockLocations));
		
		while (tempLocs.size() > 0) {
			ArrayList<Location> currentLocs = new ArrayList<Location>();
			
			String name = tempLocs.get(0).getWorld().getName();
			int chunkX = tempLocs.get(0).getBlockX() >> 4;
			int chunkZ = tempLocs.get(0).getBlockZ() >> 4;
			
			currentLocs.add(tempLocs.remove(0));
			
			for (int i = tempLocs.size() - 1; i >= 0; i--) {
				if (
					tempLocs.get(i).getWorld().getName().equals(name)
					&& tempLocs.get(i).getBlockX() >> 4 == chunkX
					&& tempLocs.get(i).getBlockZ() >> 4 == chunkZ
				) {
					currentLocs.add(tempLocs.remove(i));
				}
			}
			
			if (name.equals(player.getWorld().getName())) {
				packets.add(packetHelper.multiBlockChange(currentLocs.toArray(new Location[0]), newMaterial, newMetadata));
			}
		}
		
		for (int i = 0; i < packets.size(); i++) {
			ProtocolReflectUtil.sendPacket(protocolManager, packets.get(i), player);
		}
	}
	public void updateBlocks(Player player, Location[] blockLocations, Material[] newMaterials) {
		updateBlocks(player, blockLocations, newMaterials, new short[newMaterials.length]);
	}
	public void updateBlocks(Player player, Location[] blockLocations, Material[] newMaterials, short[] newMetadata) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		if (blockLocations == null) {
			throw new IllegalArgumentException("newLocations cannot be null.");
		}
		if (newMaterials == null) {
			throw new IllegalArgumentException("newMaterials cannot be null.");
		}
		if (blockLocations.length == 0 || newMaterials.length == 0) {
			return;
		} else if (blockLocations.length == 1 || newMaterials.length == 1) {
			updateBlock(player, blockLocations[0], newMaterials[0], newMetadata[0]);
			return;
		}
		if (blockLocations.length != newMaterials.length || newMaterials.length != newMetadata.length) {
			throw new RuntimeException("blockLocations,newMaterials, and newMetadata must be the same length.");
		}
		
		ArrayList<PacketContainer> packets = new ArrayList<PacketContainer>();
		ArrayList<Location> tempLocs = new ArrayList<Location>(Arrays.asList(blockLocations));
		ArrayList<Material> tempMats = new ArrayList<Material>(Arrays.asList(newMaterials));
		ShortList tempMeta = new ShortArrayList();
		
		for (int i = 0; i < newMetadata.length; i++) {
			tempMeta.add(newMetadata[i]);
		}
		
		while (tempLocs.size() > 0) {
			ArrayList<Location> currentLocs = new ArrayList<Location>();
			ArrayList<Material> currentMats = new ArrayList<Material>();
			ShortList currentMeta = new ShortArrayList();
			
			String name = tempLocs.get(0).getWorld().getName();
			int chunkX = tempLocs.get(0).getBlockX() >> 4;
			int chunkZ = tempLocs.get(0).getBlockZ() >> 4;
			
			currentLocs.add(tempLocs.remove(0));
			currentMats.add(tempMats.remove(0));
			currentMeta.add(tempMeta.removeShort(0));
			
			for (int i = tempLocs.size() - 1; i >= 0; i--) {
				if (
					tempLocs.get(i).getWorld().getName() == name
					&& tempLocs.get(i).getBlockX() >> 4 == chunkX
					&& tempLocs.get(i).getBlockZ() >> 4 == chunkZ
				) {
					currentLocs.add(tempLocs.remove(i));
					currentMats.add(tempMats.remove(i));
					currentMeta.add(tempMeta.removeShort(i));
				}
			}
			
			short[] metaOut = new short[currentMeta.size()];
			for (int i = 0; i < currentMeta.size(); i++) {
				metaOut[i] = currentMeta.getShort(i);
			}
			
			if (name.equals(player.getWorld().getName())) {
				packets.add(packetHelper.multiBlockChange(currentLocs.toArray(new Location[0]), currentMats.toArray(new Material[0]), metaOut));
			}
		}
		
		for (int i = 0; i < packets.size(); i++) {
			ProtocolReflectUtil.sendPacket(protocolManager, packets.get(i), player);
		}
	}
	public void updateBlocks(Player[] players, Location[] blockLocations, Material newMaterial) {
		updateBlocks(players, blockLocations, newMaterial, (short) 0);
	}
	public void updateBlocks(Player[] players, Location[] blockLocations, Material newMaterial, short newMetadata) {
		if (players == null) {
			throw new IllegalArgumentException("players cannot be null.");
		}
		if (players.length == 0) {
			return;
		} else if (players.length == 1) {
			updateBlocks(players[0], blockLocations, newMaterial, newMetadata);
			return;
		}
		if (blockLocations == null) {
			throw new IllegalArgumentException("blockLocations cannot be null.");
		}
		if (blockLocations.length == 0) {
			return;
		} else if (blockLocations.length == 1) {
			updateBlock(players, blockLocations[0], newMaterial, newMetadata);
			return;
		}
		
		for (int i = 0; i < players.length; i++) {
			updateBlocks(players[i], blockLocations, newMaterial, newMetadata);
		}
	}
	public void updateBlocks(Player[] players, Location[] blockLocations, Material[] newMaterials) {
		updateBlocks(players, blockLocations, newMaterials, new short[newMaterials.length]);
	}
	public void updateBlocks(Player[] players, Location[] blockLocations, Material[] newMaterials, short[] newMetadata) {
		if (players == null) {
			throw new IllegalArgumentException("players cannot be null.");
		}
		if (players.length == 0) {
			return;
		} else if (players.length == 1) {
			updateBlocks(players[0], blockLocations, newMaterials, newMetadata);
			return;
		}
		if (blockLocations == null) {
			throw new IllegalArgumentException("blockLocations cannot be null.");
		}
		if (newMaterials == null) {
			throw new IllegalArgumentException("newMaterials cannot be null.");
		}
		if (newMetadata == null) {
			throw new IllegalArgumentException("newMetadata cannot be null.");
		}
		if (newMetadata.length != newMaterials.length) {
			throw new RuntimeException("newMetaData must be equal in length to newMaterials");
		}
		if (blockLocations.length == 0 || newMaterials.length == 0) {
			return;
		} else if (blockLocations.length == 1 || newMaterials.length == 1) {
			updateBlock(players, blockLocations[0], newMaterials[0], newMetadata[0]);
			return;
		}
		
		for (int i = 0; i < players.length; i++) {
			updateBlocks(players[i], blockLocations, newMaterials, newMetadata);
		}
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	
}
