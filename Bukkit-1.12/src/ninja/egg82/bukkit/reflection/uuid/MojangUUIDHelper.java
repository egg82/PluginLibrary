package ninja.egg82.bukkit.reflection.uuid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;

import ninja.egg82.bukkit.core.PlayerInfoContainer;
import ninja.egg82.enums.ExpirationPolicy;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.ExpiringRegistry;
import ninja.egg82.patterns.registries.IRegistry;
import ninja.egg82.plugin.utils.DirectoryUtil;
import ninja.egg82.plugin.utils.JSONUtil;
import ninja.egg82.utils.ThreadUtil;
import ninja.egg82.utils.TimeUtil;

public class MojangUUIDHelper implements IUUIDHelper {
	//vars
	private Gson gson = new Gson();
	
	private IRegistry<UUID, PlayerInfoContainer> uuidCache = new ExpiringRegistry<UUID, PlayerInfoContainer>(UUID.class, PlayerInfoContainer.class, 60L * 60L * 1000L, TimeUnit.MILLISECONDS, ExpirationPolicy.ACCESSED);
	private IRegistry<String, PlayerInfoContainer> nameCache = new ExpiringRegistry<String, PlayerInfoContainer>(String.class, PlayerInfoContainer.class, 60L * 60L * 1000L, TimeUnit.MILLISECONDS, ExpirationPolicy.ACCESSED);
	
	//constructor
	public MojangUUIDHelper() {
		
	}
	
	//public
	public PlayerInfoContainer getPlayer(UUID playerUuid, boolean expensive) {
		// Lookup from Bukkit
		Player player = Bukkit.getPlayer(playerUuid);
		if (player != null) {
			return new PlayerInfoContainer(player.getName(), player.getUniqueId(), System.currentTimeMillis());
		}
		
		// Lookup from in-memory cache
		PlayerInfoContainer retVal = uuidCache.getRegister(playerUuid);
		if (retVal != null) {
			if (expired(retVal)) {
				// Expired. Fetch possible new info in background
				ThreadUtil.submit(new Runnable() {
					public void run() {
						getInfoExpensive(playerUuid);
					}
				});
			}
			
			// Return current cached info
			return retVal;
		}
		
		File infoFile = getInfoFile(playerUuid);
		
		// Lookup from file cache
		if (infoFile.exists()) {
			try (FileReader reader = new FileReader(infoFile)) {
				retVal = gson.fromJson(reader, PlayerInfoContainer.class);
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			}
			
			if (retVal != null) {
				// Add current cached info to memory (at least until new info is fetched form network)
				uuidCache.setRegister(retVal.getUuid(), retVal);
				nameCache.setRegister(retVal.getName(), retVal);
				
				if (expired(retVal)) {
					// Expired. Fetch possible new info in background
					ThreadUtil.submit(new Runnable() {
						public void run() {
							getInfoExpensive(playerUuid);
						}
					});
				}
				
				// Return current cached info
				return retVal;
			}
		}
		
		if (!expensive) {
			// If not using an expensive lookup, fetch new info in background
			ThreadUtil.submit(new Runnable() {
				public void run() {
					getInfoExpensive(playerUuid);
				}
			});
		}
		
		// If not using an expensive lookup, return null. Otherwise fetch from network
		return (expensive) ? getInfoExpensive(playerUuid) : null;
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
			if (expired(retVal)) {
				// Expired. Fetch possible new info in background
				ThreadUtil.submit(new Runnable() {
					public void run() {
						getInfoExpensive(playerName);
					}
				});
			}
			
			// Return current cached info
			return retVal;
		}
		
		File infoFile = getInfoFile(playerName);
		
		// Lookup from file cache
		if (infoFile.exists()) {
			try (FileReader reader = new FileReader(infoFile)) {
				retVal = gson.fromJson(reader, PlayerInfoContainer.class);
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			}
			
			if (retVal != null) {
				// Add current cached info to memory (at least until new info is fetched form network)
				uuidCache.setRegister(retVal.getUuid(), retVal);
				nameCache.setRegister(retVal.getName(), retVal);
				
				if (expired(retVal)) {
					// Expired. Fetch possible new info in background
					ThreadUtil.submit(new Runnable() {
						public void run() {
							getInfoExpensive(playerName);
						}
					});
				}
				
				// Return current cached info
				return retVal;
			}
		}
		
		if (!expensive) {
			// If not using an expensive lookup, fetch new info in background
			ThreadUtil.submit(new Runnable() {
				public void run() {
					getInfoExpensive(playerName);
				}
			});
		}
		
