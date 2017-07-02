package ninja.egg82.plugin.reflection.protocol.wrappers.block;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.comphenix.protocol.events.PacketContainer;

public interface IPacketBlockHelper {
	//functions
	PacketContainer blockChange(Location blockLocation, Material newMaterial, short newMetadata);
	
	PacketContainer multiBlockChange(Location[] blockLocations, Material newMaterial, short newMetadata);
	PacketContainer multiBlockChange(Location[] blockLocations, Material[] newMaterials, short[] newMetadata);
	
	void send(PacketContainer packet, Player player);
	void send(PacketContainer packet, List<Player> players);
	void send(PacketContainer packet, Player[] players);
}
