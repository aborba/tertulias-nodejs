package pt.isel.s1516v.ps.apiaccess.tertuliacreation;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Error;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.*;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiCreateTertuliaMonthly;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiCreateTertuliaMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiCreateTertuliaWeekly;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;

public class NewTertuliaActivity extends Activity implements TertuliasApi {

    public final static int REQUEST_CODE = NEW_TERTULIA_RETURN_CODE;

    public static final String MY_TERTULIAS = "MyTertulias";
    public static final String DATA_TITLE = "NewTertulia_Title";
    public static final String DATA_SUBJECT = "NewTertulia_Subject";
    public static final String DATA_LOCATIONVIEW = "NewTertulia_Location";
    public static final String DATA_ADDRESSVIEW = "NewTertulia_Address";
    public static final String DATA_ZIPVIEW = "NewTertulia_Zip";
    public static final String DATA_CITYVIEW = "NewTertulia_City";
    public static final String DATA_COUNTRYVIEW = "NewTertulia_Country";
    public static final String DATA_LATITUDEVIEW = "NewTertulia_Latitude";
    public static final String DATA_LONGITUDEVIEW = "NewTertulia_Longitude";
    public static final String DATA_SCHEDULE = "NewTertulia_Schedule";
    public static final String DATA_PRIVACY = "NewTertulia_Privacy";

    private static final String APILINKS_KEY = LINK_CREATE;

