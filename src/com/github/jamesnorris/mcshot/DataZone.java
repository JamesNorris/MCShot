package com.github.jamesnorris.mcshot;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.util.Vector;

import com.github.jamesnorris.mcshot.util.Location;

public class DataZone extends Zone {
    private final UUID uuid = UUID.randomUUID();
    private World world;
    public double multiplier, distanceMultiplier;

    public DataZone(Location from, Location to, double distanceMultiplier, double thickness) {
        this(from.getWorld(), from.toVector(), to.toVector(), distanceMultiplier, thickness);
        if (!from.getWorld().equals(to.getWorld())) {
            throw new IllegalArgumentException("From and to must be in the same world.");
        }
    }

    public DataZone(World world, Vector from, Vector to, double multiplier, double distanceMultiplier) {
        super(from, to);
        this.world = world;
        this.multiplier = multiplier;
        this.distanceMultiplier = distanceMultiplier;
    }

    public UUID getUUID() {
        return uuid;
    }

    public World getWorld() {
        return world;
    }
}
