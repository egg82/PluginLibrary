package ninja.egg82.plugin.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LocationUtil {
	//vars
	
	//constructor
	public LocationUtil() {
		
	}
	
	//public
	public static Location getLocationBehind(Location loc, double distance) {
		double angle = loc.getYaw() + 180.0d;
		
		while (angle < 0.0d) {
			angle += 360.0d;
		}
		while (angle > 36.0d) {
			angle -= 360.0d;
		}
		
		return new Location(loc.getWorld(), loc.getX() + (distance * Math.cos(angle)), loc.getY(), loc.getZ() + distance * (Math.sin(angle)));
	}
	public static Location[] getCircleAround(Location loc, double radius, int numPoints) {
		Location[] retVal = new Location[numPoints];
		double piSlice = 2.0d * Math.PI / numPoints;
		
		for (int i = 0; i < numPoints; i++) {
			double angle = piSlice * i;
			retVal[i] = new Location(loc.getWorld(), loc.getX() + radius * Math.cos(angle), loc.getY(), loc.getZ() + radius * Math.sin(angle));
		}
		
		return retVal;
	}
	
	public static Vector moveSmoothly(Location from, Location to) {
		return moveSmoothly(from, to, 1.0d);
	}
	public static Vector moveSmoothly(Location from, Location to, double speed) {
		return to.clone().subtract(from).toVector().normalize().multiply(speed);
	}
	
	//private
	
}
