package ninja.egg82.protocol.reflection;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.comphenix.protocol.events.PacketContainer;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.enums.SpigotInitType;
import ninja.egg82.plugin.utils.VersionUtil;
import ninja.egg82.protocol.reflection.wrappers.block.IPacketBlockHelper;
import ninja.egg82.startup.InitRegistry;

public class ProtocolLibFakeBlockHelper implements IFakeBlockHelper {
	//vars
	private IPacketBlockHelper packetHelper = null;
	private int maxDistance = Bukkit.getViewDistance() * 16;
	
	//constructor
	public ProtocolLibFakeBlockHelper() {
		String gameVersion = ServiceLocator.getService(InitRegistry.class).getRegister(SpigotInitType.GAME_VERSION, String.class);
		reflect(gameVersion, "ninja.egg82.protocol.reflection.wrappers.block");
		packetHelper = ServiceLocator.getService(IPacketBlockHelper.class);
	}
	
	//public
	public void updateBlock(Player player, Location blockLocation, Material newMaterial) {
		updateBlock(player, blockLocation, newMaterial, (short) 0);
	}
	public void updateBlock(Player player, Location blockLocation, Material newMaterial, short newMetadata) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		if (blockLocation == null) {
			throw new ArgumentNullException("blockLocation");
		}
		if (!blockLocation.getWorld().equals(player.getWorld()) || blockLocation.distanceSquared(player.getLocation()) > maxDistance * maxDistance) {
			return;
		}
		
		if (blockLocation.getWorld().getName() == player.getWorld().getName()) {
			packetHelper.send(packetHelper.blockChange(blockLocation, newMaterial, newMetadata), player);
		}
	}
	public void updateBlock(Player[] players, Location blockLocation, Material newMaterial) {
		updateBlock(players, blockLocation, newMaterial, (short) 0);
	}
	public void updateBlock(Player[] players, Location blockLocation, Material newMaterial, short newMetadata) {
		if (players == null) {
			throw new ArgumentNullException("players");
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
			throw new ArgumentNullException("player");
		}
		if (blockLocations == null) {
			throw new ArgumentNullException("blockLocations");
		}
		if (newMaterial == null) {
			throw new ArgumentNullException("newMaterial");
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
					tempLocs.get(i).getWorld().getName() == name
					&& tempLocs.get(i).getBlockX() >> 4 == chunkX
					&& tempLocs.get(i).getBlockZ() >> 4 == chunkZ
				) {
					currentLocs.add(tempLocs.remove(i));
				}
			}
			
			if (name == player.getWorld().getName()) {
				packets.add(packetHelper.multiBlockChange(currentLocs.toArray(new Location[0]), newMaterial, newMetadata));
			}
		}
		
		for (int i = 0; i < packets.size(); i++) {
			packetHelper.send(packets.get(i), player);
		}
	}
	public void updateBlocks(Player player, Location[] blockLocations, Material[] newMaterials) {
		updateBlocks(player, blockLocations, newMaterials, new short[newMaterials.length]);
	}
	public void updateBlocks(Player player, Location[] blockLocations, Material[] newMaterials, short[] newMetadata) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		if (blockLocations == null) {
			throw new ArgumentNullException("blockLocations");
		}
		if (newMaterials == null) {
			throw new ArgumentNullException("newMaterials");
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
		ArrayList<Short> tempMeta = new ArrayList<Short>();
		
		for (int i = 0; i < newMetadata.length; i++) {
			tempMeta.add(newMetadata[i]);
		}
		
		while (tempLocs.size() > 0) {
			ArrayList<Location> currentLocs = new ArrayList<Location>();
			ArrayList<Material> currentMats = new ArrayList<Material>();
			ArrayList<Short> currentMeta = new ArrayList<Short>();
			
			String name = tempLocs.get(0).getWorld().getName();
			int chunkX = tempLocs.get(0).getBlockX() >> 4;
			int chunkZ = tempLocs.get(0).getBlockZ() >> 4;
			
			currentLocs.add(tempLocs.remove(0));
			currentMats.add(tempMats.remove(0));
			currentMeta.add(tempMeta.remove(0));
			
			for (int i = tempLocs.size() - 1; i >= 0; i--) {
				if (
					tempLocs.get(i).getWorld().getName() == name
					&& tempLocs.get(i).getBlockX() >> 4 == chunkX
					&& tempLocs.get(i).getBlockZ() >> 4 == chunkZ
				) {
					currentLocs.add(tempLocs.remove(i));
					currentMats.add(tempMats.remove(i));
					currentMeta.add(tempMeta.remove(i));
				}
			}
			
			short[] metaOut = new short[currentMeta.size()];
			for (int i = 0; i < currentMeta.size(); i++) {
				metaOut[i] = currentMeta.get(i);
			}
			
			if (name == player.getWorld().getName()) {
				packets.add(packetHelper.multiBlockChange(currentLocs.toArray(new Location[0]), currentMats.toArray(new Material[0]), metaOut));
			}
		}
		
		for (int i = 0; i < packets.size(); i++) {
			packetHelper.send(packets.get(i), player);
		}
	}
	public void updateBlocks(Player[] players, Location[] blockLocations, Material newMaterial) {
		updateBlocks(players, blockLocations, newMaterial, (short) 0);
	}
	public void updateBlocks(Player[] players, Location[] blockLocations, Material newMaterial, short newMetadata) {
		if (players == null) {
			throw new ArgumentNullException("players");
		}
		if (players.length == 0) {
			return;
		} else if (players.length == 1) {
			updateBlocks(players[0], blockLocations, newMaterial, newMetadata);
			return;
		}
		if (blockLocations == null) {
			throw new ArgumentNullException("blockLocations");
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
			throw new ArgumentNullException("players");
		}
		if (players.length == 0) {
			return;
		} else if (players.length == 1) {
			updateBlocks(players[0], blockLocations, newMaterials, newMetadata);
			return;
		}
		if (blockLocations == null) {
			throw new ArgumentNullException("blockLocations");
		}
		if (newMaterials == null) {
			throw new ArgumentNullException("newMaterials");
		}
		if (newMetadata == null) {
			throw new ArgumentNullException("newMetadata");
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
	private void reflect(String version, String pkg) {
		reflect(version, pkg, true);
	}
	private void reflect(String version, String pkg, boolean lazyInitialize) {
		Class<?> bestMatch = VersionUtil.getBestMatch(version, pkg);
		
		if (bestMatch != null) {
			ServiceLocator.provideService(bestMatch, lazyInitialize);
		}
	}
}
