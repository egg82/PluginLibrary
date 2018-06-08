package ninja.egg82.bungeecord.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ninja.egg82.bungeecord.core.ProxiedOfflinePlayer;
import ninja.egg82.utils.MathUtil;

public final class CommandUtil {
	//vars
	
	//constructor
	public CommandUtil() {
		
	}
	
	//public
	public static boolean isPlayer(CommandSender sender) {
		if (sender == null) {
			return false;
		}
		return (sender instanceof ProxiedPlayer) ? true : false;
	}
	
	public static boolean isArrayOfAllowedLength(Object[] arr, int... allowedLengths) {
		for (int i = 0; i < allowedLengths.length; i++) {
			if ((arr == null && allowedLengths[i] == 0) || (arr != null && arr.length == allowedLengths[i])) {
				return true;
			}
		}
		
		return false;
	}
	
	public static String getAtSymbolType(String symbol) {
		if (symbol == null || symbol.length() <= 1 || symbol.charAt(0) != '@') {
			return null;
		}
		
		symbol = symbol.trim().toLowerCase();
		
		if (symbol.charAt(1) == 'a') {
			return "a";
		} else if (symbol.charAt(1) == 'p') {
			return "p";
		} else if (symbol.charAt(1) == 'r') {
			return "r";
		} else if (symbol.charAt(1) == 'e') {
			return "e";
		}
		
		return null;
	}
	/**
	 * Please note that in Bungeecord this function is limited. The limitations are listed below:
	 * 1. Results are limited to ProxiedPlayers. Symbols and flags that target specific entities will be ignored.
	 * 2. Symbols and flags that target specific locations will be ignored.
	 * 3. Symbols and flags that target specific levels will be ignored.
	 * 4. Symbols and flags that target specific GameModes will be ignored.
	 * 5. Symbols and flags that target specific scoreboard teams or scores will be ignored.
	 * Everything else should work as expected. If you need these features, simply pass
	 * a command or channel to your plugin.
	 * 
	 * @param symbol The symbol string to parse
	 * @return A list of ProxiedPlayers returned by all servers matching the result
	 */
	public static List<ProxiedPlayer> parseAtSymbol(String symbol) {
		if (symbol == null || symbol.length() <= 1 || symbol.charAt(0) != '@') {
			return new ArrayList<ProxiedPlayer>();
		}
		
		symbol = symbol.trim().toLowerCase();
		
		if (symbol.charAt(1) == 'a') {
			return parseASymbol(symbol);
		} else if (symbol.charAt(1) == 'p') {
			return parsePSymbol(symbol);
		} else if (symbol.charAt(1) == 'r') {
			return parseRSymbol(symbol);
		} else if (symbol.charAt(1) == 'e') {
			return parseESymbol(symbol);
		}
		
		return new ArrayList<ProxiedPlayer>();
	}
	
	public static ProxiedPlayer getPlayerByName(String name) {
		if (name == null) {
			return null;
		}
		
		return ProxyServer.getInstance().getPlayer(name);
	}
	public static ProxiedPlayer getPlayerByUuid(String uuid) {
		return getPlayerByUuid(UUID.fromString(uuid));
	}
	public static ProxiedPlayer getPlayerByUuid(UUID uuid) {
		if (uuid == null) {
			return null;
		}
		
		return ProxyServer.getInstance().getPlayer(uuid);
	}
	
	public static ProxiedOfflinePlayer getOfflinePlayerByName(String name) {
		if (name == null) {
			return null;
		}
		
		return new ProxiedOfflinePlayer(name);
	}
	public static ProxiedOfflinePlayer getOfflinePlayerByUuid(String uuid) {
		return getOfflinePlayerByUuid(UUID.fromString(uuid));
	}
	public static ProxiedOfflinePlayer getOfflinePlayerByUuid(UUID uuid) {
		if (uuid == null) {
			return null;
		}
		
		return new ProxiedOfflinePlayer(uuid);
	}
	
