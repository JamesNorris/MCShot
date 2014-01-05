package com.github.jamesnorris.mcshot.type.rifle;

import com.github.jamesnorris.mcshot.type.RealisticShotData;

public class Realistic308ShotData extends RealisticShotData {
    @Override public double getDamage(double distance) {
        return -.3 * distance + 30;
    }

    @Override public double getDeltaX(double distance, float originalYaw) {
        return (random.nextInt(10) - 5)
                / 1000
                * distance
                * Math.abs(Math.abs(Math.sin(.506148 * Math.toRadians(originalYaw))) - 1);
    }

    @Override public double getDeltaY(double distance, float originalPitch) {
        return .05 / getDistanceToTravel() * Math.pow(distance, 2)
                + originalPitch / 100;
    }

    @Override public double getDeltaZ(double distance, float originalYaw) {
        return (random.nextInt(10) - 5) / 1000 * distance
                * Math.abs(Math.sin(.506148 * Math.toRadians(originalYaw)));
    }

    @Override public double getDistanceToTravel() {
        return 2280.5;
    }

    @Override public double getInitialPenetration() {
        return 48;
    }

    @Override public double getPenetration(double distance) {
        return distance < 1000 ? getInitialPenetration()
                - (31 - 10 * Math.log10(distance)) : 1;
    }

    @Override public float getSpeedBPS(double distance) {
        return (float) (-3 * distance + 430);
    }
}
