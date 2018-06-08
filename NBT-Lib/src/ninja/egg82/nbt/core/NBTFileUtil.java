package ninja.egg82.nbt.core;

import java.io.File;

public final class NBTFileUtil {
    //vars
    
    //constructor
    public NBTFileUtil() {
        
    }
    
    //public
    public static boolean pathIsFile(File file) {
    	if (!pathExists(file)) {
    		return false;
    	}
    	
    	try {
    		return file.isFile();
    	} catch (Exception ex) {
    		return false;
    	}
    }
    public static boolean pathIsFile(String path) {
    	if (!pathExists(path)) {
    		return false;
    	}
    	
    	File p = new File(path);
    	try {
    		return p.isFile();
    	} catch (Exception ex) {
    		return false;
    	}
    }
    
    public static boolean pathExists(File file) {
    	if (file == null) {
    		return false;
    	}
    	
    	try {
    		return file.exists();
    	} catch (Exception ex) {
    		return false;
    	}
    }
    public static boolean pathExists(String path) {
    	if (path == null || path.isEmpty()) {
    		return false;
    	}
    	
    	File p = new File(path);
    	try {
    		return p.exists();
    	} catch (Exception ex) {
    		return false;
    	}
    }
    
    //private
    
}
