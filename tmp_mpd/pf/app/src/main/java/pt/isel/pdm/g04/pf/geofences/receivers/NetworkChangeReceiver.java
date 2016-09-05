package pt.isel.pdm.g04.pf.geofences.receivers;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.isel.pdm.g04.pf.geofences.IselGeofences;
import pt.isel.pdm.g04.pf.geofences.SimpleGeofence;
import pt.isel.pdm.g04.pf.helpers.Constants;
import pt.isel.pdm.g04.pf.helpers.Logger;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private static Calendar mCal;


    @Override
    public void onReceive(Context context, Intent intent) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isGpsEnabled || isNetworkEnabled) {
            return;
        }

        if (mCal == null) {
            mCal = Calendar.getInstance();
            mCal.setTime(new Date()); // sets calendar time/date
            mCal.add(Calendar.MINUTE, 5);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());

            if (cal.getTimeInMillis() > mCal.getTimeInMillis()) {
                cal.add(Calendar.MINUTE, 5);
                mCal = cal;
            } else {
                return;
            }
        }


        WifiManager wifiManager = (WifiManager) context.getSystemService(
                Context.WIFI_SERVICE);
        List<ScanResult> wifiList = wifiManager.getScanResults();
        Map<String, Integer> ssidLevels = new HashMap<>();
        int max = 0;
        for (ScanResult scanResult : wifiList) {
            int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
            max = Math.max(level, max);
            ssidLevels.put(scanResult.BSSID, level);
            Logger.d("SSID:" + scanResult.SSID + ", BSSID:" + scanResult.BSSID + ", capabilities:" + scanResult.capabilities + ", frequency:" + scanResult.frequency + ", Level is " + level + " out of 5\n");
        }
        SimpleGeofence geofence = new SimpleGeofence(Constants.Isel.Locations.OUTSIDE);


        for (Map.Entry<String, Integer> entry : ssidLevels.entrySet()) {
            if (entry.getValue() == max) {
                SimpleGeofence current = IselGeofences.getGeofence(entry.getKey());
                if (current != null) {
                    geofence = current;
                    break;
                }
            }
        }
        geofence.store(context);

    }
}