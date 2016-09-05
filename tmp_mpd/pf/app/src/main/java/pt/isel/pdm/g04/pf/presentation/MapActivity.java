package pt.isel.pdm.g04.pf.presentation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;

import java.util.List;

import pt.isel.pdm.g04.pf.R;
import pt.isel.pdm.g04.pf.data.Notification;
import pt.isel.pdm.g04.pf.geofences.IselGeofences;
import pt.isel.pdm.g04.pf.geofences.SimpleGeofence;
import pt.isel.pdm.g04.pf.helpers.Constants;
import pt.isel.pdm.g04.pf.helpers.Preferences;
import pt.isel.pdm.g04.pf.helpers.Utils;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnCameraChangeListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        setUpMapIfNeeded();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        List<Notification> notifications = getIntent().getParcelableArrayListExtra(Constants.Activities.NOTIFICATIONS_EXTRA);
        Utils.showMarkers(this, mMap, notifications);

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpMap() {
        if (Preferences.showGeofences(this)) {
            mMap.setOnCameraChangeListener(this);
        }
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(Constants.Isel.LOCATION));

    }

    // region Camera CallBacks

    @Override
    public void onCameraChange(CameraPosition position) {
        // Makes sure the visuals remain when zoom changes.
        for (SimpleGeofence g : IselGeofences.getSimpleGeofences()) {
            mMap.addCircle(new CircleOptions().center(g.getLatLng())
                    .radius(g.getRadius())
                    .fillColor(Color.parseColor(g.getId()))
                    .strokeColor(Color.TRANSPARENT)
                    .strokeWidth(2));
        }
    }

    // endregion
}
