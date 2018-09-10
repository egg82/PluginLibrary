package ninja.egg82.bungeecord.reflection.redisBungee;

import java.net.InetAddress;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;

public class RedisBungeeHelper implements IRedisBungeeHelper {
    // vars
    private RedisBungeeAPI api = RedisBungee.getApi();

    // constructor
    public RedisBungeeHelper() {

    }

    // public
    public String getName(UUID playerUuid) {
        return getName(playerUuid, true);
    }

    public String getName(UUID playerUuid, boolean expensive) {
        if (playerUuid == null) {
            throw new IllegalArgumentException("playerUuid cannot be null.");
        }

        return api.getNameFromUuid(playerUuid, expensive);
    }

    public UUID getUuid(String playerName) {
        return getUuid(playerName, true);
    }

    public UUID getUuid(String playerName, boolean expensive) {
        if (playerName == null) {
            throw new IllegalArgumentException("playerName cannot be null.");
        }

        return api.getUuidFromName(playerName, expensive);
    }

    public InetAddress getIp(UUID playerUuid) {
        if (playerUuid == null) {
            throw new IllegalArgumentException("playerUuid cannot be null.");
        }

        return api.getPlayerIp(playerUuid);
    }

    public boolean isValidLibrary() {
        return true;
    }

    // private

}
