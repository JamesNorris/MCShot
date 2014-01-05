package com.github.jamesnorris.mcshot.util;

import org.bukkit.util.Vector;

public class LineSegment3D {
    private Vector start, end;

    public LineSegment3D(Location start, Location end) {
        if (!start.getWorld().equals(end.getWorld())) {
            throw new IllegalArgumentException("Start and end must be in the same world.");
        }
        this.start = start.toVector();
        this.end = end.toVector();
    }

    public LineSegment3D(Vector point1, Vector point2) {
        start = point1;
        end = point2;
    }

    public Vector getEnd() {
        return end;
    }

    public Vector getStart() {
        return start;
    }

    public LineSegment2D getXYComponent() {
        return new LineSegment2D(new Location2D(start.getX(), start.getY()), new Location2D(end.getX(), end.getY()));
    }

    public LineSegment2D getXZComponent() {
        return new LineSegment2D(new Location2D(start.getX(), start.getZ()), new Location2D(end.getX(), end.getZ()));
    }

    public LineSegment2D getYZComponent() {
        return new LineSegment2D(new Location2D(start.getY(), start.getZ()), new Location2D(end.getY(), end.getZ()));
    }

    @Override public String toString() {
        return "LineSegment3D:(from: " + start + ", to: " + end + ")";
    }
}
