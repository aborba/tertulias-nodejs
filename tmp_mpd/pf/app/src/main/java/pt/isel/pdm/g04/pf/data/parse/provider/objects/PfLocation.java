/*
package pt.isel.pdm.g04.pf.data.parse.provider.objects;

import android.content.ContentValues;
import android.location.Location;

import java.util.Date;
import java.util.Random;

import pt.isel.pdm.g04.pf.data.parse.provider.ParseContract;

public class PfLocation extends Location {
    public final String email;

    public PfLocation(String provider, String email) {
        super(provider);
        this.email = email;
    }

    public void selfUpdate(Location location) {
        selfUpdate(location.getLatitude(), location.getLongitude(), location.getAltitude(),
                location.getAccuracy(), location.getTime());
    }

    public void selfUpdate(double latitude, double longitude, double height, float accuracy) {
        selfUpdate(latitude, longitude, height, accuracy, new Date().getTime());
    }

    public void selfUpdate(double latitude, double longitude, double height, float accuracy, long timestamp) {
        setLatitude(latitude);
        setLongitude(longitude);
        setAltitude(height);
        setAccuracy(accuracy);
        setTime(timestamp);
    }

    public void selfUpdateRandomISEL() {
        selfUpdateRandom(38.755066, 38.757128, -9.117386, -9.115680, 75, 90, 30, 2);
    }

    public void selfUpdateRandom(double latMin, double latMax,
                                 double lngMin, double lngMax,
                                 double hgtMin, double hgtMax,
                                 float accMin, float accMax) {
        Random random = new Random();
        selfUpdate(random.nextDouble() * (latMax - latMin) + latMin,
                random.nextDouble() * (lngMax - lngMin) + lngMin,
                random.nextDouble() * (hgtMax - hgtMin) + hgtMin,
                random.nextFloat() * (accMin - accMax) + accMax,
                new Date().getTime());
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(ParseContract.Locations.EMAIL, email);
        values.put(ParseContract.Locations.LATITUDE, getLatitude());
        values.put(ParseContract.Locations.LONGITUDE, getLongitude());
        //values.put(ParseContract.Locations.HEIGHT, getAltitude());
        //values.put(ParseContract.Locations.ACCURACY, getAccuracy());
        values.put(ParseContract.Locations.TIMESTAMP, getTime());
        return values;
    }

}
*/
