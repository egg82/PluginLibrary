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
	
	/**
	 * Returns a single real (not an interface or an abstract) class in a package that best
	 * matches the version you are targeting.
	 * The classes must be named as such: 'ClassName_v1_v2_v3_etc'
	 * The version must be named as such: 'v1.v2.v3.etc'
	 * 
	 * For example, if you have a version "1.11.2" that you want to match against you'll have
	 * a package full of classes with names like the ones below:
	 * DynamicClass_1_12
	 * DynamicClass_1_8
	 * DynamicClass_1_10_2
	 * In which case this function will return "DynamicClass_1_10_2" since it is the closest (lower-bounded) match.
	 * If you instead used "1.8" as your matching string it would return "DynamicClass_1_8"
	 * If you used "1.7" as your version string you would get null, as there's no lower-bound match against that version.
	 * Finally, if "1.14.2.10.6.12" was your version string you would end up with "DynamicClass_1_12"
	 * 
	 * @param clazz The class type to search for. Can be an interface, abstract, or real class
	 * @param version The current version to match against
	 * @param pkg The package name to search
	 * @param recursive Whether or not to search the package recursively
	 * @return The closest-matching class found matching the criteria, or null if none were found
	 */
	public static <T> Class<T> getBestMatch(Class<T> clazz, String version, String pkg, boolean recursive) {
		if (version == null) {
			throw new ArgumentNullException("version");
		}
		if (pkg == null) {
			throw new ArgumentNullException("pkg");
		}
		
		List<Class<T>> enums = ReflectUtil.getClasses(clazz, pkg, recursive, false, false);
		
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
		
		Class<T> bestMatch = null;
		
		// Ascending order means it will naturally try to get the highest possible value (lowest->highest)
		for (Class<T> c : enums) {
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