		// If not using an expensive lookup, return null. Otherwise fetch from network
		return (expensive) ? getInfoExpensive(playerName) : null;
	}
	
	//private
	private PlayerInfoContainer getInfoExpensive(UUID playerUuid) {
		PlayerInfoContainer retVal = null;
		
		// Grab info from network/Mojang
		HttpURLConnection conn = null;
		try {
			conn = getConnection("https://api.mojang.com/user/profiles/" + playerUuid.toString().replace("-", "") + "/names");
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			return null;
		}
		
		try {
			int code = conn.getResponseCode();
			
			try (InputStream in = (code == 200) ? conn.getInputStream() : conn.getErrorStream(); InputStreamReader reader = new InputStreamReader(in); BufferedReader buffer = new BufferedReader(reader)) {
				StringBuilder builder = new StringBuilder();
				String line = null;
				while ((line = buffer.readLine()) != null) {
					builder.append(line);
				}
				
				if (code == 200) {
					JSONArray json = JSONUtil.parseArray(builder.toString());
					JSONObject last = (JSONObject) json.get(json.size() - 1);
					String name = (String) last.get("name");
					
					retVal = new PlayerInfoContainer(name, playerUuid, System.currentTimeMillis());
					writeToFileCache(retVal);
					uuidCache.setRegister(retVal.getUuid(), retVal);
					nameCache.setRegister(retVal.getName(), retVal);
				} else if (code == 204) {
					// No data exists
					uuidCache.setRegister(playerUuid, null);
				}
			}
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
		}
		
		return retVal;
	}
	private PlayerInfoContainer getInfoExpensive(String playerName) {
		PlayerInfoContainer retVal = null;
		
		// Grab info from network/Mojang
		HttpURLConnection conn = null;
		try {
			conn = getConnection("https://api.mojang.com/users/profiles/minecraft/" + playerName);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			return null;
		}
		
		try {
			int code = conn.getResponseCode();
			
			try (InputStream in = (code == 200) ? conn.getInputStream() : conn.getErrorStream(); InputStreamReader reader = new InputStreamReader(in); BufferedReader buffer = new BufferedReader(reader)) {
				StringBuilder builder = new StringBuilder();
				String line = null;
				while ((line = buffer.readLine()) != null) {
					builder.append(line);
				}
				
				if (code == 200) {
					JSONObject json = JSONUtil.parseObject(builder.toString());
					UUID uuid = UUID.fromString(((String) json.get("id")).replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
					String name = (String) json.get("name");
					
					retVal = new PlayerInfoContainer(name, uuid, System.currentTimeMillis());
					writeToFileCache(retVal);
					uuidCache.setRegister(retVal.getUuid(), retVal);
					nameCache.setRegister(retVal.getName(), retVal);
				} else if (code == 204) {
					// No data exists
					nameCache.setRegister(playerName, null);
				}
			}
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
		}
		
		return retVal;
	}
	
	private boolean expired(PlayerInfoContainer info) {
		return (info.getTimeCreated() <= System.currentTimeMillis() - TimeUtil.getTime("1day")) ? true : false;
	}
	
	private void writeToFileCache(PlayerInfoContainer info) {
		// UUID
		File uuidFile = getInfoFile(info.getUuid());
		if (uuidFile.exists()) {
			if (uuidFile.isDirectory()) {
				DirectoryUtil.delete(uuidFile);
			} else {
				uuidFile.delete();
			}
		}
		
		try (FileWriter writer = new FileWriter(uuidFile, false)) {
			gson.toJson(info, writer);
			writer.flush();
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
		}
		
		// Name
		File nameFile = getInfoFile(info.getName());
		if (nameFile.exists()) {
			if (nameFile.isDirectory()) {
				DirectoryUtil.delete(nameFile);
			} else {
				nameFile.delete();
			}
		}
		
		try (FileWriter writer = new FileWriter(nameFile, false)) {
			gson.toJson(info, writer);
			writer.flush();
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
		}
	}
	private File getInfoFile(UUID playerUuid) {
		// playerdata directory, where info is stored
		File uuidDir = new File(ServiceLocator.getService(Plugin.class).getDataFolder(), "UUIDData");
		if (uuidDir.exists() && !uuidDir.isDirectory()) {
			uuidDir.delete();
		}
		
		if (!uuidDir.exists()) {
			try {
				// Create the directory if needed
				uuidDir.mkdirs();
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			}
		}
		
		File retVal = new File(uuidDir, playerUuid.toString() + ".json");
		if (retVal.exists() && retVal.isDirectory()) {
			DirectoryUtil.delete(retVal);
		}
		
		return retVal;
	}
	private File getInfoFile(String playerName) {
		// playerdata directory, where info is stored
		File nameDir = new File(ServiceLocator.getService(Plugin.class).getDataFolder(), "NameData");
		if (nameDir.exists() && !nameDir.isDirectory()) {
			nameDir.delete();
		}
		
		if (!nameDir.exists()) {
			try {
				// Create the directory if needed
				nameDir.mkdirs();
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			}
		}
		
		File retVal = new File(nameDir, playerName + ".json");
		if (retVal.exists() && retVal.isDirectory()) {
			DirectoryUtil.delete(retVal);
		}
		
		return retVal;
	}
	
	private static HttpURLConnection getConnection(String url) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		
		conn.setDoInput(true);
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Connection", "close");
		conn.setRequestProperty("User-Agent", "egg82/PluginLibrary/MojangUUIDHelper");
		conn.setRequestMethod("GET");
		
		return conn;
	}
}
