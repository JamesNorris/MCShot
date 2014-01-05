package com.github.jamesnorris.mcshot;

import org.bukkit.World;
import org.bukkit.block.Biome;

public interface ShotData {
    public double getDamage(double distance);

    // TODO add comments that let the user know that all yaw and pitch arguments passed should be in normalized (real) form, and not notch's nonsense
    public double getDeltaX(double distance, float originalYaw);

    public double getDeltaY(double distance, float originalPitch);

    public double getDeltaZ(double distance, float originalYaw);

    /**
     * The distance to travel, in blocks from the shot location. Blocks can be expressed as meters in this situation.
     * @return The distance to travel, in blocks or meters.
     */
    public double getDistanceToTravel();

    public double getInitialPenetration();

    public double getPenetration(double distance);

    public float getSpeedBPS(double distance);

    public float getWindCompassDirection(World world);

    public float getWindSpeedMPH(World world);

    public float getWindSpeedMPH(World world, Biome[] biomes);
}
