package pt.isel.s1516v.ps.apiaccess.tertuliadetails;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;

public class PlacePresentationActivity extends FragmentActivity
        implements TertuliasApi
        , GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks
        , GoogleMap.OnMyLocationButtonClickListener {

    public static final String INTENT_LABEL = "label";
    public static final String INTENT_SNIPPET = "snippet";
    public static final String INTENT_LATITUDE = "latitude";
    public static final String INTENT_LONGITUDE = "longitude";

    private static final int REQUEST_LOCATION = 2;

    private ProgressBar progressBar;
    private SupportMapFragment mapFragment;
    private GoogleApiClient googleApiClient;
    private GoogleMap map;
    private LocationManager locationManager;

    private double targetLatitude, targetLongitude;
    private String targetLabel, targetSnippet;

    // region Activity Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_place);

        if (savedInstanceState != null) restoreInstanceState(savedInstanceState);

        Intent intent = getIntent();
        targetLatitude = intent.getDoubleExtra(INTENT_LATITUDE, 0.0);
        targetLongitude = intent.getDoubleExtra(INTENT_LONGITUDE, 0.0);
        targetLabel = intent.getStringExtra(INTENT_LABEL);
        targetSnippet = intent.getStringExtra(INTENT_SNIPPET);

        progressBar = (ProgressBar) findViewById(R.id.ts_progressbar);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ppa_map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng position = new LatLng(targetLatitude, targetLongitude);
                googleMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(targetLabel)
                        .snippet(targetSnippet)
                        .visible(true))
                        .showInfoWindow();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17.0f));
            }
        });

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.toolbar),
                R.string.title_activity_place_presentation,
                Util.IGNORE, Util.IGNORE, null, true);

        setUpGoogleApiClient();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                Util.longSnack(findViewById(android.R.id.content), R.string.new_tertulia_toast_cancel);
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient != null)
            googleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpGoogleApiClient();
        googleApiClient.connect();
    }

    // endregion

    // region GoogleApiClient.OnConnectionFailedListener

    private void setUpGoogleApiClient() {
        if (googleApiClient != null)
            return;
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    // endregion

    // region GoogleApiClient.ConnectionCallbacks

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient,
                REQUEST,
                new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location == null)
                            return;
                        targetLatitude = location.getLatitude();
                        targetLongitude = location.getLongitude();
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(30000)
            .setFastestInterval(1)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    // endregion

    // region OnMyLocationButtonClickListener

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    // endregion

    // region Private Methods

    private void setUpMap() {
        if (map == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;
            map.setMyLocationEnabled(true);
            map.setOnMyLocationButtonClickListener(this);
        }
    }

    // endregion

    // region Private Classes

    // endregion

    // region Private Methods

    private void restoreInstanceState(Bundle savedInstanceState) {
    }

    // endregion

}
