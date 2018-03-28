package ninja.egg82.protocol.reflection.wrappers.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

import it.unimi.dsi.fastutil.ints.IntList;
import ninja.egg82.exceptions.ArgumentNullException;

public abstract class PacketEntityHelper_1_8 implements IPacketEntityHelper {
	//vars
	private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
	
	//constructor
	public PacketEntityHelper_1_8() {
		
	}
	
	//public
	public PacketContainer destroy(IntList entityIds) {
		if (entityIds == null) {
			throw new ArgumentNullException("entityIds");
		}
		
		return destroy(entityIds.toIntArray());
	}
	public PacketContainer destroy(int entityId) {
		return destroy(new int[] {entityId});
	}
	public PacketContainer destroy(int[] entityIds) {
		if (entityIds == null) {
			throw new ArgumentNullException("entityIds");
		}
		
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		
		packet.getIntegerArrays()
			.write(0, entityIds);
		
		return packet;
	}
	
	//private
	
}
