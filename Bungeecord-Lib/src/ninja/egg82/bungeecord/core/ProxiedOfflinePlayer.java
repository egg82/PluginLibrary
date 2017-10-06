package ninja.egg82.bungeecord.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.utils.ReflectUtil;

public class ProxiedOfflinePlayer {
	//vars
	private UUID uuid = null;
	private String name = null;
	
	//constructor
	public ProxiedOfflinePlayer(String name) {
		if (uuid == null) {
			throw new ArgumentNullException("name");
		}
		
		this.name = name;
		fetchPlayerUuid(name);
	}
	public ProxiedOfflinePlayer(UUID uuid) {
		if (uuid == null) {
			throw new ArgumentNullException("uuid");
		}
		
		this.uuid = uuid;
		fetchPlayerName(uuid);
	}
	
	//public
	public boolean isOnline() {
		return (ProxyServer.getInstance().getPlayer(uuid) != null) ? true : false;
	}
	public ProxiedPlayer getPlayer() {
		return ProxyServer.getInstance().getPlayer(uuid);
	}
	
	public String getName() {
		return name;
	}
	public UUID getUniqueId() {
		return uuid;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!ReflectUtil.doesExtend(getClass(), obj.getClass())) {
			return false;
		}
		
		ProxiedOfflinePlayer p = (ProxiedOfflinePlayer) obj;
		if (uuid.equals(p.uuid) && name.equals(p.name)) {
			return true;
		}
		
		return false;
	}
	public int hashCode() {
		return new HashCodeBuilder(429045719, 2349829).append(uuid).append(name).toHashCode();
	}
	
	//private
	private void fetchPlayerName(UUID uuid) {
		IRegistry<UUID> offlinePlayerRegistry = ServiceLocator.getService(OfflinePlayerRegistry.class);
		IRegistry<String> offlinePlayerReverseRegistry = ServiceLocator.getService(OfflinePlayerReverseRegistry.class);
		
		name = offlinePlayerRegistry.getRegister(uuid, String.class);
		if (name != null) {
			return;
		}
		
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL("https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names").openConnection();
		} catch (Exception ex) {
			return;
		}
		
		conn.setDoInput(true);
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Connection", "close");
		conn.setRequestProperty("User-Agent", "egg82/BungeecordLibrary/ProxiedOfflinePlayer");
		
		try {
			conn.setRequestMethod("GET");
		} catch (Exception ex) {
			return;
		}
		
		try {
			int code = conn.getResponseCode();
			
			InputStream in = (code == 200) ? conn.getInputStream() : conn.getErrorStream();
			InputStreamReader reader = new InputStreamReader(in);
			BufferedReader buffer = new BufferedReader(reader);
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = buffer.readLine()) != null) {
				builder.append(line);
			}
			buffer.close();
			reader.close();
			in.close();
			
			if (code == 200) {
				JSONArray json = new JSONArray(builder.toString());
				JSONObject first = json.getJSONObject(0);
				name = first.getString("name");
			} else if (code == 204) {
				// No data exists
				name = "";
			}
		} catch (Exception ex) {
			return;
		}
		
		if (name != null) {
			offlinePlayerRegistry.setRegister(uuid, name);
			offlinePlayerReverseRegistry.setRegister(name, uuid);
		}
	}
	private void fetchPlayerUuid(String name) {
		IRegistry<UUID> offlinePlayerRegistry = ServiceLocator.getService(OfflinePlayerRegistry.class);
		IRegistry<String> offlinePlayerReverseRegistry = ServiceLocator.getService(OfflinePlayerReverseRegistry.class);
		
		uuid = offlinePlayerReverseRegistry.getRegister(name, UUID.class);
		if (uuid != null) {
			return;
		}
		
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
		} catch (Exception ex) {
			return;
		}
		
		conn.setDoInput(true);
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Connection", "close");
		conn.setRequestProperty("User-Agent", "egg82/BungeecordLibrary/ProxiedOfflinePlayer");
		
		try {
			conn.setRequestMethod("GET");
		} catch (Exception ex) {
			return;
		}
		
		try {
			int code = conn.getResponseCode();
			
			InputStream in = (code == 200) ? conn.getInputStream() : conn.getErrorStream();
			InputStreamReader reader = new InputStreamReader(in);
			BufferedReader buffer = new BufferedReader(reader);
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = buffer.readLine()) != null) {
				builder.append(line);
			}
			buffer.close();
			reader.close();
			in.close();
			
			if (code == 200) {
				JSONObject json = new JSONObject(builder.toString());
				uuid = UUID.fromString(json.getString("id").replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
			} else if (code == 204) {
				// No data exists
				uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
			}
		} catch (Exception ex) {
			return;
		}
		
		if (uuid != null) {
			offlinePlayerRegistry.setRegister(uuid, name);
			offlinePlayerReverseRegistry.setRegister(name, uuid);
		}
	}
}
