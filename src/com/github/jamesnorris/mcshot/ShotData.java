package com.github.jamesnorris.mcshot;

import org.bukkit.World;

public interface ShotData {
    public double getDamage(double distance);

    // TODO add comments that let the user know that all yaw and pitch arguments passed should be in normalized (real) form, and not notch's nonsense
    public double getDeltaX(double distance, float originalYaw);

    public double getDeltaY(double distance, float originalPitch);

    public double getDeltaZ(double distance, float originalYaw);

    public double getDistanceToTravel();

    public double getPenetration(double current, double distance);

    public float getSpeedBPS(double distance);

    public double getStartingPenetration();

    public float getWindCompassDirection(World world);

    public float getWindSpeedMPH(World world);
}
