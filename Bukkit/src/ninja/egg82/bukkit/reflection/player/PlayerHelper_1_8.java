package ninja.egg82.bukkit.reflection.player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class PlayerHelper_1_8 implements IPlayerHelper {
	//vars
	private static Method getHandleMethod;
	private static Field pingField;
	
	//constructor
	public PlayerHelper_1_8() {
		
	}
	
	//public
	@SuppressWarnings("deprecation")
	public ItemStack getItemInMainHand(Player player) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		return player.getItemInHand();
	}
	public ItemStack getItemInOffHand(Player player) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		return null;
	}
	@SuppressWarnings("deprecation")
	public void setItemInMainHand(Player player, ItemStack item) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		player.setItemInHand(item);
	}
	@SuppressWarnings("deprecation")
	public void setItemInOffHand(Player player, ItemStack item) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		if (player.getInventory().addItem(item).size() > 0) {
			player.setItemInHand(item);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void hidePlayer(Player player, Player playerToHide) {
		player.hidePlayer(playerToHide);
	}
	@SuppressWarnings("deprecation")
	public void showPlayer(Player player, Player playerToShow) {
		player.showPlayer(playerToShow);
	}
	
	public int getPing(Player player) {
		int ping = -1;
		
		try {
			if (getHandleMethod == null) {
				getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
				getHandleMethod.setAccessible(true);
			}
			Object entityPlayer = getHandleMethod.invoke(player);
			if (pingField == null) {
				pingField = entityPlayer.getClass().getDeclaredField("ping");
				pingField.setAccessible(true);
			}
			
			ping = pingField.getInt(entityPlayer);
			if (ping < 0) {
				ping = 0;
			}
		} catch (Exception ex) {
			
		}
		
		return ping;
	}
	
	public boolean supportsOffhand() {
		return false;
	}
	
	//private
	
}
