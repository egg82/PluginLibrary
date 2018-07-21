package ninja.egg82.nbt.utils;

public class NBTReflectUtil {
	//vars
	
	//constructor
	public NBTReflectUtil() {
		
	}
	
	//public
	public static boolean doesExtend(Class<?> baseClass, Class<?> classToTest) {
		if (classToTest == null || baseClass == null) {
			return false;
		}
		
		return classToTest.equals(baseClass) || baseClass.isAssignableFrom(classToTest);
	}
	
	//private
	
}
