package pt.isel.pdm.g04.pf.geofences;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import pt.isel.pdm.g04.pf.helpers.Constants;
import pt.isel.pdm.g04.pf.helpers.Logger;

public class TransitionsIntentService extends IntentService {

    public TransitionsIntentService() {
        super("TransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (!geofencingEvent.hasError()) {
            int transition = geofencingEvent.getGeofenceTransition();
            Geofence geofence = getMainGeofence(intent, transition);
            switch (transition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    Logger.d("Geofence Entered");
                    sendNotification(this, getSimpleGeofence(geofence));
                    break;
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    Logger.d("Dwelling in Geofence");
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    SimpleGeofence dummy = new SimpleGeofence(Constants.Isel.Locations.OUTSIDE);
                    sendNotification(this, dummy);
                    Logger.d("Geofence Exited");
                    break;
                default:
                    Logger.d("Geofence Unknown");
            }

        }
    }

    private Geofence getMainGeofence(Intent intent, int transition) {
        GeofencingEvent geofenceEvent = GeofencingEvent.fromIntent(intent);
        List<Geofence> geofences = geofenceEvent
                .getTriggeringGeofences();
        Geofence lastGeofence = null;
        for (Geofence g : geofences) {
            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER && !g.getRequestId().equals(Constants.Isel.Locations.OTHER))
                return g;
            lastGeofence = g;
        }
        return lastGeofence;
    }

    private SimpleGeofence getSimpleGeofence(Geofence geofence) {
        for (SimpleGeofence g : IselGeofences.getSimpleGeofences()) {
            String requestId = geofence.getRequestId();
            String id = g.getId();
            if (id.equals(requestId))
                return g;
        }
        return null;
    }


    private void sendNotification(Context context, SimpleGeofence geofence) {

        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "");
        wakeLock.acquire();
        geofence.store(context);
        wakeLock.release();
    }

}
