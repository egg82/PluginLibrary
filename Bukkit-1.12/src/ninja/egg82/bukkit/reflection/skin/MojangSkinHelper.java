package ninja.egg82.bukkit.reflection.skin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.SocketTimeoutException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.google.gson.Gson;

import ninja.egg82.analytics.exceptions.IExceptionHandler;
import ninja.egg82.bukkit.core.PlayerInfoContainer;
import ninja.egg82.bukkit.mineskin.MineskinClient;
import ninja.egg82.bukkit.mineskin.data.Skin;
import ninja.egg82.bukkit.mineskin.data.SkinCallback;
import ninja.egg82.bukkit.mineskin.data.SkinData;
import ninja.egg82.bukkit.mineskin.data.Texture;
import ninja.egg82.bukkit.reflection.material.IMaterialHelper;
import ninja.egg82.bukkit.reflection.skull.ISkullHelper;
import ninja.egg82.bukkit.reflection.uuid.IUUIDHelper;
import ninja.egg82.enums.ExpirationPolicy;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.ExpiringRegistry;
import ninja.egg82.patterns.registries.IRegistry;
import ninja.egg82.plugin.utils.DirectoryUtil;
import ninja.egg82.utils.ThreadUtil;
import ninja.egg82.utils.TimeUtil;

public class MojangSkinHelper implements ISkinHelper {
    // vars
    private Gson gson = new Gson();
    private MineskinClient client = new MineskinClient();

    private IRegistry<UUID, Skin> skinCache = new ExpiringRegistry<UUID, Skin>(UUID.class, Skin.class, 60L * 60L * 1000L, TimeUnit.MILLISECONDS, ExpirationPolicy.ACCESSED);

    private ISkullHelper skullHelper = ServiceLocator.getService(ISkullHelper.class);
    private IUUIDHelper uuidHelper = ServiceLocator.getService(IUUIDHelper.class);

    private Material skull = getSkull();

    // constructor
    public MojangSkinHelper() {

    }

    // public
    public ItemStack getSkull(UUID playerUuid) {
        return getSkull(playerUuid, 1, true);
    }

    public ItemStack getSkull(UUID playerUuid, boolean expensive) {
        return getSkull(playerUuid, 1, expensive);
    }

    public ItemStack getSkull(UUID playerUuid, int amount, boolean expensive) {
        if (amount < 1) {
            amount = 1;
        }

        ItemStack retVal = new ItemStack(skull, amount);
        retVal.setDurability((short) 3);

        if (playerUuid == null) {
            return retVal;
        }

        retVal = skullHelper.createSkull(playerUuid);
        retVal.setAmount(amount);

        PlayerInfoContainer player = uuidHelper.getPlayer(playerUuid, true);
        String name = (player != null) ? player.getName() : playerUuid.toString();

        ItemMeta meta = retVal.getItemMeta();
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(skull);
        }
        meta.setDisplayName(name);

        retVal.setItemMeta(meta);

