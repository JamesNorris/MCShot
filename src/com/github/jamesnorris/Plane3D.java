package com.github.jamesnorris;

import org.bukkit.Location;
import org.bukkit.World;

public class Plane3D {
    private Location[] corners = new Location[4];

    public Plane3D(Location loc1, Location loc2, Location loc3, Location loc4) {
        corners[0] = loc1;// TODO position these corners properly (top left is 1, top right is 2...)
        corners[1] = loc2;
        corners[2] = loc3;
        corners[3] = loc4;
    }

    public Location getIntersect(LineSegment3D line) {
        World world = line.getStart().getWorld();
        double x = Double.NaN;
        double y = Double.NaN;
        double z = Double.NaN;
        float yaw = line.getStart().getYaw();
        float pitch = line.getStart().getPitch();
        // Location[] segIntersects = new Location[4];
        LineSegment3D[] outline = outliningSegments();
        for (int index = 0; index < outline.length; index++) {
            LineSegment2D xyComponent = outline[index].getXYComponent();
            LineSegment2D xzComponent = outline[index].getXZComponent();
            LineSegment2D yzComponent = outline[index].getYZComponent();
            Location2D xyIntersect = line.getXYComponent().getIntersect(xyComponent);
            Location2D xzIntersect = line.getXZComponent().getIntersect(xzComponent);
            Location2D yzIntersect = line.getYZComponent().getIntersect(yzComponent);
            if (xyIntersect != null) {
                if (x == Double.NaN) {
                    x = xyIntersect.getX();
                }
                if (y == Double.NaN) {
                    y = xyIntersect.getY();
                }
            }
            if (xzIntersect != null) {
                if (x == Double.NaN) {
                    x = xzIntersect.getX();
                }
                if (z == Double.NaN) {
                    z = xzIntersect.getY();
                }
            }
            if (yzIntersect != null) {
                if (y == Double.NaN) {
                    y = yzIntersect.getX();
                }
                if (z == Double.NaN) {
                    z = yzIntersect.getY();
                }
            }
            // System.out.println("XY: " + xyIntersect + "\nXZ: " + xyIntersect + "\nYZ: " + yzIntersect);// TODO remove
            // if ((xyIntersect == null && xzIntersect == null) || (xyIntersect == null && yzIntersect == null) || (xzIntersect == null && yzIntersect == null)) {
            // System.out.println("cannot find any intersects");//TODO remove
            // return null;
            // }
            // segIntersects[index] = new Location(world, xyIntersect != null ? xyIntersect.getX() : xzIntersect != null ? xzIntersect.getX() : Double.NaN, xyIntersect != null ?
            // xyIntersect.getY() : yzIntersect != null ? yzIntersect.getX() : Double.NaN, xzIntersect != null ? xzIntersect.getY() : yzIntersect != null ? yzIntersect.getY() :
            // Double.NaN, yaw, pitch);
            if (x != Double.NaN && y != Double.NaN && z != Double.NaN) {
                break;
            }
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    public LineSegment3D[] outliningSegments() {
        LineSegment3D seg1 = new LineSegment3D(corners[0], corners[1]);
        LineSegment3D seg2 = new LineSegment3D(corners[1], corners[2]);
        LineSegment3D seg3 = new LineSegment3D(corners[2], corners[3]);
        LineSegment3D seg4 = new LineSegment3D(corners[3], corners[1]);
        return new LineSegment3D[] {seg1, seg2, seg3, seg4};
    }
}
