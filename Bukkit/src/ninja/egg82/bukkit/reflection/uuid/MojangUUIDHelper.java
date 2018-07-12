package ninja.egg82.bukkit.reflection.uuid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ninja.egg82.bukkit.core.PlayerInfoContainer;
import ninja.egg82.enums.ExpirationPolicy;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.ExpiringRegistry;
import ninja.egg82.patterns.registries.IRegistry;

public class MojangUUIDHelper implements IUUIDHelper {
	//vars
	private IRegistry<UUID, PlayerInfoContainer> uuidRegistry = new ExpiringRegistry<UUID, PlayerInfoContainer>(UUID.class, PlayerInfoContainer.class, 60L * 60L * 1000L, TimeUnit.MILLISECONDS, ExpirationPolicy.ACCESSED);
	private IRegistry<String, PlayerInfoContainer> nameRegistry = new ExpiringRegistry<String, PlayerInfoContainer>(String.class, PlayerInfoContainer.class, 60L * 60L * 1000L, TimeUnit.MILLISECONDS, ExpirationPolicy.ACCESSED);
	
	private JSONParser parser = new JSONParser();
	
	//constructor
	public MojangUUIDHelper() {
		
	}
	
	//public
	public PlayerInfoContainer getPlayer(UUID uuid) {
		PlayerInfoContainer info = uuidRegistry.getRegister(uuid);
		if (info == null) {
			info = fetchPlayerByUuid(uuid);
		}
		return info;
	}
	public boolean isCached(UUID uuid) {
		return uuidRegistry.hasRegister(uuid);
	}
	public PlayerInfoContainer getPlayer(String name) {
		PlayerInfoContainer info = nameRegistry.getRegister(name);
		if (info == null) {
			info = fetchPlayerByName(name);
		}
		return info;
	}
	public boolean isCached(String name) {
		return nameRegistry.hasRegister(name);
	}
	
	//private
	private PlayerInfoContainer fetchPlayerByUuid(UUID uuid) {
		HttpURLConnection conn = null;
		try {
			conn = getConnection("https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names");
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
					JSONArray json = (JSONArray) parser.parse(builder.toString());
					JSONObject last = (JSONObject) json.get(json.size() - 1);
					String name = (String) last.get("name");
					PlayerInfoContainer container = new PlayerInfoContainer(name, uuid);
					uuidRegistry.setRegister(uuid, container);
					nameRegistry.setRegister(name, container);
					return container;
				} else if (code == 204) {
					// No data exists
					uuidRegistry.setRegister(uuid, null);
					return null;
				}
			}
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
		}
		
		return null;
	}
	private PlayerInfoContainer fetchPlayerByName(String name) {
		HttpURLConnection conn = null;
		try {
			conn = getConnection("https://api.mojang.com/users/profiles/minecraft/" + name);
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
					JSONObject json = (JSONObject) parser.parse(builder.toString());
					UUID uuid = UUID.fromString(((String) json.get("id")).replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
					name = (String) json.get("name");
					PlayerInfoContainer container = new PlayerInfoContainer(name, uuid);
					uuidRegistry.setRegister(uuid, container);
					nameRegistry.setRegister(name, container);
					return container;
				} else if (code == 204) {
					// No data exists
					nameRegistry.setRegister(name, null);
					return null;
				}
			}
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
		}
		
		return null;
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
