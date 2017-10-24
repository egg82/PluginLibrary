package ninja.egg82.protocol.reflection.wrappers.entity;

import java.util.List;

import com.comphenix.protocol.events.PacketContainer;

public interface IPacketEntityHelper {
	//functions
	PacketContainer destroy(List<Integer> entityIds);
	PacketContainer destroy(int entityId);
	PacketContainer destroy(int[] entityIds);
}
