package com.github.jamesnorris.mcshot;

import org.bukkit.Location;

public class LineSegment3D {
    private Location start, end;

    public LineSegment3D(Location start, Location end) {
        this.start = start;
        this.end = end;
    }

    public Location getEnd() {
        return end;
    }

    public Location getStart() {
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
}
