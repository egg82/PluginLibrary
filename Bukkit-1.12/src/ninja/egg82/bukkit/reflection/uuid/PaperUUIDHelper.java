package ninja.egg82.bukkit.reflection.uuid;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.destroystokyo.paper.profile.PlayerProfile;

import ninja.egg82.bukkit.core.PlayerInfoContainer;
import ninja.egg82.enums.ExpirationPolicy;
import ninja.egg82.patterns.registries.ExpiringRegistry;
import ninja.egg82.patterns.registries.IRegistry;
import ninja.egg82.utils.ThreadUtil;

public class PaperUUIDHelper implements IUUIDHelper {
    // vars
    private IRegistry<UUID, PlayerInfoContainer> uuidCache = new ExpiringRegistry<UUID, PlayerInfoContainer>(UUID.class, PlayerInfoContainer.class, 60L * 60L * 1000L, TimeUnit.MILLISECONDS,
        ExpirationPolicy.ACCESSED);
    private IRegistry<String, PlayerInfoContainer> nameCache = new ExpiringRegistry<String, PlayerInfoContainer>(String.class, PlayerInfoContainer.class, 60L * 60L * 1000L, TimeUnit.MILLISECONDS,
        ExpirationPolicy.ACCESSED);

    // constructor
    public PaperUUIDHelper() {

    }

    // public
    public PlayerInfoContainer getPlayer(UUID playerUuid, boolean expensive) {
        // Lookup from Bukkit
        Player player = Bukkit.getPlayer(playerUuid);
        if (player != null) {
            return new PlayerInfoContainer(player.getName(), player.getUniqueId(), System.currentTimeMillis());
        }

        // Lookup from in-memory cache
        PlayerInfoContainer retVal = uuidCache.getRegister(playerUuid);
        if (retVal != null) {
            // Fetch possible new info in background
            ThreadUtil.submit(new Runnable() {
                public void run() {
                    getInfo(playerUuid, expensive);
                }
            });

            // Return current cached info
            return retVal;
        }

        return getInfo(playerUuid, expensive);
    }

    public PlayerInfoContainer getPlayer(String playerName, boolean expensive) {
        // Lookup from Bukkit
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) {
            return new PlayerInfoContainer(player.getName(), player.getUniqueId(), System.currentTimeMillis());
        }

        // Lookup from in-memory cache
        PlayerInfoContainer retVal = nameCache.getRegister(playerName);
        if (retVal != null) {
            // Fetch possible new info in background
            ThreadUtil.submit(new Runnable() {
                public void run() {
                    getInfo(playerName, expensive);
                }
            });

            // Return current cached info
            return retVal;
        }

        return getInfo(playerName, expensive);
    }

    // private
    private PlayerInfoContainer getInfo(UUID playerUuid, boolean expensive) {
        PlayerInfoContainer retVal = null;

        // Create profile
        PlayerProfile profile = Bukkit.createProfile(playerUuid);
        // Grab info from cache
        if (profile.isComplete() || profile.completeFromCache()) {
            retVal = new PlayerInfoContainer(profile.getName(), profile.getId(), System.currentTimeMillis());
            uuidCache.setRegister(profile.getId(), retVal);
            nameCache.setRegister(profile.getName(), retVal);
            return retVal;
        }

        if (!expensive) {
            // If not using an expensive lookup, fetch new info in background
            ThreadUtil.submit(new Runnable() {
                public void run() {
                    getInfo(playerUuid, true);
                }
            });
            return null;
        }

        // Grab info from network/Mojang
        if (profile.complete(false)) {
            retVal = new PlayerInfoContainer(profile.getName(), profile.getId(), System.currentTimeMillis());
            uuidCache.setRegister(profile.getId(), retVal);
            nameCache.setRegister(profile.getName(), retVal);
            return retVal;
        }

        // Could not get data
        return null;
    }

    private PlayerInfoContainer getInfo(String playerName, boolean expensive) {
        PlayerInfoContainer retVal = null;

        // Create profile
        PlayerProfile profile = Bukkit.createProfile(playerName);
        // Grab info from cache
        if (profile.isComplete() || profile.completeFromCache()) {
            retVal = new PlayerInfoContainer(profile.getName(), profile.getId(), System.currentTimeMillis());
            uuidCache.setRegister(profile.getId(), retVal);
            nameCache.setRegister(profile.getName(), retVal);
            return retVal;
        }

        if (!expensive) {
            // If not using an expensive lookup, fetch new info in background
            ThreadUtil.submit(new Runnable() {
                public void run() {
                    getInfo(playerName, true);
                }
            });
            return null;
        }

        // Grab info from network/Mojang
        if (profile.complete(false)) {
            retVal = new PlayerInfoContainer(profile.getName(), profile.getId(), System.currentTimeMillis());
            uuidCache.setRegister(profile.getId(), retVal);
            nameCache.setRegister(profile.getName(), retVal);
            return retVal;
        }

        // Could not get data
        return null;
    }
}