    private String apiEndPoint, apiMethod;
    private EditText titleView, subjectView, locationView, addressView, zipView, cityView, countryView, latitudeView, longitudeView;
    private TextView scheduleView;
    private CheckBox privacyView;
    private String[] spinnerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tertulia);

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.nt_toolbar),
                R.string.title_activity_new_tertulia,
                Util.IGNORE, Util.IGNORE, null, true);

        titleView = (EditText) findViewById(R.id.nt_Title);
        subjectView = (EditText) findViewById(R.id.nt_Subject);
        locationView = (EditText) findViewById(R.id.nt_LocationName);
        addressView = (EditText) findViewById(R.id.nt_Address);
        zipView = (EditText) findViewById(R.id.nt_Zip);
        cityView = (EditText) findViewById(R.id.nt_City);
        countryView = (EditText) findViewById(R.id.nt_Country);
        latitudeView = (EditText) findViewById(R.id.nt_Latitude);
        longitudeView = (EditText) findViewById(R.id.nt_Longitude);
        scheduleView = (TextView) findViewById(R.id.nt_Schedule);
        privacyView = (CheckBox) findViewById(R.id.nt_Privacy);

        if (savedInstanceState != null) restoreInstanceState(savedInstanceState);

        spinnerData = new String[]{"Weekly", "Monthly", "MonthlyW", "Yearly", "YearlyW"};
        /*
        ArrayAdapter<String> scheduleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerData);
        scheduleView.setAdapter(scheduleAdapter);
        */

        scheduleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment scheduleSelectionFragment = new ScheduleSelectionDialog();
                scheduleSelectionFragment.show(getFragmentManager(), "schedule");
            }
        });

        ApiLinks apiLinks = getIntent().getParcelableExtra(LINKS_LABEL);
        apiEndPoint = apiLinks.getRoute(APILINKS_KEY);
        apiMethod = apiLinks.getMethod(APILINKS_KEY);
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
        outState.putString(DATA_TITLE, titleView.getText().toString());
        outState.putString(DATA_SUBJECT, subjectView.getText().toString());
        outState.putString(DATA_LOCATIONVIEW, locationView.getText().toString());
        outState.putString(DATA_ADDRESSVIEW, addressView.getText().toString());
        outState.putString(DATA_ZIPVIEW, zipView.getText().toString());
        outState.putString(DATA_CITYVIEW, cityView.getText().toString());
        outState.putString(DATA_COUNTRYVIEW, countryView.getText().toString());
        outState.putString(DATA_LATITUDEVIEW, latitudeView.getText().toString());
        outState.putString(DATA_LONGITUDEVIEW, longitudeView.getText().toString());
        outState.putString(DATA_SCHEDULE, scheduleView.getText().toString());
        outState.putString(DATA_PRIVACY, privacyView.getText().toString());
    }

    public void onClickUsePicker(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PICK_PLACE_RETURN_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void onClickCreateTertulia(View view) {
        String value = latitudeView.getText().toString();
        Double latitude = Double.parseDouble(TextUtils.isEmpty(value) ? "0" : value);
        value = longitudeView.getText().toString();
        Double longitude = Double.parseDouble(TextUtils.isEmpty(value) ? "0" : value);
//        int scheduleType = scheduleView.getSelectedItemPosition();
        int scheduleType = 3;
        String name = titleView.getText().toString();

        if (!isNameValid(name)) {
            Util.longSnack(findViewById(android.R.id.content), R.string.new_tertulia_toast_invalid_name);
            return;
        }

        JsonElement postParameters;
        switch (scheduleType) {
            case 1: // Weekly
                ApiCreateTertuliaWeekly weekly = new ApiCreateTertuliaWeekly(
                        name,
                        subjectView.getText().toString(),
                        locationView.getText().toString(),
                        addressView.getText().toString(),
                        zipView.getText().toString(),
                        cityView.getText().toString(),
                        countryView.getText().toString(),
                        String.valueOf(latitude),
                        String.valueOf(longitude),
                        "", // weekDay
                        0, // skip
                        privacyView.isChecked());
                postParameters = new Gson().toJsonTree(weekly);
                break;
            case 2: // Monthly
                ApiCreateTertuliaMonthly monthly = new ApiCreateTertuliaMonthly(
                        titleView.getText().toString(),
                        subjectView.getText().toString(),
                        locationView.getText().toString(),
                        addressView.getText().toString(),
                        zipView.getText().toString(),
                        cityView.getText().toString(),
                        countryView.getText().toString(),
                        String.valueOf(latitude),
                        String.valueOf(longitude),
                        0, // dayNr
                        true, // fromStart
                        0, // skip
                        privacyView.isChecked());
                postParameters = new Gson().toJsonTree(monthly);
                break;
            case 3: // MonthlyW
                ApiCreateTertuliaMonthlyW monthlyW = new ApiCreateTertuliaMonthlyW(
                        titleView.getText().toString(),
                        subjectView.getText().toString(),
                        locationView.getText().toString(),
                        addressView.getText().toString(),
                        zipView.getText().toString(),
                        cityView.getText().toString(),
                        countryView.getText().toString(),
                        String.valueOf(latitude),
                        String.valueOf(longitude),
                        "", // weekDay
                        0, // weekNr
                        true, // fromStart
                        0, // skip
                        privacyView.isChecked());
                postParameters = new Gson().toJsonTree(monthlyW);
                break;
            case 4: // Yearly
                throw new UnsupportedOperationException();
            case 5: // YearlyW
                throw new UnsupportedOperationException();
            default:
                throw new IllegalArgumentException();
        }

        Futures.addCallback(Util.getMobileServiceClient(this)
                        .invokeApi(apiEndPoint, postParameters)
                , new Callback(findViewById(android.R.id.content)));
    }

    private boolean isNameValid(String name) {
        if (TextUtils.isEmpty(name)) return false;
        name = name.toLowerCase();
        Parcelable[] tertulias = getIntent().getParcelableArrayExtra(MY_TERTULIAS);
        for (Parcelable tertulia : tertulias) {
            String s = ((Tertulia) tertulia).name;
            if (((Tertulia) tertulia).name.toLowerCase().equals(name))
                return false;
        }
        return true;
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
                locationView.setText(place.getName());
                Address address = new Address(place.getAddress().toString());
                addressView.setText(address.address);
                zipView.setText(address.zip);
                cityView.setText(address.city);
                countryView.setText(address.country);
                latitudeView.setText(String.format(Locale.getDefault(), "%.6f", place.getLatLng().latitude));
                longitudeView.setText(String.format(Locale.getDefault(), "%.6f", place.getLatLng().longitude));
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    // region Private Classes

    public class Callback implements FutureCallback<JsonElement> {
        private View view;

        public Callback(View view) {
            this.view = view;
        }

        @Override
        public void onFailure(Throwable e) {
            Util.longSnack(view, getEMsg(NewTertuliaActivity.this, e.getMessage()));
            Util.logd("New tertulia creation failed");
            Util.logd(e.getMessage());
            setResult(RESULT_FAIL);
            finish();
        }

        @Override
        public void onSuccess(JsonElement result) {
            Util.longSnack(view, R.string.new_tertulia_toast_success);
            Util.logd("New tertulia created");
            Util.logd(result.toString());
            setResult(RESULT_SUCCESS);
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
        restoreTextViewState(savedInstanceState, titleView, DATA_TITLE);
        restoreTextViewState(savedInstanceState, subjectView, DATA_SUBJECT);
        restoreTextViewState(savedInstanceState, locationView, DATA_LOCATIONVIEW);
        restoreTextViewState(savedInstanceState, addressView, DATA_ADDRESSVIEW);
        restoreTextViewState(savedInstanceState, zipView, DATA_ZIPVIEW);
        restoreTextViewState(savedInstanceState, cityView, DATA_CITYVIEW);
        restoreTextViewState(savedInstanceState, countryView, DATA_COUNTRYVIEW);
        restoreTextViewState(savedInstanceState, latitudeView, DATA_LATITUDEVIEW);
        restoreTextViewState(savedInstanceState, longitudeView, DATA_LONGITUDEVIEW);
        restoreTextViewState(savedInstanceState, scheduleView, DATA_SCHEDULE);
        restoreTextViewState(savedInstanceState, privacyView, DATA_PRIVACY);
    }

    // endregion

    // region Private Static Methods

    private static void restoreTextViewState(Bundle savedInstanceState, TextView textView, String key) {
        if (savedInstanceState.containsKey(key))
            textView.setText(savedInstanceState.getString(key));
    }

    private static void restoreTextViewState(Bundle savedInstanceState, Spinner spinner, String key) {
        if (savedInstanceState.containsKey(key))
            spinner.setSelection(savedInstanceState.getInt(key));
    }

    // endregion

}
