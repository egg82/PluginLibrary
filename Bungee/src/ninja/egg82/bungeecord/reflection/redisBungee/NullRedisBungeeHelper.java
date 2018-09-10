package ninja.egg82.bungeecord.reflection.redisBungee;

import java.net.InetAddress;
import java.util.UUID;

public class NullRedisBungeeHelper implements IRedisBungeeHelper {
    // vars

    // constructor
    public NullRedisBungeeHelper() {

    }

    // public
    public String getName(UUID playerUuid) {
        return null;
    }

    public String getName(UUID playerUuid, boolean expensive) {
        return null;
    }

    public UUID getUuid(String playerName) {
        return null;
    }

    public UUID getUuid(String playerName, boolean expensive) {
        return null;
    }

    public InetAddress getIp(UUID playerUuid) {
        return null;
    }

    public boolean isValidLibrary() {
        return false;
    }

    // private

}
