package com.github.jamesnorris.mcshot;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import com.github.jamesnorris.mcshot.util.LineSegment3D;
import com.github.jamesnorris.mcshot.util.Location;
import com.github.jamesnorris.mcshot.util.Plane3D;

public class Zone {
    private Plane3D[] planes = new Plane3D[6];
    private Vector from, center, to;
    private double rotationX, rotationY, rotationZ;

    public Zone(Location from, Location to) {
        this(from.toVector(), to.toVector());
    }

    public Zone(Vector from, Vector to) {
        this.from = from;
        this.to = to;
        center = from.midpoint(to);
        updatePlanes();
    }

    public boolean contains(Vector point) {
        return contained1D(point.getX(), from.getX(), to.getX())
                && contained1D(point.getY(), from.getY(), to.getY())
                && contained1D(point.getZ(), from.getZ(), to.getZ());
    }

    public Vector getCenter() {
        return center;
    }

    public Vector getFrom() {
        return from;
    }

    public List<Vector> getIntersects(LineSegment3D segment) {
        List<Vector> intersects = new ArrayList<Vector>();
        for (Plane3D plane : planes) {
            Vector intersect = plane.getIntersect(segment);
            if (intersect != null) {
                intersects.add(intersect);
            }
        }
        return intersects;
    }

    public Plane3D[] getPlanes() {
        return planes;
    }

    public double getRotationX() {
        return rotationX;
    }

    public double getRotationY() {
        return rotationY;
    }

    public double getRotationZ() {
        return rotationZ;
    }

    public Vector getTo() {
        return to;
    }

    public void moveTo(Vector center) {
        Vector move = center.clone().subtract(this.center);
        from.add(move);
        to.add(move);
        this.center.add(move);
        updatePlanes();
    }

    public void rotate(float degreesX, float degreesY, float degreesZ) {
        ((Location) from.toLocation(Bukkit.getWorlds().get(0))).rotate(degreesX, degreesY, degreesZ);
        ((Location) to.toLocation(Bukkit.getWorlds().get(0))).rotate(degreesX, degreesY, degreesZ);
        ((Location) center.toLocation(Bukkit.getWorlds().get(0))).rotate(degreesX, degreesY, degreesZ);
        rotationX += degreesX;
        rotationY += degreesY;
        rotationZ += degreesZ;
        updatePlanes();
    }

    @Override public String toString() {
        return "Zone:(from: " + from + ", center: " + center + ", to: " + to
                + ")";
    }

    private boolean contained1D(double check, double one, double two) {
        return check > Math.min(one, two) && check < Math.max(one, two);
    }

    protected void updatePlanes() {
        planes[0] = new Plane3D(from, to, new Vector(from.getX(), to.getY(), to.getZ()));
        planes[1] = new Plane3D(from, to, new Vector(from.getX(), from.getY(), to.getZ()));
        planes[2] = new Plane3D(from, to, new Vector(from.getX(), to.getY(), from.getZ()));
        planes[3] = new Plane3D(from, to, new Vector(from.getX(), from.getY(), from.getZ()));
        planes[4] = new Plane3D(from, to, new Vector(to.getX(), to.getY(), to.getZ()));
        planes[5] = new Plane3D(from, to, new Vector(to.getX(), from.getY(), from.getZ()));
    }
}
