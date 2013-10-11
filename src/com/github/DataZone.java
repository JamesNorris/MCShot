package com.github;

import java.util.UUID;

public class DataZone {
    private final UUID uuid = UUID.randomUUID();
    
    public UUID getUUID() {
        return uuid;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getThickness() {
        return thickness;
    }

    public void setThickness(double thickness) {
        this.thickness = thickness;
    }

    public float xFrom, xTo, yFrom, yTo, zFrom, zTo;
    private double multiplier, thickness;
    
    public DataZone(float xFrom, float xTo, float yFrom, float yTo, float zFrom, float zTo) {
        this(xFrom, xTo, yFrom, yTo, zFrom, zTo, 1, 1);
    }
    
    public DataZone(float xFrom, float xTo, float yFrom, float yTo, float zFrom, float zTo, double multiplier, double thickness) {
        this.xFrom = xFrom;
        this.xTo = xTo;
        this.yFrom = yFrom;
        this.yTo = yTo;
        this.zFrom = zFrom;
        this.zTo = zTo;
        this.multiplier = multiplier;
        this.thickness = thickness;
    }
}
