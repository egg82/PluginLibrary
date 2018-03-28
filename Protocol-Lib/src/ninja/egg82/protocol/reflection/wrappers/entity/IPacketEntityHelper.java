package ninja.egg82.protocol.reflection.wrappers.entity;

import com.comphenix.protocol.events.PacketContainer;

import it.unimi.dsi.fastutil.ints.IntList;

public interface IPacketEntityHelper {
	//functions
	PacketContainer destroy(IntList entityIds);
	PacketContainer destroy(int entityId);
	PacketContainer destroy(int[] entityIds);
}
