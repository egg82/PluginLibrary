package ninja.egg82.bukkit.core;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class EntityData {
    // vars
    private Location location = null;
    private EntityType type = null;

    // constructor
    public EntityData(Entity e) {
        location = e.getLocation().clone();
        type = e.getType();
    }

    // public
    public Location getLocation() {
        return location.clone();
    }

    public EntityType getType() {
        return type;
    }

    // private

}
