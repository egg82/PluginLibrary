package ninja.egg82.protocol.reflection.wrappers.block;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.Direction;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;

import ninja.egg82.exceptions.ArgumentNullException;

public class PacketBlockHelper_1_8 implements IPacketBlockHelper {
	//vars
	private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
	
	//constructor
	public PacketBlockHelper_1_8() {
		
	}
	
	//public
	public PacketContainer blockChange(Location blockLocation, Material newMaterial, short newMetadata) {
		if (blockLocation == null) {
			throw new ArgumentNullException("blockLocation");
		}
		if (newMaterial == null) {
			throw new ArgumentNullException("newMaterial");
		}
		
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
		
		packet.getBlockPositionModifier()
			.write(0, new BlockPosition(new Vector(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getZ())));
		packet.getBlockData()
			.write(0, WrappedBlockData.createData(newMaterial, newMetadata));
		
		return packet;
	}
	public PacketContainer blockChange(Location blockLocation, Material newMaterial, short newMetadata, BlockFace facing) {
		if (blockLocation == null) {
			throw new ArgumentNullException("blockLocation");
		}
		if (newMaterial == null) {
			throw new ArgumentNullException("newMaterial");
		}
		
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
		
		packet.getBlockPositionModifier()
			.write(0, new BlockPosition(new Vector(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getZ())));
		packet.getBlockData()
			.write(0, WrappedBlockData.createData(newMaterial, newMetadata));
			//.write(1, getDirection(facing));
		
		return packet;
	}
	
	public PacketContainer multiBlockChange(Location[] blockLocations, Material newMaterial, short newMetadata) {
		if (blockLocations == null) {
			throw new ArgumentNullException("blockLocations");
		}
		if (blockLocations.length == 0) {
			throw new RuntimeException("blockLocations must have at least one element.");
		} else if (blockLocations.length == 1) {
			return blockChange(blockLocations[0], newMaterial, newMetadata);
		}
		if (newMaterial == null) {
			throw new ArgumentNullException("newMaterial");
		}
		
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
		WrappedBlockData wrappedData = WrappedBlockData.createData(newMaterial, newMetadata);
		
		HashSet<MultiBlockChangeInfo> info = new HashSet<MultiBlockChangeInfo>();
		for (int i = 0; i < blockLocations.length; i++) {
			if (
				blockLocations[i].getWorld().getName() != blockLocations[0].getWorld().getName()
				|| blockLocations[i].getBlockX() >> 4 != blockLocations[0].getBlockX() >> 4
				|| blockLocations[i].getBlockZ() >> 4 != blockLocations[0].getBlockZ() >> 4
			) {
				continue;
			}
			
			info.add(new MultiBlockChangeInfo(blockLocations[i], wrappedData));
		}
		
		packet.getChunkCoordIntPairs()
			.write(0, new ChunkCoordIntPair(blockLocations[0].getBlockX() >> 4, blockLocations[0].getBlockZ() >> 4));
		packet.getMultiBlockChangeInfoArrays()
			.write(0, info.toArray(new MultiBlockChangeInfo[0]));
		
		return packet;
	}
	public PacketContainer multiBlockChange(Location[] blockLocations, Material newMaterial, short newMetadata, BlockFace facing) {
		if (blockLocations == null) {
			throw new ArgumentNullException("blockLocations");
		}
		if (blockLocations.length == 0) {
			throw new RuntimeException("blockLocations must have at least one element.");
		} else if (blockLocations.length == 1) {
			return blockChange(blockLocations[0], newMaterial, newMetadata, facing);
		}
		if (newMaterial == null) {
			throw new ArgumentNullException("newMaterial");
		}
		
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
		WrappedBlockData wrappedData = WrappedBlockData.createData(newMaterial, newMetadata);
		
		HashSet<MultiBlockChangeInfo> info = new HashSet<MultiBlockChangeInfo>();
		for (int i = 0; i < blockLocations.length; i++) {
			if (
				blockLocations[i].getWorld().getName() != blockLocations[0].getWorld().getName()
				|| blockLocations[i].getBlockX() >> 4 != blockLocations[0].getBlockX() >> 4
				|| blockLocations[i].getBlockZ() >> 4 != blockLocations[0].getBlockZ() >> 4
			) {
				continue;
			}
			
			info.add(new MultiBlockChangeInfo(blockLocations[i], wrappedData));
		}
		
		packet.getChunkCoordIntPairs()
			.write(0, new ChunkCoordIntPair(blockLocations[0].getBlockX() >> 4, blockLocations[0].getBlockZ() >> 4));
		packet.getMultiBlockChangeInfoArrays()
			.write(0, info.toArray(new MultiBlockChangeInfo[0]));
		
		return packet;
	}
	public PacketContainer multiBlockChange(Location[] blockLocations, Material[] newMaterials, short[] newMetadata) {
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
			throw new RuntimeException("blockLocations and newMaterials must have at least one element.");
		} else if (blockLocations.length == 1 || newMaterials.length == 1) {
			return blockChange(blockLocations[0], newMaterials[0], newMetadata[0]);
		}
		
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
		
