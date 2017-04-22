package ninja.egg82.plugin.reflection.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import ninja.egg82.plugin.utils.SpigotReflectUtil;

public class EntityUtil_1_9 implements IEntityUtil {
	//vars
	
	//constructor
	public EntityUtil_1_9() {
		
	}
	
	//public
	
	//private
	@SuppressWarnings("deprecation")
	public void addPassenger(Entity bottom, Entity top) {
		if (bottom == null) {
			throw new IllegalArgumentException("bottom cannot be null.");
		}
		if (top == null) {
			throw new IllegalArgumentException("top cannot be null.");
		}
		
		bottom.setPassenger(top);
		sendPacket(bottom);
	}
	public void removePassenger(Entity bottom, Entity top) {
		if (bottom == null) {
			throw new IllegalArgumentException("bottom cannot be null.");
		}
		if (top == null) {
			throw new IllegalArgumentException("top cannot be null.");
		}
		
		bottom.eject();
		sendPacket(bottom);
	}
	public void removeAllPassengers(Entity bottom) {
		if (bottom == null) {
			throw new IllegalArgumentException("bottom cannot be null.");
		}
		
		bottom.eject();
		sendPacket(bottom);
	}
	
	private void sendPacket(Entity entity) {
		// Reflection, ahoy!
		// Shamelessly stolen from EasyMFnE/DeadHorses
		Class<?> eentity;
		Class<?> mountPacket;
		try {
			eentity = SpigotReflectUtil.getNms("Entity");
			mountPacket = SpigotReflectUtil.getNms("PacketPlayOutMount");
			Constructor<?> mPacketConstructor = mountPacket.getConstructor(eentity);
			for(Player player : Bukkit.getServer().getOnlinePlayers()){
				Method getHandle = player.getClass().getMethod("getHandle");
				Object nmsPlayer = getHandle.invoke(player);					
				Field conField = nmsPlayer.getClass().getField("playerConnection");
				Object con = conField.get(nmsPlayer);
				Object packet = mPacketConstructor.newInstance(nmsPlayer);
				Method sendPacket = SpigotReflectUtil.getNms("PlayerConnection").getMethod("sendPacket", SpigotReflectUtil.getNms("Packet"));
				sendPacket.invoke(con, packet);
			}
		} catch (Exception ex) {
			
		}
	}
}
