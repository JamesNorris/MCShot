package com.github.jamesnorris.mcshot.util;

public class MathUtility {
    public static double absDegrees(double degrees) {
        if (degrees < 0) {
            for (int i = (int) Math.round(degrees / -360); i >= 0; i--) {
                degrees += 360;;
            }
        }
        return degrees % 360;
    }

    public static float absDegrees(float degrees) {
        return (float) absDegrees((double) degrees);
    }

    public static boolean overlap1D(double low1, double high1, double low2, double high2) {
        return low1 <= low2 ? low2 <= high1 : low1 <= high2;
    }
    
    public static double logBase(double base, double number) {
        return Math.log(number) / Math.log(base);
    }
}
