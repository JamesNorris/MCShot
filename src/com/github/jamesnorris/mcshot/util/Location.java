package com.github.jamesnorris.mcshot.util;

import org.bukkit.World;

public class Location extends org.bukkit.Location {
    private Location worldOrigin;

    public Location(World world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    public Location(World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
        worldOrigin = new Location(world, 0, 0, 0, 0, 0);
    }

    public Location relativeTo(Location other) {
        return (Location) clone().subtract(other);
    }

    public Location relativeToOrigin() {
        return relativeTo(worldOrigin);
    }

    public Location rotate(float degreesX, float degreesY, float degreesZ) {
        rotateX(degreesX);
        rotateY(degreesY);
        rotateZ(degreesZ);
        return this;
    }

    public Location rotateX(float degrees) {
        double y = getY(), z = getZ();
        double rads = Math.toRadians(degrees);
        setY(y * Math.cos(rads) - z * Math.sin(rads));
        setZ(y * Math.sin(rads) + z * Math.cos(rads));
        return this;
    }

    public Location rotateY(float degrees) {
        double x = getX(), z = getZ();
        double rads = Math.toRadians(degrees);
        setX(x * Math.cos(rads) + z * Math.sin(rads));
        setZ(-x * Math.sin(rads) + z * Math.cos(rads));
        return this;
    }

    public Location rotateZ(float degrees) {
        double x = getX(), y = getY();
        double rads = Math.toRadians(degrees);
        setX(x * Math.cos(rads) - y * Math.sin(rads));
        setY(x * Math.sin(rads) + y * Math.cos(rads));
        return this;
    }
}
