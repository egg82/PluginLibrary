package ninja.egg82.plugin.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LocationUtil {
	//vars
	private static double gravity = 0.1d;
	
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
		return moveSmoothly(from, to, 2.5d);
	}
	public static Vector moveSmoothly(Location from, Location to, double time) {
		double x = to.getX() - from.getX();
		double y = to.getY() - from.getY();
		double z = to.getZ() - from.getZ();
		
		return new Vector(getTargetVelocity(x, 0, time), getTargetVelocity(y, gravity, time), getTargetVelocity(z, 0, time));
	}
	
	public static boolean areEqualXYZ(Location from, Location to) {
		if (from.getX() != to.getX()) {
			return false;
		}
		if (from.getY() != to.getY()) {
			return false;
		}
		if (from.getZ() != to.getZ()) {
			return false;
		}
		return true;
	}
	public static Location makeEqualXYZ(Location from, Location to) {
		to = to.clone();
		
		to.setX(from.getX());
		to.setY(from.getY());
		to.setZ(from.getZ());
		
		return to;
	}
	
	//private
	private static double getTargetVelocity(double d, double a, double t) {
		a *= -0.5d;
		a *= Math.pow(t, 2.0d);
		d -= a;
		return 2.0d * (d / t);
	}
}
