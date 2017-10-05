package gg.revival.rac.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;

public class MathUtils {

    public static double offset(Entity entityA, Entity entityB) {
        return offset(entityA.getLocation().toVector(), entityB.getLocation().toVector());
    }

    public static double offset(final Location a, final Location b) {
        return offset(a.toVector(), b.toVector());
    }

    public static double offset(Vector vecA, Vector vecB) {
        return vecA.subtract(vecB).length();
    }

    public static long averageLong(List<Long> toAvg) {
        long result = 0L;

        for(long value : toAvg)
            result += value;

        return result / toAvg.size();
    }

    public static double getFixedXAxis(double x) {
        double rem = x - Math.round(x) + 0.01;

        if(rem < 0.3)
            x = Math.floor(x) - 1;

        return x;
    }

    public static Vector getHorizontalVector(Vector vector) {
        vector.setY(0);
        return vector;
    }

    public static Vector getVerticalVector(Vector vector) {
        vector.setX(0); vector.setZ(0);
        return vector;
    }

    public static double clamp180(double theta) {
        theta %= 360.0;

        if (theta >= 180.0)
            theta -= 360.0;

        if (theta < -180.0)
            theta += 360.0;

        return theta;
    }

    public static Vector getRotation(Location locationA, Location locationB) {
        double dx = locationB.getX() - locationA.getX();
        double dy = locationB.getY() - locationA.getY();
        double dz = locationB.getZ() - locationA.getZ();

        double distance = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float)(Math.atan2(dz, dx) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float)(-(Math.atan2(dy, distance) * 180.0 / 3.141592653589793));

        return new Vector(yaw, pitch, 0.0f);
    }

    public static double getHorizontalDistance(Location locationA, Location locationB) {
        double toReturn = 0.0;

        double xSqr = (locationB.getX() - locationA.getX()) * (locationB.getX() - locationA.getX());
        double zSqr = (locationB.getZ() - locationA.getZ()) * (locationB.getZ() - locationA.getZ());

        double sqrt = Math.sqrt(xSqr + zSqr);

        return Math.abs(sqrt);
    }

    public static double getDistance3D(final Location one, final Location two) {
        double xSqr = (two.getX() - one.getX()) * (two.getX() - one.getX());
        double ySqr = (two.getY() - one.getY()) * (two.getY() - one.getY());
        double zSqr = (two.getZ() - one.getZ()) * (two.getZ() - one.getZ());

        double sqrt = Math.sqrt(xSqr + ySqr + zSqr);

        return Math.abs(sqrt);
    }

    public static double getAimbotOffset(Location playerLocation, double playerEyeHeight, LivingEntity entity) {
        Location entityLocation = entity.getLocation().add(0.0, entity.getEyeHeight(), 0.0);
        Location playerEyeLocation = playerLocation.add(0.0, playerEyeHeight, 0.0);

        Vector playerRotation = new Vector(playerEyeLocation.getYaw(), playerEyeLocation.getPitch(), 0.0f);
        Vector expectedRotation = getRotation(playerEyeLocation, entityLocation);

        double deltaYaw = clamp180(playerRotation.getX() - expectedRotation.getX());
        double horizontalDistance = getHorizontalDistance(playerEyeLocation, entityLocation);
        double distance = getDistance3D(playerEyeLocation, entityLocation);

        return deltaYaw * horizontalDistance * distance;
    }

    public static double getAimbotoffset2(final Location playerLocLoc, final double playerEyeHeight, final LivingEntity entity) {
        final Location entityLoc = entity.getLocation().add(0.0, entity.getEyeHeight(), 0.0);
        final Location playerLoc = playerLocLoc.add(0.0, playerEyeHeight, 0.0);

        final Vector playerRotation = new Vector(playerLoc.getYaw(), playerLoc.getPitch(), 0.0f);
        final Vector expectedRotation = getRotation(playerLoc, entityLoc);

        final double deltaPitch = clamp180(playerRotation.getY() - expectedRotation.getY());
        final double distance = getDistance3D(playerLoc, entityLoc);

        return deltaPitch * Math.abs(Math.sqrt(entityLoc.getY() - playerLoc.getY())) * distance;
    }

}