	//private
	private static List<ProxiedPlayer> parseASymbol(String symbol) {
		//@a means ALL players
		
		int beginArgs = symbol.indexOf('[');
		int endArgs = symbol.indexOf(']');
		
		if (beginArgs != -1 && endArgs != -1) {
			// Get the specified args
			Map<String, String> args = getArguments(symbol.substring(beginArgs + 1, endArgs).trim());
			ArrayList<ProxiedPlayer> retVal = new ArrayList<ProxiedPlayer>(ProxyServer.getInstance().getPlayers());
			
			filter(retVal, args);
			
			return retVal;
		}
		
		// No args specified. Get ALL players
		return new ArrayList<ProxiedPlayer>(ProxyServer.getInstance().getPlayers());
	}
	private static List<ProxiedPlayer> parseESymbol(String symbol) {
		// @e means ALL entities
		
		int beginArgs = symbol.indexOf('[');
		int endArgs = symbol.indexOf(']');
		
		if (beginArgs != -1 && endArgs != -1) {
			// Get the specified args
			Map<String, String> args = getArguments(symbol.substring(beginArgs + 1, endArgs).trim());
			ArrayList<ProxiedPlayer> retVal = new ArrayList<ProxiedPlayer>(ProxyServer.getInstance().getPlayers());
			
			filter(retVal, args);
			
			return retVal;
		}
		
		// No args specified. Get EVERYTHING
		return new ArrayList<ProxiedPlayer>(ProxyServer.getInstance().getPlayers());
	}
	private static List<ProxiedPlayer> parsePSymbol(String symbol) {
		// @p means closest player (or entity with "type" argument set)
		
		int beginArgs = symbol.indexOf('[');
		int endArgs = symbol.indexOf(']');
		
		if (beginArgs != -1 && endArgs != -1) {
			// Get the specified args
			Map<String, String> args = getArguments(symbol.substring(beginArgs + 1, endArgs).trim());
			ArrayList<ProxiedPlayer> retVal = new ArrayList<ProxiedPlayer>(ProxyServer.getInstance().getPlayers());
			
			filter(retVal, args);
			
			return retVal;
		}
		
		// No args specified. Get closest player
		ProxiedPlayer closest = null;
		
		Collection<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers();
		if (!players.isEmpty()) {
			closest = players.iterator().next();
		}
		
		if (closest == null) {
			return new ArrayList<ProxiedPlayer>();
		}
		return new ArrayList<ProxiedPlayer>(Arrays.asList(closest));
	}
	private static List<ProxiedPlayer> parseRSymbol(String symbol) {
		// @r means random player (or entity with "type" argument set)
		
		int beginArgs = symbol.indexOf('[');
		int endArgs = symbol.indexOf(']');
		
		if (beginArgs != -1 && endArgs != -1) {
			// Get the specified args
			Map<String, String> args = getArguments(symbol.substring(beginArgs + 1, endArgs).trim());
			ArrayList<ProxiedPlayer> retVal = new ArrayList<ProxiedPlayer>(ProxyServer.getInstance().getPlayers());
			
			Collections.shuffle(retVal);
			
			filter(retVal, args);
			
			return retVal;
		}
		
		// No args specified. Get random player
		ArrayList<ProxiedPlayer> players = new ArrayList<ProxiedPlayer>(ProxyServer.getInstance().getPlayers());
		
		if (players.size() == 0) {
			return new ArrayList<ProxiedPlayer>();
		}
		return new ArrayList<ProxiedPlayer>(Arrays.asList(players.get(MathUtil.fairRoundedRandom(0, players.size()))));
	}
	
	private static Map<String, String> getArguments(String symbol) {
		HashMap<String, String> retVal = new HashMap<String, String>();
		String[] pairs = symbol.split(",");
		
		for (int i = 0; i < pairs.length; i++) {
			String[] pair = pairs[i].split("=");
			if (pair.length != 2) {
				continue;
			}
			retVal.put(pair[0].trim(), pair[1].trim());
		}
		
		return retVal;
	}
	private static void filter(List<ProxiedPlayer> list, Map<String, String> args) {
		int c = -1;
		boolean goodVal = true;
		try {
			c = Integer.parseInt(args.get("c"));
		} catch (Exception ex) {
			goodVal = true;
		}
		String name = args.get("name");
		
		ArrayList<ProxiedPlayer> removalList = new ArrayList<ProxiedPlayer>();
		for (ProxiedPlayer entity : list) {
			if (!eName(entity.getName(), name)) {
				removalList.add(entity);
				continue;
			}
		}
		list.removeAll(removalList);
		
		if (goodVal) {
			while (list.size() > c) {
				list.remove(list.size() - 1);
			}
		}
	}
	
	private static boolean eName(String entityName, String name) {
		if (name != null) {
			boolean flipped = (name.length() > 0 && name.charAt(0) == '!') ? true : false;
			
			if (flipped) {
				name = name.substring(1);
			}
			
			return (entityName.equalsIgnoreCase(name)) ? !flipped : flipped;
		}
		
		return true;
	}
}
