package pt.isel.pdm.g04.pf.geofences;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import pt.isel.pdm.g04.pf.data.parse.provider.ParseContract;
import pt.isel.pdm.g04.pf.helpers.Utils;

public class SimpleGeofence {
    private final String id;
    private double latitude;
    private double longitude;
    private float radius;
    private long expirationDuration;
    private int transitionType;
    private int loiteringDelay = 60000;

    public SimpleGeofence(String geofenceId, double latitude, double longitude,
                          float radius, long expiration, int transition) {
        this(geofenceId);
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.expirationDuration = expiration;
        this.transitionType = transition;
    }

    public SimpleGeofence(String geofenceId) {
        this.id = geofenceId;
    }

    public String getId() {
        return id;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getRadius() {
        return radius;
    }

    public long getExpirationDuration() {
        return expirationDuration;
    }

    public int getTransitionType() {
        return transitionType;
    }

    public Geofence toGeofence() {
        Geofence g = new Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(transitionType)
                .setCircularRegion(getLatitude(), getLongitude(), getRadius())
                .setExpirationDuration(expirationDuration)
                .setLoiteringDelay(loiteringDelay).build();
        return g;
    }

    public void store(Context context) {
        Uri uri = ParseContract.Locations.CONTENT_URI;
        String where = String.format("%s = ?", ParseContract.Locations.EMAIL);
        String email = Utils.getEmail();
        String[] whereArgs = new String[]{email};
        ContentResolver locationContent = context.getContentResolver();
        Cursor cursor = locationContent.query(uri,
                ParseContract.Locations.PROJECTION_ALL,
                where, whereArgs,
                ParseContract.Locations.DEFAULT_ORDER_BY);
        boolean locationExists = cursor.moveToFirst();
        cursor.close();
        ContentValues values = new ContentValues();
        values.put(ParseContract.Locations.EMAIL, email);
        values.put(ParseContract.Locations.LATITUDE, getLatitude());
        values.put(ParseContract.Locations.LONGITUDE, getLongitude());
        values.put(ParseContract.Locations.LOCATION, getId());
        values.put(ParseContract.Locations.TIMESTAMP, new Date().getTime());
        if (locationExists) { // update
            locationContent.update(uri, values, where, whereArgs);
        } else { // insert
            locationContent.insert(uri, values);
        }
    }
}
