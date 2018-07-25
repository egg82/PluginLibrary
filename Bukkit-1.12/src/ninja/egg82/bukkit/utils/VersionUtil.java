package ninja.egg82.bukkit.utils;

import java.util.Comparator;
import java.util.List;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
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
	 * @param packageName The package name to search
	 * @param recursive Whether or not to search the package recursively
	 * @return The closest-matching class found matching the criteria, or null if none were found
	 */
	public static <T> Class<T> getBestMatch(Class<T> clazz, String version, String packageName, boolean recursive) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null.");
		}
		if (version == null) {
			throw new IllegalArgumentException("version cannot be null.");
		}
		if (packageName == null) {
			throw new IllegalArgumentException("packageName cannot be null.");
		}
		
		List<Class<T>> enums = ReflectUtil.getClasses(clazz, packageName, recursive, false, false);
		
		// Sort by version, ascending
		enums.sort(new Comparator<Class<T>>() {
			public int compare(Class<T> v1, Class<T> v2) {
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
			}
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
			throw new IllegalArgumentException("version cannot be null.");
		}
		
		IntList ints = new IntArrayList();
		
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
		
		return ints.toIntArray();
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
