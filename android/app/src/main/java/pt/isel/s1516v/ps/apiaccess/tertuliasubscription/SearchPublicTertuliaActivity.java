package pt.isel.s1516v.ps.apiaccess.tertuliasubscription;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.LinkedList;
import java.util.List;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.tertuliasubscription.gson.ApiSearchList;
import pt.isel.s1516v.ps.apiaccess.tertuliasubscription.gson.ApiSearchListItem;

public class SearchPublicTertuliaActivity extends Activity
        implements TertuliasApi
        , GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks
        , GoogleMap.OnMyLocationButtonClickListener {

    public final static int ACTIVITY_REQUEST_CODE = SEARCH_PUBLIC_TERTULIA_RETURN_CODE;
    public static final String DATA_SEARCH = "SubscribeTertulia_Search";

    private static final String APILINKS_KEY = LINK_SEARCHPUBLIC;
    private static final int REQUEST_LOCATION = 2;

    private SearchView searchView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private PublicTertuliaArrayAdapter viewAdapter;
    private PublicTertulia[] publicTertulias;

    private GoogleApiClient googleApiClient;
//    private GoogleMap map;
//    private LocationManager locationManager;

    private double currentLatitude, currentLongitude;

    // region Activity Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_public_terulia);

        if (savedInstanceState != null) restoreInstanceState(savedInstanceState);

        progressBar = (ProgressBar) findViewById(R.id.ts_progressbar);
        searchView = (SearchView) findViewById(R.id.ts_search);
        searchView.setSubmitButtonEnabled(true);
        emptyView = (TextView) findViewById(R.id.ts_empty_view);

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.ts_toolbar),
                R.string.title_activity_search_public_tertulia,
                Util.IGNORE, Util.IGNORE, null, true, searchView);

        recyclerView = (RecyclerView) findViewById(R.id.ts_RecyclerView);
        viewAdapter = new PublicTertuliaArrayAdapter(this, publicTertulias != null ? publicTertulias : new PublicTertulia[0]);

        Util.setupAdapter(this, recyclerView, viewAdapter);

        handleIntent(getIntent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence(DATA_SEARCH, searchView.getQuery());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PublicTertuliaDetailsActivity.ACTIVITY_REQUEST_CODE) {
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            if (resultCode == RESULT_SUCCESS) {
                Util.longSnack(rootView, R.string.public_tertulia_details_subscribe_success);
                setResult(RESULT_SUCCESS);
                if (searchView != null)
                    requestSearch(searchView.getQuery().toString());
            }
        }
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
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
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

//    private void setUpMap() {
//        if (map == null) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
//            }
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//                return;
//            map.setMyLocationEnabled(true);
//            map.setOnMyLocationButtonClickListener(this);
//        }
//    }

    // endregion

    // region Private Classes

    public class SearchCallback implements FutureCallback<JsonElement> {
        @Override
        public void onSuccess(JsonElement result) {
            new AsyncTask<JsonElement, Void, PublicTertulia[]>() {

                @Override
                protected PublicTertulia[] doInBackground(JsonElement... params) {
                    ApiSearchList apiTertuliasList = new Gson().fromJson(params[0], ApiSearchList.class);
                    LinkedList<PublicTertulia> publicTertulias = new LinkedList<>();
                    for (ApiSearchListItem apiSearchListItem : apiTertuliasList.items) {
                        PublicTertulia publicTertulia = new PublicTertulia(apiSearchListItem);
                        publicTertulias.add(publicTertulia);
                    }
                    return publicTertulias.toArray(new PublicTertulia[publicTertulias.size()]);
                }

                @Override
                protected void onPostExecute(PublicTertulia[] publicTerulias) {
                    SearchPublicTertuliaActivity.this.publicTertulias = publicTerulias;
                    PublicTertuliaArrayAdapter viewAdapter = new PublicTertuliaArrayAdapter(SearchPublicTertuliaActivity.this,
                            publicTerulias != null ? publicTerulias : new PublicTertulia[0]);
                    recyclerView.setAdapter(viewAdapter);
                    if (publicTerulias == null || publicTerulias.length == 0) {
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }.execute(result);
            setResult(RESULT_SUCCESS);
        }

        @Override
        public void onFailure(Throwable e) {
            Util.longSnack(findViewById(android.R.id.content), Util.getEMsg(SearchPublicTertuliaActivity.this, e.getMessage()));
            Util.logd("New tertulia creation failed");
            Util.logd(e.getMessage());
            setResult(RESULT_FAIL);
        }
    }

    // endregion

    // region Private Methods

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null && query != null)
                searchView.setQuery(query, false);
            progressBar.setVisibility(View.VISIBLE);
            requestSearch(query);
        }
    }

    private void requestSearch(String query) {
        ApiLinks apiLinks = getIntent().getParcelableExtra(INTENT_LINKS);
        String apiEndPoint = apiLinks.getRoute(APILINKS_KEY);
        String apiMethod = apiLinks.getMethod(APILINKS_KEY);
        List<Pair<String, String>> parameters = new LinkedList<>();
        parameters.add(new Pair<>("query", query));
        parameters.add(new Pair<>("latitude", String.valueOf(currentLatitude)));
        parameters.add(new Pair<>("longitude", String.valueOf(currentLongitude)));
        Util.request(this, apiEndPoint, apiMethod, parameters, new SearchCallback());
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        if (searchView == null)
            return;
        searchView.setQuery(savedInstanceState.getCharSequence(DATA_SEARCH), false);
    }

    // endregion

}
