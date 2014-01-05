package com.github.jamesnorris.mcshot.type;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.block.Biome;

import com.github.jamesnorris.mcshot.ShotData;

public abstract class RealisticShotData implements ShotData {
    protected Random random = new Random();
    private Map<UUID, Float> worldWindCompassDirections = new HashMap<UUID, Float>();

    @Override public float getWindCompassDirection(World world) {
        if (worldWindCompassDirections.containsKey(world.getUID())) {
            return worldWindCompassDirections.get(world.getUID());
        }
        float direction = random.nextInt(359);
        worldWindCompassDirections.put(world.getUID(), direction);
        return direction;
    }

    @Override public float getWindSpeedMPH(World world) {
        float windSpeed = random.nextInt(2);
        if (world.hasStorm()) {
            windSpeed += random.nextInt(10);
        }
        return windSpeed;
    }

    @Override public float getWindSpeedMPH(World world, Biome[] biomes) {// TODO check for biomes
        float windSpeed = random.nextInt(2);
        if (world.hasStorm()) {
            for (Biome biome : biomes) {
                switch (biome) {
                    case EXTREME_HILLS:
                    case FOREST:
                    case FOREST_HILLS:
                    case JUNGLE:
                    case JUNGLE_HILLS:
                    case MUSHROOM_ISLAND:
                    case MUSHROOM_SHORE:
                    case PLAINS:
                    case RIVER:
                    case SMALL_MOUNTAINS:
                    case BEACH:
                    case DESERT:
                    case DESERT_HILLS:
                    break;
                    case HELL:
                        windSpeed += 4.5;
                    break;
                    case OCEAN:
                        windSpeed += 18;
                    break;
                    case SKY:
                        windSpeed += 12;
                    break;
                    case SWAMPLAND:
                        windSpeed += 7.5;
                    break;
                    case FROZEN_OCEAN:
                    case FROZEN_RIVER:
                    case ICE_MOUNTAINS:
                    case ICE_PLAINS:
                    case TAIGA:
                    case TAIGA_HILLS:
                        windSpeed += 10;
                    break;
                    default:
                    break;
                }
            }
        }
        return windSpeed;
    }
}
