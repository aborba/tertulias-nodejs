/*
 * Copyright (c) 2016 António Borba da Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package pt.isel.s1516v.ps.apiaccess.tertuliacreation;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.Arrays;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Error;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Address;
import pt.isel.s1516v.ps.apiaccess.support.domain.Geolocation;
import pt.isel.s1516v.ps.apiaccess.support.domain.LocationCreation;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaCreation;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.api.CrApiTertuliaMonthlyDSchedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.api.CrApiTertuliaMonthlyWSchedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.api.CrApiTertuliaWeeklySchedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiManager;

public class NewTertuliaActivity extends Activity implements
        TertuliasApi
        , DialogFragmentResult
        , GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks {

    public final static int ACTIVITY_REQUEST_CODE = NEW_TERTULIA_RETURN_CODE;
    private final static String INSTANCE_KEY_TERTULIA = "tertulia";

    public static final String INTENT_TERTULIAS = "Tertulias";

    private String apiEndPoint, apiMethod;

    private CrUiManager uiManager;
    private TertuliaCreation tertulia;
    private ApiLinks apiLinks;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tertulia);

        uiManager = new CrUiManager(this);

        Util.setupToolBar(this, (Toolbar) uiManager.getView(CrUiManager.UIRESOURCE.TOOLBAR),
                R.string.title_activity_new_tertulia,
                Util.IGNORE, Util.IGNORE, null, true);

        if (savedInstanceState != null) restoreInstanceState(savedInstanceState);
        if (tertulia == null) {
            Address address = new Address(null, null, null, null);
            Geolocation geolocation = new Geolocation(0.0, 0.0);
            LocationCreation location = new LocationCreation(null, address, geolocation);
            tertulia = new TertuliaCreation(null, null, false, location, null, null, null);
        }

        apiLinks = getIntent().getParcelableExtra(INTENT_LINKS);
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
    protected void onSaveInstanceState(Bundle outState) {
        if (tertulia != null)
            uiManager.update(tertulia);
        outState.putParcelable(INSTANCE_KEY_TERTULIA, tertulia);
    }

    public void onClickMapLookup(View view) {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            String latitude = uiManager.getLatitudeData();
            String longitude = uiManager.getLongitudeData();
            if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
                LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                builder.setLatLngBounds(new LatLngBounds(latLng, latLng));
            }
            startActivityForResult(builder.build(this), PICK_PLACE_RETURN_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void onClickSelectSchedule(View view) {
        DialogFragment scheduleSelectionFragment = new ScheduleSelectionDialog();
        scheduleSelectionFragment.show(getFragmentManager(), "schedule");
    }

    public void onClickCreateTertulia(View view) {
        uiManager.update(tertulia);

        if (!isNameValid(tertulia.name, getIntent().getStringArrayExtra(INTENT_TERTULIAS))) {
            Util.longSnack(view, R.string.new_tertulia_toast_invalid_name);
            return;
        }

        if (tertulia.tertuliaSchedule == null) {
            Util.longSnack(view, R.string.new_tertulia_toast_no_schedule_selected);
            return;
        }

        JsonElement postParameters;
        String apiLinksKey;
        uiManager.update(tertulia);
        switch (tertulia.tertuliaSchedule.getType().name()) {
            case "WEEKLY":
                CrApiTertuliaWeeklySchedule apiWeekly = new CrApiTertuliaWeeklySchedule(tertulia);
                postParameters = new Gson().toJsonTree(apiWeekly);
                apiLinksKey = LINK_CREATE_WEEKLY;
                break;
            case "MONTHLYD":
                CrApiTertuliaMonthlyDSchedule apiMonthly = new CrApiTertuliaMonthlyDSchedule(tertulia);
                postParameters = new Gson().toJsonTree(apiMonthly);
                apiLinksKey = LINK_CREATE_MONTHLY;
                break;
            case "MONTHLYW":
                CrApiTertuliaMonthlyWSchedule apiMonthlyW = new CrApiTertuliaMonthlyWSchedule(tertulia);
                postParameters = new Gson().toJsonTree(apiMonthlyW);
                apiLinksKey = LINK_CREATE_MONTHLYW;
                break;
            case "YEARLY":
                apiLinksKey = LINK_CREATE_YEARLY;
                throw new UnsupportedOperationException();
            case "YEARLYW":
                apiLinksKey = LINK_CREATE_YEARLYW;
                throw new UnsupportedOperationException();
            default:
                throw new IllegalArgumentException();
        }
        apiEndPoint = apiLinks.getRoute(apiLinksKey);
        apiMethod = apiLinks.getMethod(apiLinksKey);

        Futures.addCallback(Util.getMobileServiceClient(this)
                .invokeApi(apiEndPoint, postParameters, apiMethod, null)
                , new Callback(findViewById(android.R.id.content)));
    }

    private static <T> boolean isNameValid(String name, String[] targets) {
        return !TextUtils.isEmpty(name) && !Arrays.asList(targets).contains(name.toLowerCase().trim());
    }

    public void onClickCancel(View view) {
        Log.d("trt", "New tertulia creation cancelled");
        Util.longSnack(view, R.string.new_tertulia_toast_cancel);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_PLACE_RETURN_CODE:
                if (resultCode == RESULT_FAIL)
                    return;
                Place place = PlacePicker.getPlace(data, this);
                if (place == null)
                    return;
                uiManager.set(place);
                break;
            case ScheduleWeeklyActivity.ACTIVITY_REQUEST_CODE:
            case ScheduleMonthlyDActivity.ACTIVITY_REQUEST_CODE:
            case ScheduleMonthlyWActivity.ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_FAIL)
                    return;
                setResult(RESULT_OK);
                tertulia.tertuliaSchedule = data.getParcelableExtra("result");
                uiManager.set(tertulia);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    // region DialogFragmentResult

    @Override
    public void onSelection(int selection) {
        String[] schedules = getResources().getStringArray(R.array.new_tertulia_dialog_schedules);
        Intent intent;
        switch (selection) {
            case WEEKLY:
                intent = new Intent(this, ScheduleWeeklyActivity.class);
                startActivityForResult(intent, ScheduleWeeklyActivity.ACTIVITY_REQUEST_CODE);
                break;
            case MONTHLY:
                intent = new Intent(this, ScheduleMonthlyDActivity.class);
                startActivityForResult(intent, ScheduleMonthlyDActivity.ACTIVITY_REQUEST_CODE);
                break;
            case MONTHLYW:
                intent = new Intent(this, ScheduleMonthlyWActivity.class);
                startActivityForResult(intent, ScheduleMonthlyWActivity.ACTIVITY_REQUEST_CODE);
                break;
            case YEARLY:
            case YEARLYW:
                Util.longSnack(findViewById(android.R.id.content), R.string.message_not_available_yet);
                break;
            default:
                throw new IllegalArgumentException();
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
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            // TODO: review
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    // endregion

    // region Private Classes

    public class Callback implements FutureCallback<JsonElement> {
        private View view;

        public Callback(View view) {
            this.view = view;
        }

        @Override
        public void onSuccess(JsonElement result) {
            Util.longSnack(view, R.string.new_tertulia_toast_success);
            Util.logd("New tertulia created");
            Util.logd(result.toString());
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onFailure(Throwable e) {
            Util.longSnack(view, getEMsg(NewTertuliaActivity.this, e.getMessage()));
            Util.logd("New tertulia creation failed");
            Util.logd(e.getMessage());
            setResult(RESULT_FAIL);
            finish();
        }

    }

    // endregion

    // region Private Methods

    private static String getEMsg(Context ctx, String msg) {
        if (!Util.isJson(msg)) return msg;
        Error error = new Gson().fromJson(msg, Error.class);
        return error.getStatusCodeMessage(ctx);
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        tertulia = savedInstanceState.getParcelable(INSTANCE_KEY_TERTULIA);
        uiManager.set(tertulia);
    }

    // endregion

}
