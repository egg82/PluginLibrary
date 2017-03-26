package ninja.egg82.plugin.utils;

import gnu.trove.list.array.TIntArrayList;

public class VersionUtil {
	//vars
	
	//constructor
	public VersionUtil() {
		
	}
	
	//public
	public static int[] parseVersion(String version, char separator) {
		TIntArrayList ints = new TIntArrayList();
		
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
		
		return ints.toArray();
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
