package com.github.jamesnorris.mcshot.util;

import org.bukkit.util.Vector;

public class Plane3D {
    private static final Vector ZERO_VECTOR = new Vector(0, 0, 0);
    private Vector normal, point;
    private double distance, a, b, c, d;

    public Plane3D(Location loc1, Location loc2, Location loc3) {
        this(loc1.toVector(), loc2.toVector(), loc3.toVector());
    }

    public Plane3D(Vector normal, Vector point) {
        this.normal = normal;
        this.point = point;
        distance = -normal.dot(point);
        findCoefficients();
    }

    public Plane3D(Vector point1, Vector point2, Vector point3) {
        Vector l = point1.clone().subtract(point2);
        Vector r = point2.clone().subtract(point3);
        normal = l.crossProduct(r).normalize();
        point = point1;
        distance = -point1.dot(normal);
        findCoefficients();
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
    }

    public double getDihedralAngle(Plane3D other) {
        if (normal.crossProduct(other.normal).equals(ZERO_VECTOR)) {
            return Double.NaN;
        }
        return (a * other.a + b * other.b + c * other.c)
                / (Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2) + Math.pow(c, 2)) * Math.sqrt(Math.pow(other.a, 2)
                        + Math.pow(other.b, 2) + Math.pow(other.c, 2)));
    }

    public double getDistance(Vector point) {
        return Math.abs(a * point.getX() + b * point.getY() + c * point.getZ()
                + d)
                / Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2) + Math.pow(c, 2));
    }

    public Vector getIntersect(LineSegment3D segment) {
        Vector u = segment.getEnd().subtract(segment.getStart());
        Vector w = segment.getStart().subtract(point);
        double D = normal.dot(u);
        double N = -normal.dot(w);
        if (Math.abs(D) < .00000001) {
            /* if N == 0, segment lies in the plane */
            return null;
        }
        double sI = N / D;
        if (sI < 0 || sI > 1) {
            return null;
        }
        return segment.getStart().add(u.multiply(sI));
    }

    public LineSegment3D getIntersect(Plane3D other) {
        Vector u = normal.multiply(other.normal);
        double ax = u.getX() >= 0 ? u.getX() : -u.getX();
        double ay = u.getY() >= 0 ? u.getY() : -u.getY();
        double az = u.getZ() >= 0 ? u.getZ() : -u.getZ();
        if (ax + ay + az < .00000001) {
            return null;
            // Vector v = other.point.subtract(point);
            // if (dot(normal, v) == 0) { // other.point lies in this plane
            // return null; // coincide
            // } else {
            // return null; // disjoint
            // }
        }
        int maxc = ax > ay ? ax > az ? 1 : 3 : ay > az ? 2 : 3;
        Vector iP = new Vector(0, 0, 0);
        double d1 = -normal.dot(point);
        double d2 = -normal.dot(other.point);
        switch (maxc) {
            case 1:
                iP.setY((d2 * normal.getZ() - d1 * other.normal.getZ())
                        / u.getX());
                iP.setZ((d1 * other.normal.getY() - d2 * normal.getY())
                        / u.getX());
            break;
            case 2:
                iP.setX((d1 * other.normal.getZ() - d2 * normal.getZ())
                        / u.getY());
                iP.setZ((d2 * normal.getX() - d1 * other.normal.getX())
                        / u.getY());
            break;
            case 3:
                iP.setX((d2 * normal.getY() - d1 * other.normal.getY())
                        / u.getZ());
                iP.setY((d1 * other.normal.getX() - d2 * normal.getX())
                        / u.getZ());
            break;
        }
        return new LineSegment3D(iP, iP.clone().add(u));
    }

    public Vector getNormal() {
        return normal;
    }

    @Override public String toString() {
        return "Plane3D:(n: " + normal + ", d: " + distance + ", equation: " + a + "x + " + b + "y + " + c + "z + " + d + ")";
    }

    private void findCoefficients() {
        Vector dir = normal.multiply(distance);
        a = dir.getX();
        b = dir.getY();
        c = dir.getZ();
        d = -(point.getX() * a + point.getY() * b + point.getZ() * c);
    }
}