		HashSet<MultiBlockChangeInfo> info = new HashSet<MultiBlockChangeInfo>();
		int minBlocks = Math.min(blockLocations.length, newMaterials.length);
		for (int i = 0; i < minBlocks; i++) {
			if (
				blockLocations[i].getWorld().getName() != blockLocations[0].getWorld().getName()
				|| blockLocations[i].getBlockX() >> 4 != blockLocations[0].getBlockX() >> 4
				|| blockLocations[i].getBlockZ() >> 4 != blockLocations[0].getBlockZ() >> 4
			) {
				continue;
			}
			
			info.add(new MultiBlockChangeInfo(blockLocations[i], WrappedBlockData.createData(newMaterials[i], newMetadata[i])));
		}
		
		packet.getChunkCoordIntPairs()
			.write(0, new ChunkCoordIntPair(blockLocations[0].getBlockX() >> 4, blockLocations[0].getBlockZ() >> 4));
		packet.getMultiBlockChangeInfoArrays()
			.write(0, info.toArray(new MultiBlockChangeInfo[0]));
		
		return packet;
	}
	public PacketContainer multiBlockChange(Location[] blockLocations, Material[] newMaterials, short[] newMetadata, BlockFace[] facing) {
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
			throw new RuntimeException("blockLocations and newMaterials must have at least one element.");
		} else if (blockLocations.length == 1 || newMaterials.length == 1) {
			return blockChange(blockLocations[0], newMaterials[0], newMetadata[0], facing[0]);
		}
		
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
		
		HashSet<MultiBlockChangeInfo> info = new HashSet<MultiBlockChangeInfo>();
		int minBlocks = Math.min(blockLocations.length, newMaterials.length);
		for (int i = 0; i < minBlocks; i++) {
			if (
				blockLocations[i].getWorld().getName() != blockLocations[0].getWorld().getName()
				|| blockLocations[i].getBlockX() >> 4 != blockLocations[0].getBlockX() >> 4
				|| blockLocations[i].getBlockZ() >> 4 != blockLocations[0].getBlockZ() >> 4
			) {
				continue;
			}
			
			info.add(new MultiBlockChangeInfo(blockLocations[i], WrappedBlockData.createData(newMaterials[i], newMetadata[i])));
		}
		
		packet.getChunkCoordIntPairs()
			.write(0, new ChunkCoordIntPair(blockLocations[0].getBlockX() >> 4, blockLocations[0].getBlockZ() >> 4));
		packet.getMultiBlockChangeInfoArrays()
			.write(0, info.toArray(new MultiBlockChangeInfo[0]));
		
		return packet;
	}
	
	//private
	private Direction getDirection(BlockFace facing) {
		if (facing == BlockFace.NORTH) {
			return EnumWrappers.Direction.NORTH;
		} else if (facing == BlockFace.EAST) {
			return EnumWrappers.Direction.EAST;
		} else if (facing == BlockFace.SOUTH) {
			return EnumWrappers.Direction.SOUTH;
		} else if (facing == BlockFace.WEST) {
			return EnumWrappers.Direction.WEST;
		} else if (facing == BlockFace.UP) {
			return EnumWrappers.Direction.UP;
		} else {
			return EnumWrappers.Direction.DOWN;
		}
	}
}
