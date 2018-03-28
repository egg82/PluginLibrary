package ninja.egg82.bungeecord.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ninja.egg82.bungeecord.reflection.offlineplayer.IRedisBungeeHelper;
import ninja.egg82.enums.ExpirationPolicy;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.ExpiringRegistry;
import ninja.egg82.patterns.registries.IRegistry;
import ninja.egg82.utils.ReflectUtil;

public class ProxiedOfflinePlayer {
	//vars
	private static IRegistry<UUID, String> uuidToNameRegistry = new ExpiringRegistry<UUID, String>(UUID.class, String.class, 300L * 1000L, TimeUnit.MILLISECONDS, ExpirationPolicy.ACCESSED);
	private static IRegistry<String, UUID> nameToUuidRegistry = new ExpiringRegistry<String, UUID>(String.class, UUID.class, 300L * 1000L, TimeUnit.MILLISECONDS, ExpirationPolicy.ACCESSED);
	
	private IRedisBungeeHelper helper = ServiceLocator.getService(IRedisBungeeHelper.class);
	
	private UUID uuid = null;
	private String name = null;
	
	//constructor
	public ProxiedOfflinePlayer(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		this.name = name;
		this.uuid = nameToUuidRegistry.getRegister(name.toLowerCase());
		
		if (uuid == null) {
			uuid = helper.getUuid(name.toLowerCase());
		}
		if (uuid == null) {
			uuid = fetchPlayerUuid(name);
		}
		if (uuid != null) {
			nameToUuidRegistry.setRegister(name.toLowerCase(), uuid);
			uuidToNameRegistry.setRegister(uuid, name);
		}
	}
	public ProxiedOfflinePlayer(UUID uuid) {
		if (uuid == null) {
			throw new ArgumentNullException("uuid");
		}
		
		this.uuid = uuid;
		name = uuidToNameRegistry.getRegister(uuid);
		
		if (name == null) {
			name = helper.getName(uuid);
		}
		if (name == null) {
			name = fetchPlayerName(uuid);
		}
		if (name != null) {
			nameToUuidRegistry.setRegister(name.toLowerCase(), uuid);
			uuidToNameRegistry.setRegister(uuid, name);
		}
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
		final String n = p.name;
		final UUID u = p.uuid;
		
		if (
			((n == null && name == null) || (n != null && n.equals(name)))
			&& ((u == null && uuid == null) || (u != null && u.equals(uuid)))
		) {
			return true;
		}
		
		return false;
	}
	public int hashCode() {
		return new HashCodeBuilder(429045719, 2349829).append(uuid).append(name).toHashCode();
	}
	
	//private
	private static String fetchPlayerName(UUID uuid) {
		HttpURLConnection conn = null;
		try {
			conn = getConnection("https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names");
		} catch (Exception ex) {
			return null;
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
				return first.getString("name");
			} else if (code == 204) {
				// No data exists
				return "";
			}
		} catch (Exception ex) {
			
		}
		
		return null;
	}
	private static UUID fetchPlayerUuid(String name) {
		HttpURLConnection conn = null;
		try {
			conn = getConnection("https://api.mojang.com/users/profiles/minecraft/" + name);
		} catch (Exception ex) {
			return null;
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
				return UUID.fromString(json.getString("id").replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
			} else if (code == 204) {
				// No data exists
				return new UUID(0L, 0L);
			}
		} catch (Exception ex) {
			
		}
		
		return null;
	}
	
	private static HttpURLConnection getConnection(String url) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		
		conn.setDoInput(true);
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Connection", "close");
		conn.setRequestProperty("User-Agent", "egg82/BungeecordLibrary/ProxiedOfflinePlayer");
		conn.setRequestMethod("GET");
		
		return conn;
	}
}
