package pt.isel.s1516v.ps.apiaccess.helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

import android.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;

import pt.isel.s1516v.ps.apiaccess.ui.UiManager;

import static android.location.LocationManager.GPS_PROVIDER;

public class GeoPosition {
    private static double lat, lng;

    public static double getLatitude(UiManager uiManager) {
        if (!uiManager.isGeoData())
            throw new IllegalArgumentException();
        if (!uiManager.isGeo())
            throw new IllegalStateException();
        return Util.string2Double(uiManager.getLatitudeData());
    }

    public static double getLongitude(UiManager uiManager) {
        if (!uiManager.isGeoData())
            throw new IllegalArgumentException();
        if (!uiManager.isGeo())
            throw new IllegalStateException();
        return Util.string2Double(uiManager.getLongitudeData());
    }

    public static LatLng getLatLng(UiManager uiManager) {
        if (!uiManager.isGeoData())
            throw new IllegalArgumentException();
        return new LatLng(getLatitude(uiManager), getLongitude(uiManager));
    }

    public static LocationManager getLocationManager(Context ctx) {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            throw new IllegalStateException();
        }
        locationManager.requestLocationUpdates(GPS_PROVIDER, 5000, 10, locationListener);
        return locationManager;
    }

    public static LatLng getLatLng(Context ctx) {
        return new LatLng(lat, lng);
    }

}
