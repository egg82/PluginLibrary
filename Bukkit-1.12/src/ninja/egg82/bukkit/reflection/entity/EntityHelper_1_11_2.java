package ninja.egg82.bukkit.reflection.entity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

public final class EntityHelper_1_11_2 implements IEntityHelper {
    // vars

    // constructor
    public EntityHelper_1_11_2() {

    }

    // public
    public void addPassenger(Entity bottom, Entity top) {
        if (bottom == null) {
            throw new IllegalArgumentException("bottom cannot be null.");
        }
        if (top == null) {
            throw new IllegalArgumentException("top cannot be null.");
        }

        bottom.addPassenger(top);
    }

    public void removePassenger(Entity bottom, Entity top) {
        if (bottom == null) {
            throw new IllegalArgumentException("bottom cannot be null.");
        }
        if (top == null) {
            throw new IllegalArgumentException("top cannot be null.");
        }

        bottom.removePassenger(top);
    }

    public void removeAllPassengers(Entity bottom) {
        if (bottom == null) {
            throw new IllegalArgumentException("bottom cannot be null.");
        }

        bottom.eject();
    }

    public List<Entity> getPassengers(Entity bottom) {
        if (bottom == null) {
            throw new IllegalArgumentException("bottom cannot be null.");
        }

        return new ArrayList<Entity>(bottom.getPassengers());
    }

    // There is literally no other way to do this right now. Everything is
    // deprecated.
    // When a future version of Bukkit comes out and has a non-deprecated way, a new
    // class will be made for that version
    public void damage(Damageable to, DamageCause cause, double damage) {
        Double d = Double.valueOf(damage);
        EntityDamageEvent damageEvent = new EntityDamageEvent(to, cause, new EnumMap<DamageModifier, Double>(ImmutableMap.of(DamageModifier.BASE, d)),
            new EnumMap<DamageModifier, Function<? super Double, Double>>(ImmutableMap.of(DamageModifier.BASE, Functions.constant(d))));
        Bukkit.getPluginManager().callEvent(damageEvent);
        damageEvent.getEntity().setLastDamageCause(damageEvent);
        to.damage(damage);
    }

    public void damage(Entity from, Damageable to, DamageCause cause, double damage) {
        Double d = Double.valueOf(damage);
        EntityDamageEvent damageEvent = new EntityDamageEvent(to, cause, new EnumMap<DamageModifier, Double>(ImmutableMap.of(DamageModifier.BASE, d)),
            new EnumMap<DamageModifier, Function<? super Double, Double>>(ImmutableMap.of(DamageModifier.BASE, Functions.constant(d))));
        Bukkit.getPluginManager().callEvent(damageEvent);
        damageEvent.getEntity().setLastDamageCause(damageEvent);
        to.damage(damage, from);
    }

    public ItemStack getItemInMainHand(Entity entity) {
        if (entity == null) {
            return null;
        }
        if (!(entity instanceof LivingEntity)) {
            return null;
        }

        EntityEquipment equipment = ((LivingEntity) entity).getEquipment();

        return equipment.getItemInMainHand();
    }

    public void setItemInMainHand(Entity entity, ItemStack item) {
        if (entity == null) {
            throw new IllegalArgumentException("entity cannot be null.");
        }
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null.");
        }
        if (!(entity instanceof LivingEntity)) {
            throw new IllegalArgumentException("entity cannot have items equipped.");
        }

        EntityEquipment equipment = ((LivingEntity) entity).getEquipment();
        equipment.setItemInMainHand(item);
    }

    public ItemStack getItemInOffHand(Entity entity) {
        if (entity == null) {
            return null;
        }
        if (!(entity instanceof LivingEntity)) {
            return null;
        }

        EntityEquipment equipment = ((LivingEntity) entity).getEquipment();

        return equipment.getItemInOffHand();
    }

    public void setItemInOffHand(Entity entity, ItemStack item) {
        if (entity == null) {
            throw new IllegalArgumentException("entity cannot be null.");
        }
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null.");
        }
        if (!(entity instanceof LivingEntity)) {
            throw new IllegalArgumentException("entity cannot have items equipped.");
        }

        EntityEquipment equipment = ((LivingEntity) entity).getEquipment();
        equipment.setItemInOffHand(item);
    }

    // private

}
