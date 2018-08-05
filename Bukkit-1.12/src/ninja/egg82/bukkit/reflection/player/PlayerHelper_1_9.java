package ninja.egg82.bukkit.reflection.player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ninja.egg82.patterns.ServiceLocator;

public final class PlayerHelper_1_9 implements IPlayerHelper {
	//vars
	private static Method getHandleMethod;
	private static Field pingField;
	
	//constructor
	public PlayerHelper_1_9() {
		
	}
	
	//public
	public void hidePlayer(Player player, Player playerToHide) {
		player.hidePlayer(ServiceLocator.getService(Plugin.class), playerToHide);
	}
	public void showPlayer(Player player, Player playerToShow) {
		player.showPlayer(ServiceLocator.getService(Plugin.class), playerToShow);
	}
	
	public synchronized int getPing(Player player) {
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
