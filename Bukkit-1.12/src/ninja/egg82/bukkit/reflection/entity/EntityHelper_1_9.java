package ninja.egg82.bukkit.reflection.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

import ninja.egg82.bukkit.utils.BukkitReflectUtil;

@SuppressWarnings("deprecation")
public class EntityHelper_1_9 implements IEntityHelper {
	//vars
	
	//constructor
	public EntityHelper_1_9() {
		
	}
	
	//public
	public void addPassenger(Entity bottom, Entity top) {
		if (bottom == null) {
			throw new IllegalArgumentException("bottom cannot be null.");
		}
		if (top == null) {
			throw new IllegalArgumentException("top cannot be null.");
		}
		
		bottom.setPassenger(top);
		sendPacket();
	}
	public void removePassenger(Entity bottom, Entity top) {
		if (bottom == null) {
			throw new IllegalArgumentException("bottom cannot be null.");
		}
		if (top == null) {
			throw new IllegalArgumentException("top cannot be null.");
		}
		
		bottom.eject();
		sendPacket();
	}
	public void removeAllPassengers(Entity bottom) {
		if (bottom == null) {
			throw new IllegalArgumentException("bottom cannot be null.");
		}
		
		bottom.eject();
		sendPacket();
	}
	public List<Entity> getPassengers(Entity bottom) {
		if (bottom == null) {
			throw new IllegalArgumentException("bottom cannot be null.");
		}
		
		return new ArrayList<Entity>(Arrays.asList(bottom.getPassenger()));
	}
	
	public void damage(Damageable to, DamageCause cause, double damage) {
		Double d = Double.valueOf(damage);
		EntityDamageEvent damageEvent = new EntityDamageEvent(to, cause, new EnumMap<DamageModifier, Double>(ImmutableMap.of(DamageModifier.BASE, d)), new EnumMap<DamageModifier, Function<? super Double, Double>>(ImmutableMap.of(DamageModifier.BASE, Functions.constant(d))));
		Bukkit.getPluginManager().callEvent(damageEvent);
		damageEvent.getEntity().setLastDamageCause(damageEvent);
		to.damage(damage);
	}
	public void damage(Entity from, Damageable to, DamageCause cause, double damage) {
		Double d = Double.valueOf(damage);
		EntityDamageEvent damageEvent = new EntityDamageEvent(to, cause, new EnumMap<DamageModifier, Double>(ImmutableMap.of(DamageModifier.BASE, d)), new EnumMap<DamageModifier, Function<? super Double, Double>>(ImmutableMap.of(DamageModifier.BASE, Functions.constant(d))));
		Bukkit.getPluginManager().callEvent(damageEvent);
		damageEvent.getEntity().setLastDamageCause(damageEvent);
		to.damage(damage, from);
	}
	
	//private
	private void sendPacket() {
		// Reflection, ahoy!
		// Shamelessly stolen from EasyMFnE/DeadHorses
		Class<?> eentity;
		Class<?> mountPacket;
		try {
			eentity = BukkitReflectUtil.getNms("Entity");
			mountPacket = BukkitReflectUtil.getNms("PacketPlayOutMount");
			Constructor<?> mPacketConstructor = mountPacket.getConstructor(eentity);
			for(Player player : Bukkit.getServer().getOnlinePlayers()){
				Method getHandle = player.getClass().getMethod("getHandle");
				Object nmsPlayer = getHandle.invoke(player);					
				Field conField = nmsPlayer.getClass().getField("playerConnection");
				Object con = conField.get(nmsPlayer);
				Object packet = mPacketConstructor.newInstance(nmsPlayer);
				Method sendPacket = BukkitReflectUtil.getNms("PlayerConnection").getMethod("sendPacket", BukkitReflectUtil.getNms("Packet"));
				sendPacket.invoke(con, packet);
			}
		} catch (Exception ex) {
			
		}
	}
}
