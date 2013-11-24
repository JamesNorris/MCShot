package com.github.jamesnorris.mcshot;

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

    @Override public String toString() {
        return "Location2D:(" + x + ", " + y + ")";
    }
}
