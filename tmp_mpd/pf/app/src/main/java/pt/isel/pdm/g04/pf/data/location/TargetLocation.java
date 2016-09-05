/*
package pt.isel.pdm.g04.pf.data.location;

import android.location.Location;

import pt.isel.pdm.g04.pf.helpers.Constants;

public  class TargetLocation {
    private static final long MAX_REPORT_RADIUS = 100; */
/* meters *//*


    public static double getLatitude() {
        return Constants.Isel.LOCATION.target.latitude;
    }

    public static double getLongitude() {
        return Constants.Isel.LOCATION.target.longitude;
    }

    public static float getDistance(double latitude, double longitude) {
        float[] distanceToTarget = new float[3];
        Location.distanceBetween(latitude, longitude, getLatitude(), getLongitude(), distanceToTarget);
        return distanceToTarget[0];
    }

    public static boolean isOutOfRange(double latitude, double longitude) {
        return getDistance(latitude, longitude) > MAX_REPORT_RADIUS;
    }
}*/
