package com.github;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class Shot {
    private final Location from;
    private ShotData data;

    public Shot(Location from, ShotData data) {
        this.from = from;
        this.data = data;
    }
    
    public List<HitBox> arrangeClosest(List<HitBox> hitBoxes) {
        return arrangeClosest(from, hitBoxes);
    }
    
    public static List<HitBox> arrangeClosest(Location from, List<HitBox> hitBoxes) {
        List<HitBox> arranged = new ArrayList<HitBox>();
        for (int i = 0; i < hitBoxes.size(); i++) {
            HitBox closest = getClosest(from, hitBoxes);
            hitBoxes.remove(closest);
            arranged.add(closest);
        }
        return arranged;
    }
    
    public HitBox getClosest(List<HitBox> hitBoxes) {
        return getClosest(from, hitBoxes);
    }
    
    public static HitBox getClosest(Location from, List<HitBox> hitBoxes) {
        HitBox closest = hitBoxes.get(0);
        for (HitBox hitBox : hitBoxes) {
            if (hitBox.getCenter().distance(from) < closest.getCenter().distance(from)) {
                closest = hitBox;
            }
        }
        return closest;
    }

    // TODO - Checking for obstacles
    public List<Hit> shoot(List<HitBox> hitBoxes) {
        List<Hit> hits = new ArrayList<Hit>();
        for (HitBox hitBox : hitBoxes) {
            hitBox.update();
            float fromYaw = from.getYaw() % 360;
            float fromPitch = from.getPitch() % 360;
            // making sure the center location is within range
            if (hitBox.getCenter().distanceSquared(from) > Math.pow(data.getDistanceToTravel(), 2)) {
                continue;
            }
            /* TODO Only allow hits on parts of the rectangle that are within range,
             * not just the whole thing if the center is within range. */
            // accounting for wind speed/direction
            float windCompassDirection = data.getWindCompassDirection(from.getWorld());
            float windSpeed = data.getWindSpeedMPH(from.getWorld());
            fromYaw += (windCompassDirection > fromYaw ? 1 : windCompassDirection < fromYaw ? -1 : 0) * windSpeed;
            fromYaw %= 360;
            int[] orderClockwise = new int[] {0, 1, 4, 3};
            Location thisSideCorner = hitBox.getCorner(0);
            Location oppositeSideCorner = hitBox.getCorner(0);
            for (int i = 0; i < orderClockwise.length; i++) {
                int num = orderClockwise[i];
                Location corner = hitBox.getCorner(num);
                Location clockwise = hitBox.getCorner(orderClockwise[(i + 1) % 3]);
                if ((Math.atan2(from.getZ() - corner.getZ(), from.getX() - corner.getX()) * 180 / Math.PI) > 0 && corner.distanceSquared(from) < clockwise.distanceSquared(from)) {
                    thisSideCorner = corner;
                    int exitCornerClockwiseAmount = (Math.atan2(from.getZ() - clockwise.getZ(), from.getX() - clockwise.getX()) * 180 / Math.PI) < 0 ? 2 : 3;
                    oppositeSideCorner = hitBox.getCorner((i + exitCornerClockwiseAmount) % 3);
                }
            }
            Location entrance = getProjectileLocation(thisSideCorner, data, hitBox, fromYaw, fromPitch);
            double distance = entrance.distance(from);
            double deltaX = data.getDeltaX(distance, fromYaw);
            double deltaY = data.getDeltaY(distance, fromPitch);
            double deltaZ = data.getDeltaZ(distance, fromYaw);
            entrance.add(deltaX, deltaY, deltaZ);
            Location exit = getProjectileLocation(oppositeSideCorner, data, hitBox, deltaX, deltaY, deltaZ, fromYaw, fromPitch);
            // hit detection and reaction
            boolean hitX = entrance.getX() <= hitBox.getHighestX() && entrance.getX() >= hitBox.getLowestX();
            boolean hitY = entrance.getY() <= hitBox.getHighestY() && entrance.getY() >= hitBox.getLowestY();
            boolean hitZ = entrance.getZ() <= hitBox.getHighestZ() && entrance.getZ() >= hitBox.getLowestZ();
            if (hitX && hitY && hitZ) {
                hits.add(new Hit(from, entrance, exit, hitBox, data));
            }
        }
        return hits;
    }

    private Location getProjectileLocation(Location thisSideCorner, ShotData data, HitBox hitBox, float fromYaw, float fromPitch) {
        return getProjectileLocation(thisSideCorner, data, hitBox, 0, 0, 0, fromYaw, fromPitch);
    }
    
    private Location getProjectileLocation(Location thisSideCorner, ShotData data, HitBox hitBox, double addX, double addY, double addZ, float fromYaw, float fromPitch) {
        double deltaFromToSideCornerX = thisSideCorner.getX() - from.getX();
        double deltaFromToSideCornerY = thisSideCorner.getY() - from.getY();
        double deltaFromToSideCornerZ = thisSideCorner.getZ() - from.getZ();
        double xzDistFromSideCorner = Math.sqrt(Math.pow(deltaFromToSideCornerX, 2) + Math.pow(deltaFromToSideCornerZ, 2));
        double yawToSideCorner = Math.atan2(deltaFromToSideCornerX, deltaFromToSideCornerZ) * 180 / Math.PI;// flipped x and z from normal
        double theta1 = yawToSideCorner - fromYaw;
        double theta2 = yawToSideCorner - theta1;
        double outerAngle = 180 - yawToSideCorner - 90;// previously theta1
        double outerAngleInShotCone = outerAngle + 90 + hitBox.getYawRotation();
        double lastAngleInShotCone = 180 - theta1 - outerAngleInShotCone;
        double xzDistanceFromHit = (xzDistFromSideCorner * Math.sin(Math.toRadians(outerAngleInShotCone))) / Math.sin(Math.toRadians(lastAngleInShotCone));
        double deltaX = xzDistanceFromHit * Math.sin(Math.toRadians(theta2));// leaves out sin 90 because its just equal to 1...
        double deltaZ = xzDistanceFromHit * Math.sin(Math.toRadians(90 - theta2));// leaves out sin 90 because its just equal to 1...
        double xyzDistFromSideCorner = Math.sqrt(Math.pow(xzDistFromSideCorner, 2) + Math.pow(deltaFromToSideCornerY, 2));
        double theta3 = Math.atan2(Math.abs(deltaFromToSideCornerY), xzDistFromSideCorner) * 180 / Math.PI;
        double theta4 = Math.abs(fromPitch) - theta3;
        double theta5 = 90 + theta3;
        double theta6 = 180 - theta4 - theta5;
        double hitDistance = (xyzDistFromSideCorner * Math.sin(Math.toRadians(theta5))) / Math.sin(Math.toRadians(theta6));
        double deltaY = hitDistance * Math.sin(Math.toRadians(Math.abs(fromPitch)));// leaves out sin 90 because its just equal to 1...
        if (deltaFromToSideCornerX < 0 && deltaX > 0) {
            deltaX *= -1;
        }
        if (fromPitch > 0 && deltaY > 0) {// pitch in minecraft is backwards, normally it would be fromPitch < 0
            deltaY *= -1;
        }
        if (deltaFromToSideCornerZ < 0 && deltaZ > 0) {
            deltaZ *= -1;
        }
        Location hit = from.clone().add(deltaX + addX, deltaY + addY, deltaZ + addZ);
        hit.setYaw(fromYaw);
        hit.setPitch(fromPitch);
        return hit;
    }
}
