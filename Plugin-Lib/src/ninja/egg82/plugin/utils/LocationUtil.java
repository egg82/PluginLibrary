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
	public static Location getLocationInFront(Location loc, double distance) {
		double angle = loc.getYaw();
		
		angle += 90.0d;
		
		while (angle < 0.0d) {
			angle += 360.0d;
		}
		while (angle > 360.0d) {
			angle -= 360.0d;
		}
		
		angle = angle * Math.PI / 180.0d;
		
		return new Location(loc.getWorld(), loc.getX() + distance * Math.cos(angle), loc.getY(), loc.getZ() + distance * Math.sin(angle));
	}
	public static Location getLocationBehind(Location loc, double distance) {
		double angle = loc.getYaw();
		
		angle += 270.0d;
		
		while (angle < 0.0d) {
			angle += 360.0d;
		}
		while (angle > 360.0d) {
			angle -= 360.0d;
		}
		
		angle = angle * Math.PI / 180.0d;
		
		return new Location(loc.getWorld(), loc.getX() + distance * Math.cos(angle), loc.getY(), loc.getZ() + distance * Math.sin(angle));
	}
	public static Location getLocationAtAngle(Location center, double distance, double dregreeOffsetFromFacingDirection) {
		double angle = center.getYaw();
		
		angle += 90.0d + dregreeOffsetFromFacingDirection;
		
		while (angle < 0.0d) {
			angle += 360.0d;
		}
		while (angle > 360.0d) {
			angle -= 360.0d;
		}
		
		angle = angle * Math.PI / 180.0d;
		
		return new Location(center.getWorld(), center.getX() + distance * Math.cos(angle), center.getY(), center.getZ() + distance * Math.sin(angle));
	}
	public static Location getRandomPointAround(Location loc, double radius) {
		double angle = Math.random() * Math.PI * 2.0d;
		return new Location(loc.getWorld(), loc.getX() + radius * Math.cos(angle), loc.getY(), loc.getZ() + radius * Math.sin(angle));
	}
	public static Location[] getHalfCircleAround(Location loc, double radius, int numPoints) {
		Location[] retVal = new Location[numPoints];
		double piSlice = Math.PI / numPoints;
		
		double angle = loc.getYaw();
		
		for (int i = 0; i < numPoints; i++) {
			double newAngle = (angle + piSlice * i) * Math.PI / 180.0d;
			retVal[i] = new Location(loc.getWorld(), loc.getX() + radius * Math.cos(newAngle), loc.getY(), loc.getZ() + radius * Math.sin(newAngle));
		}
		
		return retVal;
	}
	public static Location[] getCircleAround(Location loc, double radius, int numPoints) {
		Location[] retVal = new Location[numPoints];
		double piSlice = 2.0d * Math.PI / numPoints;
		
		for (int i = 0; i < numPoints; i++) {
			double angle = (piSlice * i) * Math.PI / 180.0d;
			retVal[i] = new Location(loc.getWorld(), loc.getX() + radius * Math.cos(angle), loc.getY(), loc.getZ() + radius * Math.sin(angle));
		}
		
		return retVal;
	}
	
	public static Location toBlockLocation(Location loc) {
		return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
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
		return areEqualXYZ(from, to, 0.0d);
	}
	public static boolean areEqualXYZ(Location from, Location to, double epsilon) {
		return (!from.getWorld().equals(to.getWorld()) || from.distanceSquared(to) > epsilon * epsilon) ? false : true;
	}
	public static Location makeEqualXYZ(Location from, Location to) {
		to = to.clone();
		
		to.setWorld(from.getWorld());
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
