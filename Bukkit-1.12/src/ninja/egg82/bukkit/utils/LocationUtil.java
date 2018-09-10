package ninja.egg82.bukkit.utils;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class LocationUtil {
    // vars
    private static double gravity = 0.1d;

    // constructor
    public LocationUtil() {

    }

    // public
    public static boolean isFinite(Vector vec) {
        if (Math.abs(vec.getX()) > Double.MAX_VALUE) {
            return false;
        }
        if (Math.abs(vec.getY()) > Double.MAX_VALUE) {
            return false;
        }
        if (Math.abs(vec.getZ()) > Double.MAX_VALUE) {
            return false;
        }
        return true;
    }

    public static BlockFace getFacingDirection(double yaw, boolean cardinal) {
        yaw += 180.0d;

        while (yaw < 0.0d) {
            yaw += 360.0d;
        }
        while (yaw > 360.0d) {
            yaw -= 360.0d;
        }

        if (cardinal) {
            if (yaw >= 315.0d || yaw < 45.0d) {
                return BlockFace.NORTH;
            } else if (yaw >= 45.0d && yaw < 135.0d) {
                return BlockFace.EAST;
            } else if (yaw >= 135.0d && yaw < 225.0d) {
                return BlockFace.SOUTH;
            }

            return BlockFace.WEST;
        }

        if (yaw >= 348.75d || yaw < 11.25d) {
            return BlockFace.NORTH;
        } else if (yaw >= 11.25d && yaw < 33.75d) {
            return BlockFace.NORTH_NORTH_EAST;
        } else if (yaw >= 33.75d && yaw < 56.25d) {
            return BlockFace.NORTH_EAST;
        } else if (yaw >= 56.25d && yaw < 78.75d) {
            return BlockFace.EAST_NORTH_EAST;
        } else if (yaw >= 78.75d && yaw < 101.25d) {
            return BlockFace.EAST;
        } else if (yaw >= 101.25d && yaw < 123.75d) {
            return BlockFace.EAST_SOUTH_EAST;
        } else if (yaw >= 123.75d && yaw < 146.25d) {
            return BlockFace.SOUTH_EAST;
        } else if (yaw >= 146.25d && yaw < 168.75d) {
            return BlockFace.SOUTH_SOUTH_EAST;
        } else if (yaw >= 168.75d && yaw < 191.25d) {
            return BlockFace.SOUTH;
        } else if (yaw >= 191.25d && yaw < 213.75d) {
            return BlockFace.SOUTH_SOUTH_WEST;
        } else if (yaw >= 213.75d && yaw < 236.25d) {
            return BlockFace.SOUTH_WEST;
        } else if (yaw >= 236.25d && yaw < 258.75d) {
            return BlockFace.WEST_SOUTH_WEST;
        } else if (yaw >= 258.75d && yaw < 281.25d) {
            return BlockFace.WEST;
        } else if (yaw >= 281.25d && yaw < 303.75d) {
            return BlockFace.WEST_NORTH_WEST;
        } else if (yaw >= 303.75d && yaw < 326.25d) {
            return BlockFace.NORTH_WEST;
        }

        return BlockFace.NORTH_NORTH_WEST;
    }

    public static double getYaw(BlockFace facing, boolean cardinal) {
        if (cardinal) {
            if (facing == BlockFace.NORTH) {
                return 0.0d + 180.0d;
            } else if (facing == BlockFace.EAST) {
                return 90.0d + 180.0d;
            } else if (facing == BlockFace.SOUTH) {
                return 180.0d + 180.0d;
            }

            return 270.0d + 180.0d;
        }

        if (facing == BlockFace.NORTH) {
            return 0.0d + 180.0d;
        } else if (facing == BlockFace.NORTH_NORTH_EAST) {
            return 22.5d + 180.0d;
        } else if (facing == BlockFace.NORTH_EAST) {
            return 45.0d + 180.0d;
        } else if (facing == BlockFace.EAST_NORTH_EAST) {
            return 67.5d + 180.0d;
        } else if (facing == BlockFace.EAST) {
            return 90.0d + 180.0d;
        } else if (facing == BlockFace.EAST_SOUTH_EAST) {
            return 112.5d + 180.0d;
        } else if (facing == BlockFace.SOUTH_EAST) {
            return 135.0d + 180.0d;
        } else if (facing == BlockFace.SOUTH_SOUTH_EAST) {
            return 157.5d + 180.0d;
        } else if (facing == BlockFace.SOUTH) {
            return 180.0d + 180.0d;
        } else if (facing == BlockFace.SOUTH_SOUTH_WEST) {
            return 202.5d + 180.0d;
        } else if (facing == BlockFace.SOUTH_WEST) {
            return 225.0d + 180.0d;
        } else if (facing == BlockFace.WEST_SOUTH_WEST) {
            return 247.5d + 180.0d;
        } else if (facing == BlockFace.WEST) {
            return 270.0d + 180.0d;
        } else if (facing == BlockFace.WEST_NORTH_WEST) {
            return 292.5d + 180.0d;
        } else if (facing == BlockFace.NORTH_WEST) {
            return 315.0d + 180.0d;
        }

        return 337.5d + 180.0d;
    }

    public static Location getLocationInFront(Location loc, double distance, boolean includeY) {
        double angle = loc.getYaw();

        angle += 90.0d;

        while (angle < 0.0d) {
            angle += 360.0d;
        }
        while (angle > 360.0d) {
            angle -= 360.0d;
        }

        angle = angle * Math.PI / 180.0d;
        double sin = Math.sin(angle);

        return new Location(loc.getWorld(), loc.getX() + distance * Math.cos(angle), (includeY) ? loc.getY() + distance * sin * sin : loc.getY(), loc.getZ() + distance * sin);
    }

    public static Location getLocationBehind(Location loc, double distance, boolean includeY) {
        double angle = loc.getYaw();

        angle += 270.0d;

        while (angle < 0.0d) {
            angle += 360.0d;
        }
        while (angle > 360.0d) {
            angle -= 360.0d;
        }

        angle = angle * Math.PI / 180.0d;
        double sin = Math.sin(angle);

        return new Location(loc.getWorld(), loc.getX() + distance * Math.cos(angle), (includeY) ? loc.getY() + distance * sin * sin : loc.getY(), loc.getZ() + distance * sin);
    }

    public static Location getLocationAtAngle(Location center, double distance, double dregreeOffsetFromFacingDirection, boolean includeY) {
        double angle = center.getYaw();

        angle += 90.0d + dregreeOffsetFromFacingDirection;

        while (angle < 0.0d) {
            angle += 360.0d;
        }
        while (angle > 360.0d) {
            angle -= 360.0d;
        }

        angle = angle * Math.PI / 180.0d;
        double sin = Math.sin(angle);

        return new Location(center.getWorld(), center.getX() + distance * Math.cos(angle), (includeY) ? center.getY() + distance * sin * sin : center.getY(), center.getZ() + distance * sin);
    }

    public static Location getRandomPointAround(Location loc, double radius, boolean includeY) {
        double angle = Math.random() * Math.PI * 2.0d;
        double sin = Math.sin(angle);
        return new Location(loc.getWorld(), loc.getX() + radius * Math.cos(angle), (includeY) ? loc.getY() + radius * sin * sin : loc.getY(), loc.getZ() + radius * sin);
    }

    public static Location[] getHalfCircleAround(Location loc, double radius, int numPoints) {
        Location[] retVal = new Location[numPoints];
        double piSlice = Math.PI / numPoints;

        double angle = loc.getYaw();

        while (angle < 0.0d) {
            angle += 360.0d;
        }
        while (angle > 360.0d) {
            angle -= 360.0d;
        }

        angle = angle * Math.PI / 180.0d;

        for (int i = 0; i < numPoints; i++) {
            double newAngle = angle + piSlice * i;
            retVal[i] = new Location(loc.getWorld(), loc.getX() + radius * Math.cos(newAngle), loc.getY(), loc.getZ() + radius * Math.sin(newAngle));
        }

        return retVal;
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

    public static boolean areEqualPitchYaw(Location from, Location to) {
        return areEqualPitchYaw(from, to, 0.0d);
    }

    public static boolean areEqualPitchYaw(Location from, Location to, double epsilon) {
        return (Math.abs(from.getPitch() - to.getPitch()) > epsilon || Math.abs(from.getYaw() - to.getYaw()) > epsilon) ? false : true;
    }

    // private
    private static double getTargetVelocity(double d, double a, double t) {
        a *= -0.5d;
        a *= Math.pow(t, 2.0d);
        d -= a;
        return 2.0d * (d / t);
    }
}
