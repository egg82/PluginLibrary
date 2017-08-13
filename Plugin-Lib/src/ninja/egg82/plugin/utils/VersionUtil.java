package ninja.egg82.plugin.utils;

import java.util.ArrayList;
import java.util.List;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.utils.ReflectUtil;

public final class VersionUtil {
	//vars
	
	//constructor
	public VersionUtil() {
		
	}
	
	//public
	public static Class<?> getBestMatch(String version, String pkg) {
		if (version == null) {
			throw new ArgumentNullException("version");
		}
		if (pkg == null) {
			throw new ArgumentNullException("pkg");
		}
		
		List<Class<?>> enums = ReflectUtil.getClasses(Object.class, pkg);
		
		// Sort by version, ascending
		enums.sort((v1, v2) -> {
			int[] v1Name = parseVersion(v1.getSimpleName(), '_');
			int[] v2Name = parseVersion(v2.getSimpleName(), '_');
			
			if (v1Name.length == 0) {
				return -1;
			}
			if (v2Name.length == 0) {
				return 1;
			}
			
			for (int i = 0; i < Math.min(v1Name.length, v2Name.length); i++) {
				if (v1Name[i] < v2Name[i]) {
					return -1;
				} else if (v1Name[i] > v2Name[i]) {
					return 1;
				}
			}
			
			return 0;
		});
		
		int[] currentVersion = parseVersion(version, '.');
		
		Class<?> bestMatch = null;
		
		// Ascending order means it will naturally try to get the highest possible value (lowest->highest)
		for (Class<?> c : enums) {
			String name = c.getSimpleName();
		    
		    int[] reflectVersion = parseVersion(name, '_');
		    
		    // Here's where we cap how high we can get, comparing the reflected version to the Bukkit version
		    // True makes the initial assumption that the current reflected version is correct
		    boolean equalToOrLessThan = true;
		    for (int i = 0; i < reflectVersion.length; i++) {
		    	if (currentVersion.length > i) {
		    		if(reflectVersion[i] > currentVersion[i]) {
		    			// We do not, in fact, have the correct version
		    			equalToOrLessThan = false;
		    			break;
		    		} else if (currentVersion[i] > reflectVersion[i]) {
		    			// We definitely have the correct version. At least until a better one comes along
		    			break;
		    		}
		    	} else {
		    		// Nope, this isn't the correct version
		    		equalToOrLessThan = false;
		    		break;
		    	}
		    }
		    if (equalToOrLessThan) {
		    	// Our initial assumption was correct. Use this version until we can find one that's better
		    	bestMatch = c;
		    }
		}
		
		return bestMatch;
	}
	
	public static int[] parseVersion(String version, char separator) {
		if (version == null) {
			throw new ArgumentNullException("version");
		}
		
		ArrayList<Integer> ints = new ArrayList<Integer>();
		
		int lastIndex = 0;
	    int currentIndex = version.indexOf(separator);
	    
	    while (currentIndex > -1) {
	      int current = tryParseInt(version.substring(lastIndex, currentIndex));
	      if (current > -1) {
	    	  ints.add(current);
	      }
	      
	      lastIndex = currentIndex + 1;
	      currentIndex = version.indexOf(separator, currentIndex + 1);
	    }
	    int current = tryParseInt(version.substring(lastIndex, version.length()));
	    if (current > -1) {
	    	ints.add(current);
	    }
		
		return ints.stream().mapToInt(i -> i).toArray();
	}
	
	//private
	private static int tryParseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (Exception ex) {
			return -1;
		}
	}
}
