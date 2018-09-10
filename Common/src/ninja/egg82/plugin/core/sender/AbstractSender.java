package ninja.egg82.plugin.core.sender;

import java.util.UUID;

public abstract class AbstractSender implements Sender {
    // vars
    private String name = null;
    private UUID uuid = null;

    private boolean isOpped = false;
    private boolean isConsole = false;

    private Object handle = null;

    // constructor
    public AbstractSender(String name, UUID uuid, boolean isOpped, boolean isConsole, Object handle) {
        this.name = name;
        this.uuid = uuid;
        this.isOpped = isOpped;
        this.isConsole = isConsole;
        this.handle = handle;
    }

    // public
    public final String getName() {
        return name;
    }

    public final UUID getUuid() {
        return uuid;
    }

    public final boolean isOpped() {
        return isOpped;
    }

    public final boolean isConsole() {
        return isConsole;
    }

    public final Object getHandle() {
        return handle;
    }

    // private

}
