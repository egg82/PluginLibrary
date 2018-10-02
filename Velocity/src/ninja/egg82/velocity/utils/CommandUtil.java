package ninja.egg82.velocity.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.utils.MathUtil;
import ninja.egg82.velocity.BasePlugin;
import ninja.egg82.velocity.core.OfflinePlayer;

public class CommandUtil {
    // vars

    // constructor
    public CommandUtil() {

    }

    // public
    public static boolean isPlayer(CommandSource sender) {
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

    /**
     * Please note that in Velocity this function is limited. The limitations are
     * listed below: 1. Results are limited to Players. Symbols and flags that
     * target specific entities will be ignored. 2. Symbols and flags that target
     * specific locations will be ignored. 3. Symbols and flags that target specific
     * levels will be ignored. 4. Symbols and flags that target specific GameModes
     * will be ignored. 5. Symbols and flags that target specific scoreboard teams
     * or scores will be ignored. Everything else should work as expected. If you
     * need these features, simply pass a command or channel to your plugin.
     * 
     * @param symbol The symbol string to parse
     * @return A list of Players returned by all servers matching the result
     */
    public static List<Player> parseAtSymbol(String symbol) {
        if (symbol == null || symbol.length() <= 1 || symbol.charAt(0) != '@') {
            return new ArrayList<Player>();
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

        return new ArrayList<Player>();
    }

    public static Player getPlayerByName(String name) {
        if (name == null) {
            return null;
        }

        return ServiceLocator.getService(BasePlugin.class).getProxy().getPlayer(name).orElse(null);
    }
    public static Player getPlayerByUuid(String uuid) {
        return getPlayerByUuid(UUID.fromString(uuid));
    }
    public static Player getPlayerByUuid(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        return ServiceLocator.getService(BasePlugin.class).getProxy().getPlayer(uuid).orElse(null);
    }
    public static OfflinePlayer getOfflinePlayerByName(String name) {
        if (name == null) {
            return null;
        }

        return new OfflinePlayer(name);
    }
    public static OfflinePlayer getOfflinePlayerByUuid(String uuid) {
        return getOfflinePlayerByUuid(UUID.fromString(uuid));
    }
    public static OfflinePlayer getOfflinePlayerByUuid(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        return new OfflinePlayer(uuid);
    }

    // private
    private static List<Player> parseASymbol(String symbol) {
        // @a means ALL players

        int beginArgs = symbol.indexOf('[');
        int endArgs = symbol.indexOf(']');

        if (beginArgs != -1 && endArgs != -1) {
            // Get the specified args
            Map<String, String> args = getArguments(symbol.substring(beginArgs + 1, endArgs).trim());
            ArrayList<Player> retVal = new ArrayList<Player>(ServiceLocator.getService(BasePlugin.class).getProxy().getAllPlayers());

            filter(retVal, args);

            return retVal;
        }

        // No args specified. Get ALL players
        return new ArrayList<Player>(ServiceLocator.getService(BasePlugin.class).getProxy().getAllPlayers());
    }
    private static List<Player> parseESymbol(String symbol) {
        // @e means ALL entities

        int beginArgs = symbol.indexOf('[');
        int endArgs = symbol.indexOf(']');

        if (beginArgs != -1 && endArgs != -1) {
            // Get the specified args
            Map<String, String> args = getArguments(symbol.substring(beginArgs + 1, endArgs).trim());
            ArrayList<Player> retVal = new ArrayList<Player>(ServiceLocator.getService(BasePlugin.class).getProxy().getAllPlayers());

            filter(retVal, args);

            return retVal;
        }

        // No args specified. Get EVERYTHING
        return new ArrayList<Player>(ServiceLocator.getService(BasePlugin.class).getProxy().getAllPlayers());
    }
    private static List<Player> parsePSymbol(String symbol) {
        // @p means closest player (or entity with "type" argument set)

        int beginArgs = symbol.indexOf('[');
        int endArgs = symbol.indexOf(']');

        if (beginArgs != -1 && endArgs != -1) {
            // Get the specified args
            Map<String, String> args = getArguments(symbol.substring(beginArgs + 1, endArgs).trim());
            ArrayList<Player> retVal = new ArrayList<Player>(ServiceLocator.getService(BasePlugin.class).getProxy().getAllPlayers());

            filter(retVal, args);

            return retVal;
        }

        // No args specified. Get closest player
        Player closest = null;

        Collection<Player> players = ServiceLocator.getService(BasePlugin.class).getProxy().getAllPlayers();
        if (!players.isEmpty()) {
            closest = players.iterator().next();
        }

        if (closest == null) {
            return new ArrayList<Player>();
        }
        return new ArrayList<Player>(Arrays.asList(closest));
    }
    private static List<Player> parseRSymbol(String symbol) {
        // @r means random player (or entity with "type" argument set)

        int beginArgs = symbol.indexOf('[');
        int endArgs = symbol.indexOf(']');

        if (beginArgs != -1 && endArgs != -1) {
            // Get the specified args
            Map<String, String> args = getArguments(symbol.substring(beginArgs + 1, endArgs).trim());
            ArrayList<Player> retVal = new ArrayList<Player>(ServiceLocator.getService(BasePlugin.class).getProxy().getAllPlayers());

            Collections.shuffle(retVal);

            filter(retVal, args);

            return retVal;
        }

        // No args specified. Get random player
        ArrayList<Player> players = new ArrayList<Player>(ServiceLocator.getService(BasePlugin.class).getProxy().getAllPlayers());

        if (players.size() == 0) {
            return new ArrayList<Player>();
        }
        return new ArrayList<Player>(Arrays.asList(players.get(MathUtil.fairRoundedRandom(0, players.size()))));
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
    private static void filter(List<Player> list, Map<String, String> args) {
        int c = -1;
        boolean goodVal = true;
        try {
            c = Integer.parseInt(args.get("c"));
        } catch (Exception ex) {
            goodVal = true;
        }
        String name = args.get("name");

        ArrayList<Player> removalList = new ArrayList<Player>();
        for (Player entity : list) {
            if (!eName(entity.getUsername(), name)) {
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
