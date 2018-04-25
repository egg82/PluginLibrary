package ninja.egg82.plugin.reflection.player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ninja.egg82.exceptions.ArgumentNullException;

public final class PlayerHelper_1_9 implements IPlayerHelper {
	//vars
	private static Method getHandleMethod;
	private static Field pingField;
	
	//constructor
	public PlayerHelper_1_9() {
		
	}
	
	//public
	public ItemStack getItemInMainHand(Player player) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		return player.getInventory().getItemInMainHand();
	}
	public ItemStack getItemInOffHand(Player player) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		return player.getInventory().getItemInOffHand();
	}
	public void setItemInMainHand(Player player, ItemStack item) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		player.getInventory().setItemInMainHand(item);
	}
	public void setItemInOffHand(Player player, ItemStack item) {
		if (player == null) {
			throw new ArgumentNullException("player");
		}
		player.getInventory().setItemInOffHand(item);
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
		return true;
	}
	
	//private
	
}
