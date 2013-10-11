package com.github;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class Shot {
    private final Location from;

    public Shot(Location from) {
        this.from = from;
    }

    // TODO - Checking for obstacles
    public List<Hit> shoot(ShotData data, List<HitBox> hitBoxes) {
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
            fromYaw += (windCompassDirection > fromYaw ? 1 : windCompassDirection < fromYaw ? -1 : 0) * windCompassDirection / 60 * windSpeed;
            fromYaw %= 360;
            Location boxOrigin = hitBox.getOrigin();
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
            Location entrance = getHit(thisSideCorner, data, hitBox, fromYaw, fromPitch, true);
            Location exit = getHit(oppositeSideCorner, data, hitBox, fromYaw, fromPitch, false);
            // hit detection and reaction
            if (entrance.getX() - boxOrigin.getX() <= hitBox.getX() && entrance.getY() - boxOrigin.getY() <= hitBox.getY() && entrance.getZ() - boxOrigin.getZ() <= hitBox.getZ()) {
                hits.add(new Hit(from, entrance, exit, hitBox, data));
            }
        }
        return hits;
    }

    private Location getHit(Location thisSideCorner, ShotData data, HitBox hitBox, float fromYaw, float fromPitch, boolean variance) {
        double deltaFromToSideCornerX = from.getX() - thisSideCorner.getX();
        double deltaFromToSideCornerZ = from.getZ() - thisSideCorner.getZ();
        double xzDistFromSideCorner = Math.sqrt(Math.pow(deltaFromToSideCornerX, 2) + Math.pow(deltaFromToSideCornerZ, 2));
        double yawSubtractionToSideCorner = Math.atan2(deltaFromToSideCornerZ, deltaFromToSideCornerX) * 180 / Math.PI;
        float deltaYaw = Math.abs(hitBox.getYawRotation() - fromYaw);
        double originDeltaYaw = 180 - (yawSubtractionToSideCorner + deltaYaw);
        double xzHitDistance = (xzDistFromSideCorner * (Math.sin(Math.toRadians(originDeltaYaw))) / Math.sin(Math.toRadians(deltaYaw)));
        double deltaX = (xzHitDistance * (Math.sin(Math.toRadians(180 - (fromYaw + 90)))) / Math.sin(Math.toRadians(90)));
        double deltaY = (xzDistFromSideCorner * (Math.sin(Math.toRadians(-fromPitch))) / Math.sin(Math.toRadians(90 - fromPitch)));
        double deltaZ = (xzHitDistance * (Math.sin(Math.toRadians(fromYaw))) / Math.sin(Math.toRadians(90)));
        double hitDistance = (xzHitDistance * (Math.sin(Math.toRadians(90))) / Math.sin(Math.toRadians(fromYaw)));
        Location hit = from.clone().add(deltaX + (variance ? data.getDeltaX(hitDistance, fromYaw) : 0), deltaY + (variance ? data.getDeltaY(hitDistance, fromPitch) : 0), deltaZ + (variance ? data.getDeltaZ(hitDistance, fromYaw) : 0));
        hit.setYaw(fromYaw);
        hit.setPitch(fromPitch);
        return hit;
    }
}
