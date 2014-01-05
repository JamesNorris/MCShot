package com.github.jamesnorris.mcshot.type.rifle;

import com.github.jamesnorris.mcshot.type.RealisticShotData;

public class Realistic45GovernmentShotData extends RealisticShotData {
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
        return 3200;
    }

    @Override public double getInitialPenetration() {
        // TODO Auto-generated method stub
        return 0;
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
