package com.github.jamesnorris.mcshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

public class HitBox {
    private float yawRotation;
    private double width, height, length;
    private double[][] additions;
    private Location center;
    private Location[] corners = new Location[8];
    private Plane3D[] planes = new Plane3D[6];
    private List<DataZone> dataZones = new ArrayList<DataZone>();
    private UUID uuid = UUID.randomUUID();

    //@formatter:off
    /*
     * O = origin
     * X = x-axis
     * Y = y-axis
     * Z = z-axis
     * C = center
     * 
     *    ---------------------
     *   /                   /|
     *  /                   / |
     * Y--------------------  |
     * |                90 |  |     0 yaw
     * |   ^               |  |    /
     * |   |               |  |
     * |   |               |  |  /
     * | HEIGHT    C       |  |
     * |   |               |  |/
     * |   |               |  Z
     * |   v               | /
     * |   <---WIDTH--->   |/<---LENGTH
     * O-------------------X - - - - - - - - -  270 yaw
     */
    
    /**
     * An invisible box in the world that can be hit with a shot.
     * Additionally, {@link DataZone} instances can be added to this, 
     * allowing for different damage and thickness on an area of the box.
     * 
     * @param center The center of the hit box
     * @param length The length (z axis) of the hit box
     * @param width The width (x axis) of the hit box
     * @param height The height (y axis) of the hit box
     * @param yawRotation The rotation around the center of the origin (or any other point)
     */
    public HitBox(Location center, double length, double width, double height, float yawRotation) {
        this.center = new Location(center.getWorld(), center.getX(), center.getY(), center.getZ(), MathUtility.absDegrees(yawRotation), 0);
        corners[0] = this.center.clone().add(-1 * width / 2, -1 * height / 2, -1 * length / 2);
        this.width = width;
        this.height = height;
        this.length = length;
        rotate(MathUtility.absDegrees(yawRotation));
    }
    public boolean addDataZone(DataZone zone) {
        return isZoneOpen(zone) && dataZones.add(zone);
    }

    public void clearDataZones() {
        dataZones.clear();
    }

    public Location getCenter() {
        return center;
    }

    public Location getCorner(int corner) {
        return corners[corner];
    }

    //@formatter:on
    public Location[] getCorners() {
        return corners;
    }

    public List<DataZone> getDataZones() {
        return dataZones;
    }

    public double getHeight() {
        return height;
    }

    public double getHighestX() {
        double highestX = Double.MIN_VALUE;
        for (Location location : corners) {
            if (location.getX() > highestX) {
                highestX = location.getX();
            }
        }
        return highestX;
    }

    public double getHighestY() {
        return corners[0].getY() + height;
    };

    public double getHighestZ() {
        double highestZ = Double.MIN_VALUE;
        for (Location location : corners) {
            if (location.getZ() > highestZ) {
                highestZ = location.getZ();
            }
        }
        return highestZ;
    }

    public double getLength() {
        return length;
    }

    public double getLowestX() {
        double lowestX = Double.MAX_VALUE;
        for (Location location : corners) {
            if (location.getX() < lowestX) {
                lowestX = location.getX();
            }
        }
        return lowestX;
    }

    public double getLowestY() {
        return corners[0].getY();
    }

    public double getLowestZ() {
        double lowestZ = Double.MAX_VALUE;
        for (Location location : corners) {
            if (location.getZ() < lowestZ) {
                lowestZ = location.getZ();
            }
        }
        return lowestZ;
    }

    public Location getOrigin() {
        return corners[0];
    }

    public Plane3D[] getPlanes() {
        return planes;
    }

    public UUID getUUID() {
        return uuid;
    }

    public double getWidth() {
        return width;
    }

    public float getYawRotation() {
        return yawRotation;
    }

    public DataZone getZone(float width, float height, float length) {
        for (DataZone zone : dataZones) {
            boolean betweenX = width < zone.widthTo && width > zone.widthFrom;
            boolean betweenY = height < zone.heightTo && height > zone.heightFrom;
            boolean betweenZ = length < zone.lengthTo && length > zone.lengthFrom;
            if (betweenX && betweenY && betweenZ) {
                return zone;
            }
        }
        return null;
    }

    public boolean hasDataZone(float width, float height, float length) {
        return getZone(width, height, length) != null;
    }

    public boolean isZoneOpen(DataZone zone) {
        for (DataZone placed : dataZones) {
            boolean Xs = overlap_1D(placed.widthFrom, placed.widthTo, zone.widthFrom, zone.widthTo);
            boolean Ys = overlap_1D(placed.heightFrom, placed.heightTo, zone.heightFrom, zone.heightTo);
            boolean Zs = overlap_1D(placed.lengthFrom, placed.lengthTo, zone.lengthFrom, zone.lengthTo);
            if (Xs && Ys && Zs) {
                return true;
            }
        }
        return false;
    }

    public void move(Location center) {
        double deltaX = center.getX() - this.center.getX();
        double deltaY = center.getY() - this.center.getY();
        double deltaZ = center.getZ() - this.center.getZ();
        for (int i = 0; i < 8; i++) {
            corners[i].add(deltaX, deltaY, deltaZ);
        }
        this.center.add(deltaX, deltaY, deltaZ);
        updatePlanes();
    }

    public void rotate(float degrees) {
        Location origin = corners[0];
        yawRotation = (yawRotation + MathUtility.absDegrees(degrees)) % 360;
        additions = new double[][] { {0, 0, 0}, {width, 0, 0}, {0, height, 0}, {0, 0, length}, {width, 0, length}, {width, height, 0}, {width, height, length}, {0, height, length}};
        for (int i = 0; i < 8; i++) {
            double[] addition = additions[i];
            double xPrime = center.getX() + (center.getX() - (origin.getX() + addition[0])) * Math.cos(Math.toRadians(yawRotation))
                    - (center.getZ() - (origin.getZ() + addition[2])) * Math.sin(Math.toRadians(yawRotation));
            double zPrime = center.getZ() + (center.getX() - (origin.getX() + addition[0])) * Math.sin(Math.toRadians(yawRotation))
                    + (center.getZ() - (origin.getZ() + addition[2])) * Math.cos(Math.toRadians(yawRotation));
            corners[i] = new Location(center.getWorld(), xPrime, origin.getY() + addition[1], zPrime, yawRotation, 0);
        }
        center.setYaw(yawRotation);
        updatePlanes();
    }

    public void update() {}

    private boolean overlap_1D(double low1, double high1, double low2, double high2) {
        return low1 <= low2 ? low2 <= high1 : low1 <= high2;
    }

    private void updatePlanes() {
        planes[0] = new Plane3D(corners[0], corners[1], corners[4], corners[3]);
        planes[1] = new Plane3D(corners[2], corners[5], corners[1], corners[0]);
        planes[2] = new Plane3D(corners[7], corners[2], corners[0], corners[3]);
        planes[3] = new Plane3D(corners[6], corners[7], corners[3], corners[4]);
        planes[4] = new Plane3D(corners[5], corners[6], corners[4], corners[1]);
        planes[5] = new Plane3D(corners[2], corners[5], corners[6], corners[7]);
    }
}
