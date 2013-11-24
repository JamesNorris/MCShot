package com.github.jamesnorris.mcshot.type;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.World;

import com.github.jamesnorris.mcshot.ShotData;

public class Realistic308ShotData implements ShotData {
    private Random random = new Random();
    private Map<UUID, Float> worldWindCompassDirections = new HashMap<UUID, Float>();

    @Override public float getWindSpeedMPH(World world) {//TODO check for biomes
        float windSpeed = random.nextInt(2);
        if (world.hasStorm()) {
            windSpeed += random.nextInt(5);
        }
        return windSpeed;
    }

    @Override public float getWindCompassDirection(World world) {
        if (worldWindCompassDirections.containsKey(world.getUID())) {
            return worldWindCompassDirections.get(world.getUID());
        }
        float direction = random.nextInt(359);
        worldWindCompassDirections.put(world.getUID(), direction);
        return direction;
    }

    @Override public double getDistanceToTravel() {
        return 2280.5;
    }
    
    @Override public double getStartingPenetration() {
        return 48;
    }

    @Override public double getPenetration(double current, double distance) {
        return distance < 1000 ? current - (31 - 10 * Math.log10(distance)) : 1;
    }

    @Override public double getDamage(double distance) {
        return (-.3 * distance) + 30;
    }

    @Override public float getSpeedBPS(double distance) {
        return (float) ((-3 * distance) + 430);
    }

    @Override public double getDeltaX(double distance, float originalYaw) {       
        return (((random.nextInt(10) - 5) / 1000) * distance) * Math.abs(Math.abs(Math.sin(.506148 * Math.toRadians(originalYaw))) - 1);
    }

    @Override public double getDeltaY(double distance, float originalPitch) {
        return ((.05 / getDistanceToTravel()) * Math.pow(distance, 2)) + (originalPitch / 100);
    }

    @Override public double getDeltaZ(double distance, float originalYaw) {
        return (((random.nextInt(10) - 5) / 1000) * distance) * Math.abs(Math.sin(.506148 * Math.toRadians(originalYaw)));
    }    
}
