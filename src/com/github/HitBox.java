package com.github;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

public class HitBox {
    private float yawRotation;
    private double x, y, z;
    private double[][] additions;
    private Location center;
    private Location[] corners = new Location[8];
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
     * O-------------------X - - - - - - - - - -270 yaw
     */
    
    /**
     * 
     * An invisible box in the world that can be hit with {@code Shot.shoot(List<HitBox> hitBoxes)}.
     * Additionally, {@link DataZone} instances can be added to this, 
     * allowing for different damage and thickness on an area of the box.
     * 
     * @param center The exact center of the hit box
     * @param length The length (z axis) of the hit box
     * @param width The width (x axis) of the hit box
     * @param height The height (y axis) of the hit box
     * @param yawRotation The rotation around the center of the origin (or any other point)
     */
    public HitBox(Location center, double length, double width, double height, float yawRotation) {  
        corners[0] = center.clone().add(-1 * width / 2, -1 * height / 2, -1 * length / 2);
        this.center = center;
        this.x = width;
        this.y = height;
        this.z = length;
        rotate(yawRotation);
    }
    //@formatter:on
    public Location[] getCorners() {
        return corners;
    }

    public Location getCorner(int corner) {
        return corners[corner];
    }

    public Location getOrigin() {
        return corners[0];
    }

    public List<DataZone> getDataZones() {
        return dataZones;
    }

    public boolean addDataZone(DataZone zone) {
        return isZoneOpen(zone) && dataZones.add(zone);
    }

    public void clearDataZones() {
        dataZones.clear();
    }

    public UUID getUUID() {
        return uuid;
    }

    public void update() {};

    public DataZone getZone(float x, float y, float z) {
        for (DataZone zone : dataZones) {
            boolean betweenX = x < zone.xTo && x > zone.xFrom;
            boolean betweenY = y < zone.yTo && y > zone.yFrom;
            boolean betweenZ = z < zone.zTo && z > zone.zFrom;
            if (betweenX && betweenY && betweenZ) {
                return zone;
            }
        }
        return null;
    }

    public boolean isZoneOpen(DataZone zone) {
        for (DataZone placed : dataZones) {
            boolean Xs = overlap_1D(placed.xFrom, placed.xTo, zone.xFrom, zone.xTo);
            boolean Ys = overlap_1D(placed.yFrom, placed.yTo, zone.yFrom, zone.yTo);
            boolean Zs = overlap_1D(placed.zFrom, placed.zTo, zone.zFrom, zone.zTo);
            if (Xs && Ys && Zs) {
                return true;
            }
        }
        return false;
    }

    private boolean overlap_1D(double low1, double high1, double low2, double high2) {
        return low1 <= low2 ? low2 <= high1 : low1 <= high2;
    }

    public boolean hasDataZone(float x, float y, float z) {
        return getZone(z, y, z) != null;
    }

    public void rotate(float degrees) {
        Location origin = corners[0];
        this.yawRotation = (yawRotation + degrees) % 360;
        additions = new double[][] { {0, 0, 0}, {x, 0, 0}, {0, y, 0}, {0, 0, z}, {x, 0, z}, {x, y, 0}, {x, y, z}, {0, y, z}};
        for (int i = 0; i < 8; i++) {
            double[] addition = additions[i];
            double xPrime = center.getX() + (center.getX() - (origin.getX() + addition[0])) * Math.cos(Math.toRadians(yawRotation)) - (center.getZ() - (origin.getZ() + addition[2])) * Math.sin(Math.toRadians(yawRotation));
            double zPrime = center.getZ() + (center.getX() - (origin.getX() + addition[0])) * Math.sin(Math.toRadians(yawRotation)) + (center.getZ() - (origin.getZ() + addition[2])) * Math.cos(Math.toRadians(yawRotation));
            corners[i] = new Location(center.getWorld(), xPrime, origin.getY() + addition[1], zPrime, yawRotation, 0);
        }
    }

    public void move(Location center) {
        double deltaX = center.getX() - this.center.getX();
        double deltaY = center.getY() - this.center.getY();
        double deltaZ = center.getZ() - this.center.getZ();
        for (int i = 0; i < 8; i++) {
            corners[i].add(deltaX, deltaY, deltaZ);
        }
        this.center = center;
    }

    public Location getCenter() {
        return center;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
    
    protected void setY(double y) {
        int[] toChange = new int[] {2, 5, 6, 7};
        for (int i : toChange) {
            corners[i].setY(corners[0].getY() + y);
        }
        this.y = y;
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
        return corners[0].getY() + y;
    }

    public double getHighestZ() {
        double highestZ = Double.MIN_VALUE;
        for (Location location : corners) {
            if (location.getZ() > highestZ) {
                highestZ = location.getZ();
            }
        }
        return highestZ;
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

    public float getYawRotation() {
        return yawRotation;
    }
}
