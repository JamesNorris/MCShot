package com.github.jamesnorris.mcshot.util;

public class Location2D {
    private double x, y;

    public Location2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distance(Location2D other) {
        return Math.sqrt(distanceSquared(other));
    }

    public double distanceSquared(Location2D other) {
        return Math.pow(x - other.getX(), 2) + Math.pow(y - other.getY(), 2);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Location2D rotate(float degrees) {
        double rads = Math.toRadians(degrees);
        x = x * Math.cos(rads) - y * Math.sin(rads);
        y = x * Math.sin(rads) + y * Math.cos(rads);
        return this;
    }

    @Override public String toString() {
        return "Location2D:(" + x + ", " + y + ")";
    }
}
