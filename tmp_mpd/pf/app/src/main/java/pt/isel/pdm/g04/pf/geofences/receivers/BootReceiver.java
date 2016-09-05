package pt.isel.pdm.g04.pf.geofences.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import pt.isel.pdm.g04.pf.geofences.IselGeofences;
import pt.isel.pdm.g04.pf.geofences.TransitionsIntentService;
import pt.isel.pdm.g04.pf.helpers.Constants;

public class BootReceiver extends BroadcastReceiver implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private static GoogleApiClient mGoogleApiClient;
    private static PendingIntent mGeofencePendingIntent;
    private static final String TAG = "BootReceiver";
    private Context contextBootReceiver;

    @Override
    public void onReceive(final Context context, Intent intent) {


        contextBootReceiver = context;

        SharedPreferences sharedPrefs;
        SharedPreferences.Editor editor;
        if ((intent.getAction().equals("android.location.MODE_CHANGED") && isLocationModeAvailable(contextBootReceiver)) || (intent.getAction().equals("android.location.PROVIDERS_CHANGED") && isLocationServciesAvailable(contextBootReceiver))) {
            // isLocationModeAvailable for API >=19, isLocationServciesAvailable for API <19
            sharedPrefs = context.getSharedPreferences("GEO_PREFS", Context.MODE_PRIVATE);
            editor = sharedPrefs.edit();
            editor.remove("Geofences added");
            editor.commit();
            if (!isGooglePlayServicesAvailable()) {
                Log.i(TAG, "Google Play services unavailable.");
                return;
            }

            mGeofencePendingIntent = null;

            mGoogleApiClient = new GoogleApiClient.Builder(contextBootReceiver)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mGoogleApiClient.connect();
        }
    }

    private boolean isLocationModeAvailable(Context context) {

        if (Build.VERSION.SDK_INT >= 19 && getLocationMode(context) != Settings.Secure.LOCATION_MODE_OFF) {
            return true;
        }
        else return false;
    }

    public boolean isLocationServciesAvailable(Context context) {
        if (Build.VERSION.SDK_INT < 19) {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER));

        }
        else return false;
    }

    public int getLocationMode(Context context) {
        try {
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
        SharedPreferences sharedPrefs = contextBootReceiver.getSharedPreferences("GEO_PREFS", Context.MODE_PRIVATE);
        String geofencesExist = sharedPrefs.getString("Geofences added", null);

        if (geofencesExist == null) {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent(contextBootReceiver)
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        SharedPreferences sharedPrefs = contextBootReceiver.getSharedPreferences("GEO_PREFS", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putString("Geofences added", "1");
                        editor.commit();
                    }
                }
            });

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult((android.app.Activity) contextBootReceiver,
                        Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Log.i(TAG, "Exception while resolving connection error.", e);
            }
        } else {
            int errorCode = connectionResult.getErrorCode();
            Log.i(TAG, "Connection to Google Play services failed with error code " + errorCode);
        }

    }

    @Override
    public void onResult(Status status) {

    }

    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(contextBootReceiver);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (android.app.Activity) contextBootReceiver,
                        Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    static GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(IselGeofences.getGeofences());
        return builder.build();
    }


    static PendingIntent getGeofencePendingIntent(Context context) {

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(context, TransitionsIntentService.class);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }}