package ninja.egg82.plugin.reflection.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

import ninja.egg82.exceptions.ArgumentNullException;

@SuppressWarnings("deprecation")
public final class EntityHelper_1_8 implements IEntityHelper {
	//vars
	
	//constructor
	public EntityHelper_1_8() {
		
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
	}
	public void removePassenger(Entity bottom, Entity top) {
		if (bottom == null) {
			throw new ArgumentNullException("bottom");
		}
		if (top == null) {
			throw new ArgumentNullException("top");
		}
		
		bottom.eject();
	}
	public void removeAllPassengers(Entity bottom) {
		if (bottom == null) {
			throw new ArgumentNullException("bottom");
		}
		
		bottom.eject();
	}
	public List<Entity> getPassengers(Entity bottom) {
		if (bottom == null) {
			throw new ArgumentNullException("bottom");
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
}
