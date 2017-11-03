package ninja.egg82.protocol.core;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NullFakeItemFrame implements IFakeItemFrame {
	//vars
	
	//constructor
	public NullFakeItemFrame(ItemFrame frame) {
		
	}
	public NullFakeItemFrame(Location loc, BlockFace facing) {
		
	}
	public NullFakeItemFrame(Location loc, BlockFace facing, ItemStack item) {
		
	}
	
	//public
	public boolean addPlayer(Player player) {
		return false;
	}
	public boolean removePlayer(Player player) {
		return false;
	}
	public void removeAllPlayers() {
		
	}
	
	public String getDisplayName() {
		return null;
	}
	public void setDisplayName(String displayName) {
		
	}
	public boolean isDisplayNameVisible() {
		return false;
	}
	public void setDisplayNameVisible(boolean value) {
		
	}
	public boolean isSilent() {
		return true;
	}
	public void setSilent(boolean value) {
		
	}
	
	public ItemStack getItem() {
		return null;
	}
	public void setItem(ItemStack item) {
		
	}
	
	public Rotation getRotation() {
		return null;
	}
	public void setRotation(Rotation rotation) {
		
	}
	
	public BlockFace getFacingDirection() {
		return null;
	}
	
	public Location getLocation() {
		return null;
	}
	
	public int getId() {
		return -1;
	}
	public UUID getUuid() {
		return null;
	}
	
	public void destroy() {
		
	}
	
	//private
	
}
