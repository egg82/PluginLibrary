package ninja.egg82.plugin.reflection.entity;

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

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.plugin.utils.SpigotReflectUtil;

@SuppressWarnings("deprecation")
public class EntityHelper_1_9 implements IEntityHelper {
	//vars
	
	//constructor
	public EntityHelper_1_9() {
		
	}
	
	//public
	public void addPassenger(Entity bottom, Entity top) {
		if (bottom == null) {
			throw new ArgumentNullException("bottom");
		}
		if (top == null) {
			throw new ArgumentNullException("top");
		}
		
		bottom.setPassenger(top);
		sendPacket(bottom);
	}
	public void removePassenger(Entity bottom, Entity top) {
		if (bottom == null) {
			throw new ArgumentNullException("bottom");
		}
		if (top == null) {
			throw new ArgumentNullException("top");
		}
		
		bottom.eject();
		sendPacket(bottom);
	}
	public void removeAllPassengers(Entity bottom) {
		if (bottom == null) {
			throw new ArgumentNullException("bottom");
		}
		
		bottom.eject();
		sendPacket(bottom);
	}
	public List<Entity> getPassengers(Entity bottom) {
		if (bottom == null) {
			throw new ArgumentNullException("bottom");
		}
		
		return new ArrayList<Entity>(Arrays.asList(bottom.getPassenger()));
	}
	
	public void damage(Damageable to, DamageCause cause, double damage) {
		EntityDamageEvent damageEvent = new EntityDamageEvent(to, cause, new EnumMap<DamageModifier, Double>(ImmutableMap.of(DamageModifier.BASE, damage)), new EnumMap<DamageModifier, Function<? super Double, Double>>(ImmutableMap.of(DamageModifier.BASE, Functions.constant(damage))));
		Bukkit.getPluginManager().callEvent(damageEvent);
		damageEvent.getEntity().setLastDamageCause(damageEvent);
		to.damage(damage);
	}
	public void damage(Entity from, Damageable to, DamageCause cause, double damage) {
		EntityDamageEvent damageEvent = new EntityDamageEvent(to, cause, new EnumMap<DamageModifier, Double>(ImmutableMap.of(DamageModifier.BASE, damage)), new EnumMap<DamageModifier, Function<? super Double, Double>>(ImmutableMap.of(DamageModifier.BASE, Functions.constant(damage))));
		Bukkit.getPluginManager().callEvent(damageEvent);
		damageEvent.getEntity().setLastDamageCause(damageEvent);
		to.damage(damage, from);
	}
	
	//private
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
