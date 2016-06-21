package pt.isel.s1516v.ps.apiaccess;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import pt.isel.s1516v.ps.apiaccess.helpers.Error;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.domain.Tertulia;
import pt.isel.s1516v.ps.apiaccess.support.raw.RTertulia;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;

public class NewTertuliaActivity extends AppCompatActivity implements TertuliasApi {

    public static final String MY_TERTULIAS = "MyTertulias";
    public final static int REQUEST_CODE = NEW_TERTULIA_RETURN_CODE;
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

    private String apiEndPoint;
    private EditText titleView, subjectView;
    private TextView locationView, addressView, zipView, cityView, countryView, latitudeView, longitudeView;
    private Spinner scheduleView;
    private CheckBox privacyView;
    private String[] spinnerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tertulia);

        Util.setupActionBar(this, R.string.title_activity_new_tertulia, true);

        titleView = (EditText) findViewById(R.id.tsTitle);
        subjectView = (EditText) findViewById(R.id.newTertuliaSubject);
        locationView = (EditText) findViewById(R.id.tsLocationName);
        addressView = (EditText) findViewById(R.id.newTertuliaLocationAddress);
        zipView = (EditText) findViewById(R.id.tsLocationZip);
        cityView = (EditText) findViewById(R.id.tsLocationCity);
        countryView = (EditText) findViewById(R.id.tsLocationCountry);
        latitudeView = (EditText) findViewById(R.id.tsLocationLatitude);
        longitudeView = (EditText) findViewById(R.id.tsLocationLongitude);
        scheduleView = (Spinner) findViewById(R.id.newTertuliaSchedule);
        privacyView = (CheckBox) findViewById(R.id.newTertuliaPrivacy);

        if (savedInstanceState != null) restoreInstanceState(savedInstanceState);

        spinnerData = new String[] {"Weekly", "Monthly", "MonthlyW", "Yearly", "YearlyW"};
        ArrayAdapter<String> scheduleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerData);
        scheduleView.setAdapter(scheduleAdapter);

        apiEndPoint = getIntent().getStringExtra(END_POINT_LABEL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
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
        outState.putInt(DATA_SCHEDULE, scheduleView.getSelectedItemPosition());
        outState.putString(DATA_PRIVACY, privacyView.getText().toString());
    }

    public void onClickSelectLocation(View view) {
        Intent intent = new Intent(this, NewLocationActivity.class);
        startActivity(intent);
    }

    public void onClickSelectSchedule(View view) {

    }

    public void onClickCreateTertulia(View view) {
        Log.d("trt", "in onClickCreateTertulia");

        RTertulia rtertulia = new RTertulia();

        rtertulia.name = titleView.getText().toString();
        rtertulia.subject = subjectView.getText().toString();
        rtertulia.locationName = locationView.getText().toString();
        rtertulia.address = addressView.getText().toString();
        rtertulia.zip = zipView.getText().toString();
        rtertulia.city = cityView.getText().toString();
        rtertulia.country = countryView.getText().toString();
        String value = latitudeView.getText().toString();
        rtertulia.latitude = Double.parseDouble(TextUtils.isEmpty(value) ? "0" : value);
        value = longitudeView.getText().toString();
        rtertulia.longitude = Double.parseDouble(TextUtils.isEmpty(value) ? "0" : value);
        rtertulia.isPrivate = privacyView.isChecked();
        rtertulia.scheduleType = spinnerData[scheduleView.getSelectedItemPosition()];
        rtertulia.isPrivate = privacyView.isChecked();

        if (!isNameValid(rtertulia.name)) {
            Util.longToast(this, R.string.new_tertulia_toast_invalid_name);
            return;
        }

        JsonElement postParameters = new Gson().toJsonTree(rtertulia);

        MobileServiceClient cli = Util.getMobileServiceClient(this);
        ListenableFuture<JsonElement> future = cli.invokeApi(apiEndPoint, postParameters);
        Futures.addCallback(future, new Callback());
    }

    private boolean isNameValid(String name) {
        if (TextUtils.isEmpty(name) ) return false;
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
        finish();
    }

    // region Private Classes

    public class Callback implements FutureCallback<JsonElement> {
        @Override
        public void onFailure(Throwable e) {
            Util.longToast(NewTertuliaActivity.this, getEMsg(NewTertuliaActivity.this, e.getMessage()));
            Util.logd("New tertulia creation failed");
            Util.logd(e.getMessage());
            setResult(RESULT_FAIL);
            finish();
        }

        @Override
        public void onSuccess(JsonElement result) {
            Util.longToast(NewTertuliaActivity.this, R.string.new_tertulia_toast_success);
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
        if (savedInstanceState.containsKey(key)) textView.setText(savedInstanceState.getString(key));
    }

    private static void restoreTextViewState(Bundle savedInstanceState, Spinner spinner, String key) {
        if (savedInstanceState.containsKey(key)) spinner.setSelection(savedInstanceState.getInt(key));
    }

    // endregion

}
