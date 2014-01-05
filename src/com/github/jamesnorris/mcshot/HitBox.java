package com.github.jamesnorris.mcshot;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.util.Vector;

import com.github.jamesnorris.mcshot.util.Location;
import com.github.jamesnorris.mcshot.util.MathUtility;

public class HitBox extends Zone {
    private World world;
    private List<Zone> zones = new ArrayList<Zone>();

    //@formatter:off
    /*
     * O = origin (originally)
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
    //@formatter:on
    /**
     * An invisible box in the world that can be hit with a shot.
     * Additionally, {@link Zone} instances can be added to this,
     * allowing for different damage and thickness on an area of the box.
     * 
     * @param center The center of the hit box
     * @param length The length (z axis) of the hit box
     * @param width The width (x axis) of the hit box
     * @param height The height (y axis) of the hit box
     * @param yawRotation The rotation around the center
     */
    public HitBox(Location center, double length, double width, double height, float yawRotation) {
        super((Location) center.clone().add(-1 * width / 2, -1 * height / 2, -1
                * length / 2), (Location) center.clone().add(width / 2, height / 2, length / 2));
        world = center.getWorld();
        rotate(0, MathUtility.absDegrees(yawRotation), 0);
    }

    public boolean addZone(Zone zone) {
        return zones.add(zone);
    }

    public void clearZones() {
        zones.clear();
    }

    public World getWorld() {
        return world;
    }

    public List<Zone> getZones() {
        return zones;
    }

    @Override public void moveTo(Vector center) {
        super.moveTo(center);
        for (Zone zone : zones) {
            zone.moveTo(center);
        }
    }

    public void rotate(float degrees) {
        rotate(0, degrees, 0);
    }

    @Override public void rotate(float degreesX, float degreesY, float degreesZ) {
        super.rotate(degreesX, degreesY, degreesZ);
        for (Zone zone : zones) {
            zone.rotate(degreesX, degreesY, degreesZ);
        }
    }

    @Override public String toString() {
        return "HitBox:(" + super.toString().substring(6);
    }

    public void update() {}
}
