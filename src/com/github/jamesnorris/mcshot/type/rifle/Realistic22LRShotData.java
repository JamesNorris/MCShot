package com.github.jamesnorris.mcshot.type.rifle;

import com.github.jamesnorris.mcshot.type.RealisticShotData;

public class Realistic22LRShotData extends RealisticShotData {
    @Override public double getDamage(double distance) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override public double getDeltaX(double distance, float originalYaw) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override public double getDeltaY(double distance, float originalPitch) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override public double getDeltaZ(double distance, float originalYaw) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override public double getDistanceToTravel() {
        return 140;
    }

    @Override public double getInitialPenetration() {
        return 36;
    }

    @Override public double getPenetration(double distance) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override public float getSpeedBPS(double distance) {
        // TODO Auto-generated method stub
        return 0;
    }
}
