package com.github.jamesnorris.mcshot;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class Shot { // possible hit
    public static List<HitBox> arrangeClosest(Location from, List<HitBox> hitBoxes) {
        List<HitBox> arranged = new ArrayList<HitBox>();
        for (int i = 0; i < hitBoxes.size(); i++) {
            HitBox closest = getClosest(from, hitBoxes);
            hitBoxes.remove(closest);
            arranged.add(closest);
        }
        return arranged;
    }

    public static HitBox getClosest(Location from, List<HitBox> hitBoxes) {
        HitBox closest = hitBoxes.get(0);
        for (HitBox hitBox : hitBoxes) {
            if (getClosestCorner(from, hitBox).distance(from) < getClosestCorner(from, closest).distance(from)) {
                closest = hitBox;
            }
        }
        return closest;
    }

    public static Location getClosestCorner(Location from, HitBox hitBox) {
        Location closest = hitBox.getCenter();
        for (Location corner : hitBox.getCorners()) {
            if (corner.distanceSquared(from) < closest.distanceSquared(from)) {
                closest = corner;
            }
        }
        return closest;
    }

    private final int[] orderClockwise = new int[] {0, 3, 4, 1};
    private final Location from, to;
    private ShotData data;

    public Shot(Location from, ShotData data) {
        this.from = from;
        to = from.clone().add(data.getDistanceToTravel() * Math.sin(Math.toRadians(from.getYaw() * -1)), data.getDistanceToTravel()
                * Math.sin(Math.toRadians(from.getPitch() * -1)), data.getDistanceToTravel() * Math.cos(Math.toRadians(from.getYaw() * -1)));
        this.data = data;
    }

    public List<HitBox> arrangeClosest(List<HitBox> hitBoxes) {
        return arrangeClosest(from, hitBoxes);
    }

    public HitBox getClosest(List<HitBox> hitBoxes) {
        return getClosest(from, hitBoxes);
    }

    public Location getClosestCorner(HitBox hitBox) {
        return getClosestCorner(from, hitBox);
    }

    /**
     * Gets the entrance and exit of an indirect (changes due to {@link ShotData}) projectile through a hit box.<br>
     * This returns a Location array, containing entrance at the index of 0 and exit at the index of 1.<br>
     * This will return null if the direct ({@code getSimpleExtranceAndExit()}) path does not intersect the hit box.
     * 
     * @param hitBox The hit box to check and get extrance and exit from
     * @return A Location array containing entrance (0) and exit (1)
     */
    public Location[] getComplexEntranceAndExit(HitBox hitBox) {
        float fromYaw = MathUtility.absDegrees(from.getYaw() * -1);
        float fromPitch = MathUtility.absDegrees(from.getPitch() * -1);
        float windCompassDirection = MathUtility.absDegrees(data.getWindCompassDirection(from.getWorld()));
        float windSpeed = data.getWindSpeedMPH(from.getWorld());
        fromYaw += (windCompassDirection > fromYaw ? 1 : windCompassDirection < fromYaw ? -1 : 0) * windSpeed;
        fromYaw %= 360;
        Location[] entEx = getSimpleEntranceAndExit(hitBox);
        if (entEx == null) {
            return null;
        }
        Location entrance = entEx[0];
        Location exit = entEx[1];
        if (entrance != null) {
            double entranceDistance = entrance.distance(from);
            entrance.add(data.getDeltaX(entranceDistance, fromYaw), data.getDeltaY(entranceDistance, fromPitch), data.getDeltaZ(entranceDistance, fromYaw));
        }
        if (exit != null) {
            double exitDistance = exit.distance(from);
            exit.add(data.getDeltaX(exitDistance, fromYaw), data.getDeltaY(exitDistance, fromPitch), data.getDeltaZ(exitDistance, fromYaw));
        }
        return new Location[] {entrance, exit};
    }

    // public Location[] getSimpleEntranceAndExit(HitBox hitBox) {
    // return getSimpleEntranceAndExit(new LineSegment3D(from, to), hitBox);
    // }
    /**
     * Gets the entrance and exit of a direct (straight) projectile through a hit box.<br>
     * This returns a Location array, containing entrance at the index of 0 and exit at the index of 1.<br>
     * This will return null if the direct path does not intersect the hit box.
     * 
     * @param hitBox The hit box to check and get extrance and exit from
     * @return A Location array containing entrance (0) and exit (1)
     */
    public Location[] getSimpleEntranceAndExit(HitBox hitBox) {
        // Location from = shot.getStart();
        // Location[] hits = new Location[2];
        // for (Plane3D plane : hitBox.getPlanes()) {
        // Location intersect = plane.getIntersect(shot);
        // if (intersect == null || intersect.getX() == Double.NaN || intersect.getY() == Double.NaN || intersect.getZ() == Double.NaN) {
        // continue;
        // }
        // if (hits[0] == null || intersect.distanceSquared(from) < hits[0].distanceSquared(from)) {
        // if (hits[0] != null) {
        // hits[1] = hits[0];
        // }
        // hits[0] = intersect;
        // }
        // if (hits[1] == null) {
        // hits[1] = intersect;
        // }
        // }
        int leftViewCornerIndex = getLeftViewCornerIndex(hitBox);
        Location origin = new Location(from.getWorld(), 0, 0, 0, 0, 0);
        double originFromDistance = origin.distance(from);
        double originToDistance = origin.distance(to);
        Location leftViewCorner = getCornerClockwise(hitBox, leftViewCornerIndex);
        Location leftFarCorner = getCornerClockwise(hitBox, (leftViewCornerIndex + 1) % 3);// always the farthest
        Location rightFarCorner = getCornerClockwise(hitBox, (leftViewCornerIndex + 2) % 3);
        Location rightViewCorner = getCornerClockwise(hitBox, (leftViewCornerIndex + 3) % 3);
        double lowY = leftFarCorner.getY();
        double highY = leftFarCorner.getY() + hitBox.getHeight();
        Location2D fromXZ = new Location2D(from.getX(), from.getZ());
        Location2D fromDY = new Location2D(originFromDistance, from.getY());
        LineSegment2D farXZ = new LineSegment2D(new Location2D(leftFarCorner.getX(), leftFarCorner.getZ()), new Location2D(rightFarCorner.getX(), rightFarCorner.getZ()));
        LineSegment2D closeXZ = new LineSegment2D(new Location2D(leftViewCorner.getX(), leftViewCorner.getZ()), new Location2D(rightViewCorner.getX(), rightViewCorner.getZ()));
        LineSegment2D leftXZ = new LineSegment2D(new Location2D(leftViewCorner.getX(), leftViewCorner.getZ()), new Location2D(leftFarCorner.getX(), leftFarCorner.getZ()));
        LineSegment2D rightXZ = new LineSegment2D(new Location2D(rightViewCorner.getX(), rightViewCorner.getZ()), new Location2D(rightFarCorner.getX(), rightFarCorner.getZ()));
        LineSegment2D[] outlineXZ = new LineSegment2D[] {farXZ, closeXZ, leftXZ, rightXZ};
        LineSegment2D shotPathXZ = new LineSegment2D(fromXZ, new Location2D(to.getX(), to.getZ()));
        Location2D[] cfXZInter = getCloseAndFarIntersect(fromXZ, shotPathXZ, outlineXZ);
        Location2D closeXZIntersect = cfXZInter[0];
        Location2D farXZIntersect = cfXZInter[1];
        double leftFarDist = leftFarCorner.distance(origin);
        double leftViewDist = leftViewCorner.distance(origin);
        double rightFarDist = rightFarCorner.distance(origin);
        double rightViewDist = rightViewCorner.distance(origin);
        LineSegment2D leftFarDY = new LineSegment2D(new Location2D(leftFarDist, lowY), new Location2D(leftFarDist, highY));
        LineSegment2D leftViewDY = new LineSegment2D(new Location2D(leftViewDist, lowY), new Location2D(leftViewDist, highY));
        LineSegment2D rightFarDY = new LineSegment2D(new Location2D(rightFarDist, lowY), new Location2D(rightFarDist, highY));
        LineSegment2D rightViewDY = new LineSegment2D(new Location2D(rightViewDist, lowY), new Location2D(rightViewDist, highY));
        LineSegment2D[] outlineDY = new LineSegment2D[] {leftFarDY, leftViewDY, rightFarDY, rightViewDY};
        LineSegment2D shotPathDY = new LineSegment2D(fromDY, new Location2D(originToDistance, to.getY()));
        Location2D[] cfDYInter = getCloseAndFarIntersect(fromDY, shotPathDY, outlineDY);
        Location2D closeDYIntersect = cfDYInter[0];
        Location2D farDYIntersect = cfDYInter[1];
        if (closeXZIntersect == null || farXZIntersect == null || closeDYIntersect == null || farDYIntersect == null) {
            LineSegment2D topXY = new LineSegment2D(new Location2D(hitBox.getLowestX(), highY), new Location2D(hitBox.getHighestX(), highY));
            LineSegment2D topZY = new LineSegment2D(new Location2D(hitBox.getLowestZ(), highY), new Location2D(hitBox.getHighestZ(), highY));
            LineSegment2D bottomXY = new LineSegment2D(new Location2D(hitBox.getLowestX(), lowY), new Location2D(hitBox.getHighestX(), lowY));
            LineSegment2D bottomZY = new LineSegment2D(new Location2D(hitBox.getLowestZ(), lowY), new Location2D(hitBox.getHighestZ(), lowY));
            LineSegment2D[] outlineXY = new LineSegment2D[] {topXY, bottomXY};
            LineSegment2D[] outlineZY = new LineSegment2D[] {topZY, bottomZY};
            Location2D fromXY = new Location2D(from.getX(), from.getY());
            Location2D fromZY = new Location2D(from.getZ(), from.getY());
            LineSegment2D shotPathXY = new LineSegment2D(fromXY, new Location2D(to.getX(), to.getY()));
            LineSegment2D shotPathZY = new LineSegment2D(fromZY, new Location2D(to.getZ(), to.getY()));
            Location2D[] cfXYInter = getCloseAndFarIntersect(fromXY, shotPathXY, outlineXY);
            Location2D[] cfZYInter = getCloseAndFarIntersect(fromZY, shotPathZY, outlineZY);
            Location2D closeXYIntersect = cfXYInter[0];
            Location2D farXYIntersect = cfXYInter[1];
            Location2D closeZYIntersect = cfZYInter[0];
            Location2D farZYIntersect = cfZYInter[1];
            if (closeXYIntersect != null && closeZYIntersect != null) {
                if (closeXZIntersect == null) {
                    closeXZIntersect = new Location2D(closeXYIntersect.getX(), closeZYIntersect.getX());
                }
                if (closeDYIntersect == null) {
                    closeDYIntersect = new Location2D(fromXZ.distance(closeXZIntersect), closeXYIntersect.getY());
                }
            }
            if (farXYIntersect != null && farZYIntersect != null) {
                if (farXZIntersect == null) {
                    farXZIntersect = new Location2D(farXYIntersect.getX(), farZYIntersect.getX());
                }
                if (farDYIntersect == null) {
                    farDYIntersect = new Location2D(fromXZ.distance(farXZIntersect), farXYIntersect.getY());
                }
            }
        }
        Location entrance = closeXZIntersect != null && closeDYIntersect != null ? new Location(from.getWorld(), closeXZIntersect.getX(), closeDYIntersect.getY(), closeXZIntersect.getY(), from.getYaw(), from.getPitch()) : null;
        Location exit = farXZIntersect != null && farDYIntersect != null ? new Location(from.getWorld(), farXZIntersect.getX(), farDYIntersect.getY(), farXZIntersect.getY(), from.getYaw(), from.getPitch()) : null;
        return new Location[] {entrance, exit};
    }

    public boolean hits(HitBox hitBox, Location entrance, Location exit) {
        if (hitBox == null || entrance == null || exit == null) {
            return false;
        }
        Location mid = new Location(entrance.getWorld(), (entrance.getX() + exit.getX()) / 2, (entrance.getY() + exit.getY()) / 2, (entrance.getZ() + exit.getZ()) / 2, entrance.getYaw(), entrance.getPitch());
        boolean Xs = MathUtility.overlap1D(hitBox.getLowestX(), hitBox.getHighestX(), mid.getX(), mid.getX());
        boolean Ys = MathUtility.overlap1D(hitBox.getLowestY(), hitBox.getHighestY(), mid.getY(), mid.getY());
        boolean Zs = MathUtility.overlap1D(hitBox.getLowestZ(), hitBox.getHighestZ(), mid.getZ(), mid.getZ());
        return Xs && Ys && Zs;
    }

    public boolean hits(HitBox hitBox, Location[] entEx) {
        if (entEx == null) {
            return false;
        }
        return hits(hitBox, entEx[0], entEx[1]);
    }

    /* TODO Checking for obstacles
     * TODO Only allow parts of the HitBox to be hit that are in range
     * TODO Speed in Blocks Per Second, as contained in ShotData
     * TODO Projectile penetration */
    public List<Hit> shoot(List<HitBox> hitBoxes) {
        List<Hit> hits = new ArrayList<Hit>();
        for (HitBox hitBox : hitBoxes) {
            hitBox.update();
            if (getClosestCorner(hitBox).distanceSquared(from) > Math.pow(data.getDistanceToTravel(), 2)) {
                continue;
            }
            Location[] entEx = getSimpleEntranceAndExit(hitBox);// TODO complex
            // if (!hits(hitBox, entEx)) {
            // continue;
            // }
            if (entEx[0] == null) {
                continue;
            }
            hits.add(new Hit(from, entEx[0], entEx[1], hitBox, data));
        }
        return hits;
    }

    private Location2D[] getCloseAndFarIntersect(Location2D from, LineSegment2D shotPath, LineSegment2D[] outline) {
        double distSq = Double.MAX_VALUE;
        Location2D closeIntersect = null;
        Location2D farIntersect = null;
        for (LineSegment2D segment : outline) {
            Location2D intersect = segment.getIntersect(shotPath);
            if (intersect == null) {
                continue;
            }
            double currentDistSq = from.distanceSquared(intersect);
            if (farIntersect != null && from.distanceSquared(farIntersect) <= distSq) {
                Location2D temporary = closeIntersect;
                closeIntersect = farIntersect;
                farIntersect = temporary;
            }
            if (currentDistSq < distSq) {
                distSq = currentDistSq;
                farIntersect = closeIntersect;
                closeIntersect = intersect;
            }
        }
        return new Location2D[] {closeIntersect, farIntersect};
    }

    private Location getCornerClockwise(HitBox hitBox, int index) {
        return hitBox.getCorner(orderClockwise[index]);
    }

    private int getLeftViewCornerIndex(HitBox hitBox) {
        double leftViewCornerYaw = 0;
        int leftViewCornerIndex = 0;
        for (int index = 0; index < orderClockwise.length; index++) {
            int number = orderClockwise[index];
            Location corner = hitBox.getCorner(number);
            double yawToCorner = Math.atan2(corner.getX() - from.getX(), corner.getZ() - from.getZ()) * 180 / Math.PI;// flipped x and z from normal
            if (yawToCorner > leftViewCornerYaw || Math.abs(yawToCorner - leftViewCornerYaw) > 180) {
                leftViewCornerYaw = yawToCorner;
                leftViewCornerIndex = index;
            }
        }
        return leftViewCornerIndex;
    }
}
