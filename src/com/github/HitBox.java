package com.github;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

public class HitBox {
    public static final int LOW_X_Y_Z = 0, HIGH_X_LOW_Y_Z = 1, HIGH_Y_LOW_X_Z = 2, HIGH_Z_LOW_X_Y = 3, HIGH_X_Z_LOW_Y = 4, HIGH_X_Y_LOW_Z = 5, HIGH_X_Y_Z = 6, HIGH_Y_Z_LOW_X = 7;
    private float yawRotation;
    private double x, y, z;
    private double[][] additions;
    private Location center;
    private List<Location> corners = new ArrayList<Location>();
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
     * An invisible box in the world that can be hit with a shot.
     * Additionally, {@link DataZone} instances can be added to this, 
     * allowing for different damage and thickness on an area of the box.
     * 
     * @param origin The current location of the origin of this box in the world TODO add a constructor with length, width, height, and have the origin found instead of provided
     * @param center The center of the prism
     * @param yawRotation The rotation around the center of the origin (or any other point)
     */
    public HitBox(Location origin, Location center, float yawRotation) {
        double x = origin.getX() - center.getX();
        double y = origin.getY() - center.getY();
        double z = origin.getZ() - center.getZ();
        
        additions = new double[][] {{0, 0, 0}, {x, 0, 0}, {0, y, 0}, {0, 0, z}, {x, 0, z}, {x, y, 0}, {x, y, z}, {0, y, z}};
        for (int i = 0; i < 8; i++) {
            double[] addition = additions[i];
            
            double xPrime = center.getX() + (center.getX() - (origin.getX() + addition[0])) * Math.cos(Math.toRadians(yawRotation)) - (center.getZ() - (origin.getZ() + addition[2])) * Math.sin(Math.toRadians(yawRotation));
            double zPrime = center.getZ() + (center.getX() - (origin.getX() + addition[0])) * Math.sin(Math.toRadians(yawRotation)) + (center.getZ() - (origin.getZ() + addition[2])) * Math.cos(Math.toRadians(yawRotation));
            
            corners.add(new Location(center.getWorld(), xPrime, origin.getY() + addition[1], zPrime, yawRotation, 0));
        }
        
        this.center = center;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yawRotation %= 360;
    }
    //@formatter:on
    public List<Location> getCorners() {
        return corners;
    }

    public Location getCorner(int corner) {
        return corners.get(corner);
    }
    
    public Location getOrigin() {
        return corners.get(0);
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
        Location origin = getCorner(0);
        this.yawRotation = (yawRotation + degrees) % 360;
        int cornersSize = corners.size();
        corners.clear();
        for (int i = 0; i < cornersSize; i++) {
            double[] addition = additions[i];
            double xPrime = center.getX() + (center.getX() - (origin.getX() + addition[0])) * Math.cos(Math.toRadians(yawRotation)) - (center.getZ() - (origin.getZ() + addition[2])) * Math.sin(Math.toRadians(yawRotation));
            double zPrime = center.getZ() + (center.getX() - (origin.getX() + addition[0])) * Math.sin(Math.toRadians(yawRotation)) + (center.getZ() - (origin.getZ() + addition[2])) * Math.cos(Math.toRadians(yawRotation));
            corners.add(new Location(center.getWorld(), xPrime, origin.getY() + addition[1], zPrime, yawRotation, 0));
        }
    }

    public void move(Location center) {
        double deltaX = center.getX() - this.center.getX();
        double deltaY = center.getY() - this.center.getY();
        double deltaZ = center.getZ() - this.center.getZ();
        for (int i = 0; i < corners.size(); i++) {
            corners.get(i).add(deltaX, deltaY, deltaZ);
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

    public float getYawRotation() {
        return yawRotation;
    }
}
