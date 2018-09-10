package ninja.egg82.bukkit.core;

import java.util.UUID;

public class PlayerInfoContainer {
    // vars
    private String name = null;
    private UUID uuid = null;
    private long timeCreated = -1L;

    // constructor
    public PlayerInfoContainer(String name, UUID uuid, long timeCreated) {
        this.name = name;
        this.uuid = uuid;
        this.timeCreated = timeCreated;
    }

    // public
    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    // private

}
