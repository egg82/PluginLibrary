package ninja.egg82.plugin.utils;

import java.util.ArrayList;

public final class VersionUtil {
	//vars
	
	//constructor
	public VersionUtil() {
		
	}
	
	//public
	public static int[] parseVersion(String version, char separator) {
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
