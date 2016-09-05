package pt.isel.pdm.g04.pf.geofences;


import android.text.format.DateUtils;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.Map;

import pt.isel.pdm.g04.pf.helpers.Constants;

public class IselGeofences {
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS
            * DateUtils.HOUR_IN_MILLIS;

    private final static ArrayList<SimpleGeofence> mSimpleGeofences = new ArrayList<>();

    static {
        mSimpleGeofences.add(new SimpleGeofence(Constants.Isel.Locations.OTHER, Constants.Isel.LOCATION.target.latitude, Constants.Isel.LOCATION.target.longitude, 185, GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT));
        mSimpleGeofences.add(new SimpleGeofence(Constants.Isel.Locations.CC, 38.755630, -9.114634, 20, GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER));
        mSimpleGeofences.add(new SimpleGeofence(Constants.Isel.Locations.GYM, 38.755547, -9.115208, 15, GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER));
        mSimpleGeofences.add(new SimpleGeofence(Constants.Isel.Locations.BUILDING_F, 38.755551, -9.115739, 30, GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER));
        mSimpleGeofences.add(new SimpleGeofence(Constants.Isel.Locations.BUILDING_G, 38.755756, -9.116437, 33, GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER));
        mSimpleGeofences.add(new SimpleGeofence(Constants.Isel.Locations.BUILDING_A, 38.756413, -9.116056, 30, GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER));
        mSimpleGeofences.add(new SimpleGeofence(Constants.Isel.Locations.BUILDING_C, 38.756153, -9.115449, 30, GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER));
        mSimpleGeofences.add(new SimpleGeofence(Constants.Isel.Locations.BUILDING_P, 38.756287, -9.116748, 30, GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER));
        mSimpleGeofences.add(new SimpleGeofence(Constants.Isel.Locations.BUILDING_M, 38.755998, -9.117407, 33, GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER));
        mSimpleGeofences.add(new SimpleGeofence(Constants.Isel.Locations.RESIDENCE, 38.757032, -9.117981, 27, GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER));
        mSimpleGeofences.add(new SimpleGeofence(Constants.Isel.Locations.BUILDING_E, 38.756739, -9.117155, 33, GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER));
    }

    public static ArrayList<SimpleGeofence> getSimpleGeofences() {
        return mSimpleGeofences;
    }

    public static ArrayList<Geofence> getGeofences() {

        ArrayList<Geofence> mGeofences = new ArrayList<>();
        // Bulding the geofences and adding them to the geofence array.

        for (SimpleGeofence g : mSimpleGeofences) {
            mGeofences.add(g.toGeofence());
        }

        return mGeofences;
    }

    public static SimpleGeofence getGeofence(String location) {
        for (Map.Entry<String, String> entry : IselSSIDS.Locations.entrySet()) {
            if (entry.getKey().equals(location)) {
                for (SimpleGeofence g : mSimpleGeofences) {
                    if (g.getId().equals(entry.getValue())) {
                        return g;
                    }
                }
                break;
            }
        }
        return null;
    }
}
