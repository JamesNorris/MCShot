package com.github.jamesnorris.mcshot;

public class LineSegment2D {
    public static Location2D getIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (denom == 0.0D) { // parallel
            return null;
        }
        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;
        if (ua >= 0.0D && ua <= 1.0D && ub >= 0.0D && ub <= 1.0D) {
            return new Location2D(x1 + ua * (x2 - x1), y1 + ua * (y2 - y1));
        }
        return null;
    }

    public static Location2D getIntersect(LineSegment2D line1, LineSegment2D line2) {
        Location2D start1 = line1.getStart();
        Location2D end1 = line1.getEnd();
        Location2D start2 = line2.getStart();
        Location2D end2 = line2.getEnd();
        return getIntersect(start1.getX(), start1.getY(), end1.getX(), end1.getY(), start2.getX(), start2.getY(), end2.getX(), end2.getY());
    }

    private double x1, x2, y1, y2;

    public LineSegment2D(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public LineSegment2D(Location2D loc1, Location2D loc2) {
        this(loc1.getX(), loc1.getY(), loc2.getX(), loc2.getY());
    }

    public Location2D getEnd() {
        return new Location2D(x2, y2);
    }

    public Location2D getIntersect(LineSegment2D other) {
        return getIntersect(this, other);
    }

    public double getSlope() {
        return (y2 - y1) / (x2 - x1);
    }

    public Location2D getStart() {
        return new Location2D(x1, y1);
    }
}
