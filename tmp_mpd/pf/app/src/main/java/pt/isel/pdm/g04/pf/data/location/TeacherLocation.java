/*
package pt.isel.pdm.g04.pf.data.location;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.parse.ParseUser;

import pt.isel.pdm.g04.pf.data.parse.provider.ParseContract;
import pt.isel.pdm.g04.pf.data.parse.provider.objects.PfLocation;
import pt.isel.pdm.g04.pf.helpers.Constants;
import pt.isel.pdm.g04.pf.helpers.Logger;

public class TeacherLocation implements LocationListener {
    private static final String CLASS_NAME = "TeacherLocation";

    private static final long MIN_DISTANCE_CHANGE = 10; */
/* meters*//*

    private static final long MIN_TIME_CHANGE = 1000 * 60 * 1; */
/* milliseconds *//*


    private final Context ctx;
    private final LocationManager locationManager;
    private boolean isNetworkEnabled;
    private boolean isGpsEnabled;
    private Location lastKnownLocation;

    public TeacherLocation(Context ctx) {
        super();
        this.ctx = ctx;
        locationManager = (LocationManager) ctx.getSystemService(Service.LOCATION_SERVICE);
        updateProviderStatus();
        getLocation();
    }

    private void updateProviderStatus() {
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // region LocationListener

    @Override
    public void onLocationChanged(Location location) {
        double curLat = location.getLatitude(), curLng = location.getLongitude(),
                lstLat = lastKnownLocation.getLatitude(), lstLng = lastKnownLocation.getLongitude();
        Logger.i(String.format("Location received: Latitude: %f, Longitude: %f\n", curLat, curLng));
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                calcTimeBwUpdates(lstLat, lstLng), calcDistanceChangeForUpdates(lstLat, lstLng), this);
        if (TargetLocation.isOutOfRange(curLat, curLng)) return;
        PfLocation pfLocation = new PfLocation(location.getProvider(), ParseUser.getCurrentUser().getEmail());
        pfLocation.selfUpdate(location);
        parseUpdateInBackground(*/
/**//*
pfLocation);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Logger.i(String.format("%s: %s disabled\n", CLASS_NAME, provider));
        updateProviderStatus();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Logger.i(String.format("%s: %s enabled\n", CLASS_NAME, provider));
        updateProviderStatus();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Logger.i(String.format("%s: %s status changed\n", CLASS_NAME, provider));
    }

    // endregion

    // region Behaviour

    public void parseUpdateInBackground(PfLocation pfLocation) {
        new AsyncTask<PfLocation, Void, RuntimeException>() {
            @Override
            protected RuntimeException doInBackground(PfLocation... pfLocations) {
                Uri uri = ParseContract.Locations.CONTENT_URI;
                String where = String.format("%s = ?", ParseContract.Locations.EMAIL);
                String[] whereArgs = new String[]{pfLocations[0].email};
                ContentResolver locationContent = ctx.getContentResolver();
                Cursor cursor = locationContent.query(uri,
                        ParseContract.Locations.PROJECTION_ALL,
                        where, whereArgs,
                        ParseContract.Locations.DEFAULT_ORDER_BY);
                boolean locationExists = cursor.moveToFirst();
                cursor.close();
                ContentValues values = pfLocations[0].toContentValues();
                if (locationExists) { // update
                    if (locationContent.update(uri, values, where, whereArgs) == 0)
                        return new RuntimeException("Internal error, location not updated.");
                } else { // insert
                    if (locationContent.insert(uri, values) == null)
                        return new RuntimeException("Internal error, location not inserted.");
                    ;
                }
                return null;
            }

            @Override
            protected void onPostExecute(RuntimeException exception) {
                if (exception != null) throw exception;
                Logger.i("Location updated");
            }
        }.execute(pfLocation);
    }


    public Location getLocation() {

        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_CHANGE, MIN_DISTANCE_CHANGE, this);
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (!isGpsEnabled) return lastKnownLocation;

        if (lastKnownLocation != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    calcTimeBwUpdates(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),
                    calcDistanceChangeForUpdates(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),
                    this);
        }

        Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation == null) return lastKnownLocation;

        lastKnownLocation = gpsLocation;

        return lastKnownLocation;
    }

    public double getLatitude() {
        return lastKnownLocation.getLatitude();
    }

    public double getLongitude() {
        return lastKnownLocation.getLongitude();
    }

    public void stopUsing() {
        if (locationManager != null) locationManager.removeUpdates(this);
    }

    public void showSettingsAlert() {
        new AlertDialog.Builder(ctx)
                .setTitle("GPS is settings") // TODO: Strings
                .setMessage("GPS is not enabled. Do you want to go to settings menu?") // TODO: Strings
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() { // TODO: Strings
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ctx.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // TODO: Strings
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    // endregion

    // region Private

    private long calcDistanceChangeForUpdates(double currentLatitude, double currentLongitude) {
        float[] results = new float[3];
        Location.distanceBetween(currentLatitude, currentLongitude, Constants.Isel.LOCATION.target.latitude, Constants.Isel.LOCATION.target.longitude, results);
        return Math.max(MIN_DISTANCE_CHANGE, (long) results[0] - MIN_DISTANCE_CHANGE);
    }

    private long calcTimeBwUpdates(double currentLatitude, double currentLongitude) {
        float[] distance = new float[3];
        Location.distanceBetween(currentLatitude, currentLongitude, Constants.Isel.LOCATION.target.latitude, Constants.Isel.LOCATION.target.longitude, distance);
        final double invMaxAvgSpeed = 20; // 90 * 1000 / (60 * 60 * 1000); // m/ms
        long travelTime = (long) (distance[0] * invMaxAvgSpeed);
        return Math.max(MIN_TIME_CHANGE, travelTime - MIN_TIME_CHANGE);
    }


    // endregion


}
*/
