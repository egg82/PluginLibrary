package ninja.egg82.bukkit.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import ninja.egg82.primitive.ints.Object2IntArrayMap;
import ninja.egg82.primitive.ints.Object2IntMap;
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
		return (sender instanceof Player) ? true : false;
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
	public static List<Entity> parseAtSymbol(String symbol, Location commandLocation) {
		if (commandLocation == null) {
			commandLocation = new Location(Bukkit.getWorlds().get(0), 0.0d, 0.0d, 0.0d);
		}
		
		if (symbol == null || symbol.length() <= 1 || symbol.charAt(0) != '@') {
			return new ArrayList<Entity>();
		}
		
		symbol = symbol.trim().toLowerCase();
		
		if (symbol.charAt(1) == 'a') {
			return parseASymbol(symbol, commandLocation);
		} else if (symbol.charAt(1) == 'p') {
			return parsePSymbol(symbol, commandLocation);
		} else if (symbol.charAt(1) == 'r') {
			return parseRSymbol(symbol, commandLocation);
		} else if (symbol.charAt(1) == 'e') {
			return parseESymbol(symbol, commandLocation);
		}
		
		return new ArrayList<Entity>();
	}
	public static List<Player> getPlayers(List<Entity> list) {
		ArrayList<Player> retVal = new ArrayList<Player>();
		
		if (list == null || list.size() == 0) {
			return retVal;
		}
		
		for (Entity e : list) {
			if (e instanceof Player) {
				retVal.add((Player) e);
			}
		}
		
		return retVal;
	}
	public static List<Entity> stripPlayers(List<Entity> list) {
		ArrayList<Entity> retVal = new ArrayList<Entity>();
		
		if (list == null || list.size() == 0) {
			return retVal;
		}
		
		for (Entity e : list) {
			if (!(e instanceof Player)) {
				retVal.add(e);
			}
		}
		
		return retVal;
	}
	
	/**
	 * This function is super hacky and might cause some performance issues if called too often.
	 * 
	 * @param sender The actual command sender (the one with permissions to execute the command)
	 * @param playerLocation The location to execute the command at
	 * @param command The command to execute
	 */
	public static void dispatchCommandAtSenderLocation(CommandSender sender, CommandSender senderForLocation, String command) {
		if (sender == null || senderForLocation == null || command == null || command.length() == 0) {
			return;
		}
		
		// The reason we're not testing to see if the playerLocation can run the command
		// is because some plugins don't handle permissions that way.
		// This way is slower by a country mile, but guaranteed the result the sender wants.
		
		// TODO: finish
		/*if (sender.isOp()) {
			boolean backup = senderForLocation.isOp();
			senderForLocation.setOp(true);
			senderForLocation.recalculatePermissions();
			Bukkit.dispatchCommand(senderForLocation, command);
			senderForLocation.setOp(backup);
			senderForLocation.recalculatePermissions();
		} else {*/
			HashSet<PermissionAttachmentInfo> backup = new HashSet<PermissionAttachmentInfo>(senderForLocation.getEffectivePermissions());
			senderForLocation.getEffectivePermissions().addAll(sender.getEffectivePermissions());
			senderForLocation.recalculatePermissions();
			Bukkit.dispatchCommand(senderForLocation, command);
			senderForLocation.getEffectivePermissions().clear();
			senderForLocation.getEffectivePermissions().addAll(backup);
			senderForLocation.recalculatePermissions();
		//}
	}
	
	public static Player getPlayerByName(String name) {
		return getPlayerByName(name, true);
	}
	@SuppressWarnings("deprecation")
	public static Player getPlayerByName(String name, boolean exact) {
		if (name == null) {
			return null;
		}
		
		Player p = Bukkit.getPlayerExact(name);
		if (p == null && !exact) {
			p = Bukkit.getPlayer(name);
		}
		return p;
	}
	public static Player getPlayerByUuid(String uuid) {
		return getPlayerByUuid(UUID.fromString(uuid));
	}
	public static Player getPlayerByUuid(UUID uuid) {
		if (uuid == null) {
			return null;
		}
		
		return Bukkit.getPlayer(uuid);
	}
	
	@SuppressWarnings("deprecation")
	public static OfflinePlayer getOfflinePlayerByName(String name) {
		if (name == null) {
			return null;
		}
		
		return Bukkit.getOfflinePlayer(name);
	}
	public static OfflinePlayer getOfflinePlayerByUuid(String uuid) {
		return getOfflinePlayerByUuid(UUID.fromString(uuid));
	}
	public static OfflinePlayer getOfflinePlayerByUuid(UUID uuid) {
		if (uuid == null) {
			return null;
		}
		
		return Bukkit.getOfflinePlayer(uuid);
	}
	
	//private
	private static List<Entity> parseASymbol(String symbol, Location loc) {
		//@a means ALL players
		
		int beginArgs = symbol.indexOf('[');
		int endArgs = symbol.indexOf(']');
		
		if (beginArgs != -1 && endArgs != -1) {
			// Get the specified args
			Map<String, String> args = getArguments(symbol.substring(beginArgs + 1, endArgs).trim());
			ArrayList<Entity> retVal = new ArrayList<Entity>(Bukkit.getOnlinePlayers());
			
			filter(retVal, loc, args, false);
			
			return retVal;
		}
		
		// No args specified. Get ALL players
		return new ArrayList<Entity>(Bukkit.getOnlinePlayers());
	}
	private static List<Entity> parseESymbol(String symbol, Location loc) {
		// @e means ALL entities
		
		int beginArgs = symbol.indexOf('[');
		int endArgs = symbol.indexOf(']');
		
		if (beginArgs != -1 && endArgs != -1) {
			// Get the specified args
			Map<String, String> args = getArguments(symbol.substring(beginArgs + 1, endArgs).trim());
			ArrayList<Entity> retVal = new ArrayList<Entity>();
			
			for (World w : Bukkit.getWorlds()) {
				retVal.addAll(w.getEntities());
			}
			
			filter(retVal, loc, args, false);
			
			return retVal;
		}
		
		// No args specified. Get EVERYTHING
		ArrayList<Entity> retVal = new ArrayList<Entity>();
		
		for (World w : Bukkit.getWorlds()) {
			retVal.addAll(w.getEntities());
		}
		
		return retVal;
	}
	private static List<Entity> parsePSymbol(String symbol, Location loc) {
		// @p means closest player (or entity with "type" argument set)
		
		int beginArgs = symbol.indexOf('[');
		int endArgs = symbol.indexOf(']');
		
		if (beginArgs != -1 && endArgs != -1) {
			// Get the specified args
			Map<String, String> args = getArguments(symbol.substring(beginArgs + 1, endArgs).trim());
			ArrayList<Entity> retVal = new ArrayList<Entity>();
			
			for (World w : Bukkit.getWorlds()) {
				retVal.addAll(w.getEntities());
			}
			
			retVal.sort(new Comparator<Entity>() {
				public int compare(Entity a, Entity b) {
					return Double.compare(a.getLocation().distanceSquared(loc), b.getLocation().distanceSquared(loc));
				}
			});
			
			filter(retVal, loc, args, true);
			
			return retVal;
		}
		
		// No args specified. Get closest player
		Player closest = null;
		double closestDistance = Double.MAX_VALUE;
		for (Player p : loc.getWorld().getPlayers()) {
			if (p.getLocation().distanceSquared(loc) < closestDistance) {
				closest = p;
				closestDistance = p.getLocation().distanceSquared(loc);
			}
		}
		
		if (closest == null) {
			return new ArrayList<Entity>();
		}
		
		return new ArrayList<Entity>(Arrays.asList(closest));
	}
	private static List<Entity> parseRSymbol(String symbol, Location loc) {
		// @r means random player (or entity with "type" argument set)
		
		int beginArgs = symbol.indexOf('[');
		int endArgs = symbol.indexOf(']');
		
		if (beginArgs != -1 && endArgs != -1) {
			// Get the specified args
			Map<String, String> args = getArguments(symbol.substring(beginArgs + 1, endArgs).trim());
			ArrayList<Entity> retVal = new ArrayList<Entity>();
			
			for (World w : Bukkit.getWorlds()) {
				retVal.addAll(w.getEntities());
			}
			
			Collections.shuffle(retVal);
			
			filter(retVal, loc, args, true);
			
			return retVal;
		}
		
		// No args specified. Get random player
		ArrayList<Entity> players = new ArrayList<Entity>(Bukkit.getOnlinePlayers());
		
		if (players.size() == 0) {
			return new ArrayList<Entity>();
		}
		return new ArrayList<Entity>(Arrays.asList(players.get(MathUtil.fairRoundedRandom(0, players.size()))));
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
	private static void filter(List<Entity> list, Location commandLocation, Map<String, String> args, boolean onlyPlayersWithoutType) {
		int x = -1;
		boolean goodX = true;
		try {
			x = Integer.parseInt(args.get("x"));
		} catch (Exception ex) {
			goodX = false;
		}
		int y = -1;
		boolean goodY = true;
		try {
			y = Integer.parseInt(args.get("y"));
		} catch (Exception ex) {
			goodY = false;
		}
		int z = -1;
		boolean goodZ = true;
		try {
			z = Integer.parseInt(args.get("z"));
		} catch (Exception ex) {
			goodZ = false;
		}
		
		ArrayList<Location> worldLocs = new ArrayList<Location>();
		if (goodX && goodY && goodZ) {
			for (World w : Bukkit.getWorlds()) {
				worldLocs.add(new Location(w, x, y, z));
			}
		} else {
			for (World w : Bukkit.getWorlds()) {
				worldLocs.add(new Location(w, commandLocation.getX(), commandLocation.getY(), commandLocation.getZ()));
			}
		}
		
		int r = -1;
		boolean goodR = true;
		try {
			r = Integer.parseInt(args.get("r"));
		} catch (Exception ex) {
			goodR = false;
		}
		int rm = -1;
		boolean goodRM = true;
		try {
			rm = Integer.parseInt(args.get("rm"));
		} catch (Exception ex) {
			goodRM = false;
		}
		
		int dx = -1;
		boolean goodDX = true;
		try {
			dx = Integer.parseInt(args.get("dx"));
		} catch (Exception ex) {
			goodDX = false;
		}
		int dy = -1;
		boolean goodDY = true;
		try {
			dy = Integer.parseInt(args.get("dy"));
		} catch (Exception ex) {
			goodDY = false;
		}
		int dz = -1;
		boolean goodDZ = true;
		try {
			dz = Integer.parseInt(args.get("dz"));
		} catch (Exception ex) {
			goodDZ = false;
		}
		
		Object2IntMap<String> maxScore = new Object2IntArrayMap<String>();
		Object2IntMap<String> minScore = new Object2IntArrayMap<String>();
		for (Entry<String, String> kvp : args.entrySet()) {
			if (kvp.getKey().startsWith("score_") && kvp.getKey().endsWith("_min")) {
				try {
					minScore.put(kvp.getKey().substring(6, kvp.getKey().length() - 4), Integer.parseInt(kvp.getValue()));
				} catch (Exception ex) {
					
				}
			} else if (kvp.getKey().startsWith("score_")) {
				try {
					maxScore.put(kvp.getKey().substring(6), Integer.parseInt(kvp.getValue()));
				} catch (Exception ex) {
					
				}
			}
		}
		String tag = args.get("tag");
		String team = args.get("team");
		Set<Objective> objectives = Bukkit.getScoreboardManager().getMainScoreboard().getObjectives();
		Set<Team> teams = Bukkit.getScoreboardManager().getMainScoreboard().getTeams();
		
		int c = -1;
		boolean goodC = true;
		try {
			c = Integer.parseInt(args.get("c"));
		} catch (Exception ex) {
			goodC = false;
		}
		int l = -1;
		boolean goodL = true;
		try {
			l = Integer.parseInt(args.get("l"));
		} catch (Exception ex) {
			goodL = false;
		}
		int lm = -1;
		boolean goodLM = true;
		try {
			lm = Integer.parseInt(args.get("lm"));
		} catch (Exception ex) {
			goodLM = false;
		}
		String m = args.get("m");
		int mInt = -1;
		boolean goodM = true;
		try {
			mInt = Integer.parseInt(m);
		} catch (Exception ex) {
			goodM = false;
		}
		String name = args.get("name");
		int rx = -1;
		boolean goodRX = true;
		try {
			rx = Integer.parseInt(args.get("rx"));
		} catch (Exception ex) {
			goodRX = false;
		}
		int rxm = -1;
		boolean goodRXM = true;
		try {
			rxm = Integer.parseInt(args.get("rxm"));
		} catch (Exception ex) {
			goodRXM = false;
		}
		int ry = -1;
		boolean goodRY = true;
		try {
			ry = Integer.parseInt(args.get("ry"));
		} catch (Exception ex) {
			goodRY = false;
		}
		int rym = -1;
		boolean goodRYM = true;
		try {
			rym = Integer.parseInt(args.get("rym"));
		} catch (Exception ex) {
			goodRYM = false;
		}
		String type = args.get("type");
		
		ArrayList<Entity> removalList = new ArrayList<Entity>();
		for (Entity entity : list) {
			if (!volume(entity.getLocation().getBlockX(), commandLocation.getBlockX(), x, goodX, dx, goodDX)) {
				removalList.add(entity);
				continue;
			}
			if (!volume(entity.getLocation().getBlockY(), commandLocation.getBlockY(), y, goodY, dy, goodDY)) {
				removalList.add(entity);
				continue;
			}
			if (!volume(entity.getLocation().getBlockZ(), commandLocation.getBlockZ(), z, goodZ, dz, goodDZ)) {
				removalList.add(entity);
				continue;
			}
			
			if (!minRadius(entity.getLocation(), commandLocation, worldLocs, rm, goodRM)) {
				removalList.add(entity);
				continue;
			}
			if (!maxRadius(entity.getLocation(), commandLocation, worldLocs, r, goodR)) {
				removalList.add(entity);
				continue;
			}
			
			if (!minScore(entity.getName(), minScore, objectives)) {
				removalList.add(entity);
				continue;
			}
			if (!maxScore(entity.getName(), maxScore, objectives)) {
				removalList.add(entity);
				continue;
			}
			if (!eTag(entity.getScoreboardTags(), tag)) {
				removalList.add(entity);
				continue;
			}
			if (!eTeam(entity.getName(), teams, team)) {
				removalList.add(entity);
				continue;
			}
			
			if (!minLevel(entity, lm, goodLM)) {
				removalList.add(entity);
				continue;
			}
			if (!maxLevel(entity, l, goodL)) {
				removalList.add(entity);
				continue;
			}
			if (!gameMode(entity, m, mInt, goodM)) {
				removalList.add(entity);
				continue;
			}
			if (!eName(entity.getName(), name)) {
				removalList.add(entity);
				continue;
			}
			if (!minYaw(entity.getLocation().getYaw(), rxm, goodRXM)) {
				removalList.add(entity);
				continue;
			}
			if (!maxYaw(entity.getLocation().getYaw(), rx, goodRX)) {
				removalList.add(entity);
				continue;
			}
			if (!minPitch(entity.getLocation().getPitch(), rym, goodRYM)) {
				removalList.add(entity);
				continue;
			}
			if (!maxPitch(entity.getLocation().getPitch(), ry, goodRY)) {
				removalList.add(entity);
				continue;
			}
			if (!eType(entity.getType().name(), type, onlyPlayersWithoutType)) {
				removalList.add(entity);
				continue;
			}
		}
		list.removeAll(removalList);
		
		if (goodC) {
			while (list.size() > c) {
				list.remove(list.size() - 1);
			}
		}
	}
	
	private static boolean volume(int entityBlockXYZ, int commandBlockXYZ, int xyz, boolean goodXYZ, int dxdydz, boolean goodDXYZ) {
		if (goodDXYZ) {
			if (goodXYZ) {
				if (Math.abs(entityBlockXYZ - xyz) > dxdydz) {
					return false;
				}
			} else {
				if (Math.abs(entityBlockXYZ - commandBlockXYZ) > dxdydz) {
					return false;
				}
			}
		} else {
			if (goodXYZ) {
				if (entityBlockXYZ != xyz) {
					return false;
				}
			}
		}
		
		return true;
	}
	private static boolean minRadius(Location entityLocation, Location commandLocation, List<Location> commandLocations, int r, boolean goodR) {
		if (goodR) {
			if (r < 0) {
				if (!entityLocation.getWorld().equals(commandLocation.getWorld())) {
					return false;
				}
				if (entityLocation.distanceSquared(commandLocation) > r * r) {
					return false;
				}
			} else {
				for (Location l : commandLocations) {
					if (entityLocation.getWorld().equals(l.getWorld())) {
						if (entityLocation.distanceSquared(l) > r * r) {
							return false;
						}
						break;
					}
				}
			}
		}
		
		return true;
	}
	private static boolean maxRadius(Location entityLocation, Location commandLocation, List<Location> commandLocations, int rm, boolean goodRM) {
		if (goodRM) {
			if (rm < 0) {
				if (!entityLocation.getWorld().equals(commandLocation.getWorld())) {
					return false;
				}
				if (entityLocation.distanceSquared(commandLocation) <= rm * rm) {
					return false;
				}
			} else {
				for (Location l : commandLocations) {
					if (entityLocation.getWorld().equals(l.getWorld())) {
						if (entityLocation.distanceSquared(l) < rm * rm) {
							return false;
						}
						break;
					}
				}
			}
		}
		
		return true;
	}
	
	private static boolean minScore(String entityName, Object2IntMap<String> scores, Set<Objective> objectives) {
		if (scores == null) {
			return true;
		}
		
		for (Object2IntMap.Entry<String> kvp : scores.object2IntEntrySet()) {
			boolean good = false;
			for (Objective o : objectives) {
				if (o.getName().toLowerCase().equals(kvp.getKey())) {
					good = true;
					if (o.getScore(entityName).getScore() < kvp.getIntValue()) {
						return false;
					}
					break;
				}
			}
			if (!good) {
				return false;
			}
		}
		
		return true;
	}
	private static boolean maxScore(String entityName, Object2IntMap<String> scores, Set<Objective> objectives) {
		if (scores == null) {
			return true;
		}
		
		for (Object2IntMap.Entry<String> kvp : scores.object2IntEntrySet()) {
			boolean good = false;
			for (Objective o : objectives) {
				if (o.getName().toLowerCase().equals(kvp.getKey())) {
					good = true;
					if (o.getScore(entityName).getScore() > kvp.getIntValue()) {
						return false;
					}
					break;
				}
			}
			if (!good) {
				return false;
			}
		}
		
		return true;
	}
	
	private static boolean eTag(Set<String> entityTags, String tag) {
		if (tag == null) {
			return true;
		}
		
		boolean flipped = (tag.length() > 0 && tag.charAt(0) == '!') ? true : false;
		
		if (tag == "" || tag == "!") {
			return (entityTags.size() == 0) ? !flipped : flipped;
		}
		
		for (String t : entityTags) {
			if (t.toLowerCase().equals(tag)) {
				return !flipped;
			}
		}
		
		return flipped;
	}
	private static boolean eTeam(String entityName, Set<Team> teams, String team) {
		if (team == null) {
			return true;
		}
		
		boolean flipped = (team.length() > 0 && team.charAt(0) == '!') ? true : false;
		
		if (team == "" || team == "!") {
			for (Team t : teams) {
				if (t.getEntries().contains(entityName)) {
					return flipped;
				}
			}
			
			return !flipped;
		}
		
		for (Team t : teams) {
			if (t.getName().equalsIgnoreCase(team)) {
				if (t.getEntries().contains(entityName)) {
					return !flipped;
				}
			}
		}
		
		return flipped;
	}
	
	private static boolean minLevel(Entity entity, int lm, boolean goodLM) {
		if (goodLM) {
			if (!(entity instanceof Player)) {
				return false;
			}
			
			if (((Player) entity).getLevel() < lm) {
				return false;
			}
		}
		
		return true;
	}
	private static boolean maxLevel(Entity entity, int l, boolean goodL) {
		if (goodL) {
			if (!(entity instanceof Player)) {
				return false;
			}
			
			if (((Player) entity).getLevel() > l) {
				return false;
			}
		}
		
		return true;
	}
	private static boolean gameMode(Entity entity, String m, int mInt, boolean goodM) {
		if (m != null) {
			boolean flipped = (m.length() > 0 && m.charAt(0) == '!') ? true : false;
			
			if (!(entity instanceof Player)) {
				return false;
			}
			
			if (goodM) {
				if (mInt == -1) {
					return !flipped;
				} else if (mInt == 0) {
					return (((Player) entity).getGameMode() == GameMode.SURVIVAL) ? !flipped : flipped;
				} else if (mInt == 1) {
					return (((Player) entity).getGameMode() == GameMode.CREATIVE) ? !flipped : flipped;
				} else if (mInt == 2) {
					return (((Player) entity).getGameMode() == GameMode.ADVENTURE) ? !flipped : flipped;
				} else if (mInt == 3) {
					return (((Player) entity).getGameMode() == GameMode.SPECTATOR) ? !flipped : flipped;
				}
			} else {
				if (flipped) {
					m = m.substring(1);
				}
				
				if (m == "s" || m == "survival") {
					return (((Player) entity).getGameMode() == GameMode.SURVIVAL) ? !flipped : flipped;
				} else if (m == "c" || m == "creative") {
					return (((Player) entity).getGameMode() == GameMode.CREATIVE) ? !flipped : flipped;
				} else if (m == "a" || m == "adventure") {
					return (((Player) entity).getGameMode() == GameMode.ADVENTURE) ? !flipped : flipped;
				} else if (m == "sp" || m == "spectator") {
					return (((Player) entity).getGameMode() == GameMode.SPECTATOR) ? !flipped : flipped;
				}
			}
		}
		
		return true;
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
	private static boolean minPitch(double entityPitch, int rym, boolean goodRYM) {
		if (goodRYM) {
			if (entityPitch < rym) {
				return false;
			}
		}
		
		return true;
	}
	private static boolean maxPitch(double entityPitch, int ry, boolean goodRY) {
		if (goodRY) {
			if (entityPitch > ry) {
				return false;
			}
		}
		
		return true;
	}
	private static boolean minYaw(double entityPitch, int rxm, boolean goodRXM) {
		if (goodRXM) {
			if (entityPitch < rxm) {
				return false;
			}
		}
		
		return true;
	}
	private static boolean maxYaw(double entityPitch, int rx, boolean goodRX) {
		if (goodRX) {
			if (entityPitch > rx) {
				return false;
			}
		}
		
		return true;
	}
	private static boolean eType(String entityType, String type, boolean onlyPlayersWithoutType) {
		if (onlyPlayersWithoutType && (type == null || type.length() == 0)) {
			if (!entityType.equalsIgnoreCase("player")) {
				return false;
			}
		}
		
		boolean flipped = (type != null && type.length() > 0 && type.charAt(0) == '!') ? true : false;
		if (flipped && type != null) {
			type = type.substring(1);
		}
		
		return (type == null || entityType.equalsIgnoreCase(type)) ? !flipped : flipped;
	}
}