        return retVal;
    }

    public Skin getSkin(UUID playerUuid, boolean expensive) {
        // Lookup from in-memory cache
        Skin retVal = skinCache.getRegister(playerUuid);
        if (retVal != null) {
            if (expired(retVal)) {
                // Expired. Fetch possible new skin in background
                ThreadUtil.submit(new Runnable() {
                    public void run() {
                        getSkinExpensive(playerUuid);
                    }
                });
            }

            // Return current cached skin
            return retVal;
        }

        File skinFile = getSkinFile(playerUuid);

        // Lookup from file cache
        if (skinFile.exists()) {
            try (FileReader reader = new FileReader(skinFile)) {
                retVal = gson.fromJson(reader, Skin.class);
            } catch (Exception ex) {
                IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                if (handler != null) {
                    handler.sendException(ex);
                }
            }

            if (retVal != null) {
                // Add current cached skin to memory (at least until new skin is fetched form
                // network)
                skinCache.setRegister(playerUuid, retVal);

                if (expired(retVal)) {
                    // Expired. Fetch possible new skin in background
                    ThreadUtil.submit(new Runnable() {
                        public void run() {
                            getSkinExpensive(playerUuid);
                        }
                    });
                }

                // Return current cached skin
                return retVal;
            }
        }

        if (!expensive) {
            // If not using an expensive lookup, fetch new skin in background
            ThreadUtil.submit(new Runnable() {
                public void run() {
                    getSkinExpensive(playerUuid);
                }
            });
        }

        // If not using an expensive lookup, return default skin. Otherwise fetch from
        // network
        return (expensive) ? getSkinExpensive(playerUuid) : getDefaultSkin();
    }

    // private
    private Skin getSkinExpensive(UUID playerUuid) {
        AtomicReference<Skin> retVal = new AtomicReference<Skin>(null);
        AtomicBoolean isDefault = new AtomicBoolean(false);

        // Grab skin from network/Mojang
        CountDownLatch latch = new CountDownLatch(1);

        client.generateUser(playerUuid, new SkinCallback() {
            public void done(Skin skin) {
                retVal.set(skin);
                latch.countDown();
            }

            public void error(String message) {
                RuntimeException ex = new RuntimeException(message);
                IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                if (handler != null) {
                    handler.sendException(ex);
                }
                latch.countDown();
                throw ex;
            }

            public void exception(Exception ex) {
                // Interpret a timeout as "no/default skin"
                if (ex instanceof SocketTimeoutException) {
                    isDefault.set(true);
                }

                RuntimeException ex2 = new RuntimeException("Could not fetch skin.", ex);
                IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                if (handler != null) {
                    handler.sendException(ex2);
                }
                latch.countDown();
                throw ex2;
            }
        });
        try {
            latch.await();
        } catch (Exception ex) {
            IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
            if (handler != null) {
                handler.sendException(ex);
            }
        }

        if (retVal.get() != null) {
            writeToFileCache(retVal.get(), playerUuid);
            skinCache.setRegister(playerUuid, retVal.get());
        }

        // Use default Steve/fallback skin
        if (retVal.get() == null) {
            retVal.set(getDefaultSkin());

            // If they're using no/default skin, cache it
            if (isDefault.get()) {
                writeToFileCache(retVal.get(), playerUuid);
                skinCache.setRegister(playerUuid, retVal.get());
            }
        }

        return retVal.get();
    }

    private static Skin getDefaultSkin() {
        Skin retVal = new Skin();
        retVal.id = 214324;
        retVal.name = "";
        retVal.data = new SkinData();
        retVal.data.uuid = UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7"); // Steve
        retVal.data.texture = new Texture();
        retVal.data.texture.value = "eyJ0aW1lc3RhbXAiOjE1MjY5MzE4NzU2MjAsInByb2ZpbGVJZCI6Ijg2NjdiYTcxYjg1YTQwMDRhZjU0NDU3YTk3MzRlZWQ3IiwicHJvZmlsZU5hbWUiOiJTdGV2ZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGMxYzc3Y2U4ZTU0OTI1YWI1ODEyNTQ0NmVjNTNiMGNkZDNkMGNhM2RiMjczZWI5MDhkNTQ4Mjc4N2VmNDAxNiJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc2N2Q0ODMyNWVhNTMyNDU2MTQwNmI4YzgyYWJiZDRlMjc1NWYxMTE1M2NkODVhYjA1NDVjYzIifX19";
        retVal.data.texture.signature = "Fc4JsPPtXUVjHCQt09enmbAvL5MN04WgoWMK5xaH6GDZdu9RN1ky3kQ9xOa9z/ARGzo3tiFZ9rwfZ8/Yz5lSwsJabNJybpSi+r1eFwKB2cc1h6Nx8sU6TXu4YhDkwC/fgoeNeHlluN4IaSMU+wTKK2mr0KO0EbTKxksGS4Okn5owAM10opW4IGsGTKEZ/4HvmEnt/8f4KZ2xiTXk35Bfw46d96gEu3REP0g6UhMJoOHVM5/+KRjkdCvTbXonUVy2KnyfHWvppnVBASp1Qf9m3zMWb3kT4qQ2te+Pz2sctNq5YqXuHx9gHg8eJ8ERKvSdVfG+rDR+4Q6dbYsCE0oh8eAgKm12CdI69IOQRl0DJmZn4lafi1wRsYB8Q1CBrLywW7iWX8k3oXEkVrzUCHEQFYQBV4xC6MthhJ5cRtjS/4vzWywE6yur+dQLulWKqzc5oCarpwAorGzdX4/KF1CXfzet0WZCgEbaM0xQr8a4vzk9K9KJg80kdVfU2M1+RIeFXBrypYOH0XYX94L+avfSGOFawilJdVJNETMU6Slvb3pgPVfed6DGffHAfG53bDN6RzVS1hqY0M3T9iDPG687ejrlK3cefL9uKn0F6qebF0ufNmGbJQkTDeLJ03JTxgTbqFYo4CLbni6TrjUR0LB08xB/pEZdgwoH/OVHYxzYZG4=";
        retVal.data.texture.url = "http://textures.minecraft.net/texture/dc1c77ce8e54925ab58125446ec53b0cdd3d0ca3db273eb908d5482787ef4016";
        retVal.timestamp = System.currentTimeMillis();
        retVal.prvate = false;
        retVal.views = 1;
        retVal.nextRequest = 0.0d;
        return retVal;
    }

    private boolean expired(Skin skin) {
        return (skin.timestamp * 1000L <= System.currentTimeMillis() - TimeUtil.getTime("1day")) ? true : false;
    }

    private void writeToFileCache(Skin skin, UUID playerUuid) {
        File skinFile = getSkinFile(playerUuid);
        if (skinFile.exists()) {
            if (skinFile.isDirectory()) {
                DirectoryUtil.delete(skinFile);
            } else {
                skinFile.delete();
            }
        }

        try (FileWriter writer = new FileWriter(skinFile, false)) {
            gson.toJson(skin, writer);
            writer.flush();
        } catch (Exception ex) {
            IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
            if (handler != null) {
                handler.sendException(ex);
            }
        }
    }

    private File getSkinFile(UUID playerUuid) {
        // playerdata directory, where skins are stored
        File skinDir = new File(ServiceLocator.getService(Plugin.class).getDataFolder(), "SkinData");
        if (skinDir.exists() && !skinDir.isDirectory()) {
            skinDir.delete();
        }

        if (!skinDir.exists()) {
            try {
                // Create the directory if needed
                skinDir.mkdirs();
            } catch (Exception ex) {
                IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                if (handler != null) {
                    handler.sendException(ex);
                }
            }
        }

        File retVal = new File(skinDir, playerUuid.toString() + ".json");
        if (retVal.exists() && retVal.isDirectory()) {
            DirectoryUtil.delete(retVal);
        }

        return retVal;
    }

    private Material getSkull() {
        IMaterialHelper materialHelper = ServiceLocator.getService(IMaterialHelper.class);
        Material retVal = materialHelper.getByName("PLAYER_HEAD");
        return (retVal != null) ? retVal : materialHelper.getByName("SKULL_ITEM");
    }
}
