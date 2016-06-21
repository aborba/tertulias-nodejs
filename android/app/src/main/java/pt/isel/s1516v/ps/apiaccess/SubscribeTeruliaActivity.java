package pt.isel.s1516v.ps.apiaccess;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Tertulia;
import pt.isel.s1516v.ps.apiaccess.support.raw.RTertulia;

public class SubscribeTeruliaActivity extends AppCompatActivity implements TertuliasApi {

    public final static int REQUEST_CODE = SUBSCRIBE_TERTULIA_RETURN_CODE;
    public static final String DATA_TITLE = "SubscribeTertulia_Title";
    public static final String DATA_LOCATIONVIEW = "SubscribeTertulia_Location";
    public static final String DATA_ADDRESSVIEW = "SubscribeTertulia_Address";
    public static final String DATA_ZIPVIEW = "SubscribeTertulia_Zip";
    public static final String DATA_CITYVIEW = "SubscribeTertulia_City";
    public static final String DATA_COUNTRYVIEW = "SubscribeTertulia_Country";
    public static final String DATA_LATITUDEVIEW = "SubscribeTertulia_Latitude";
    public static final String DATA_LONGITUDEVIEW = "SubscribeTertulia_Longitude";

    private String apiEndPoint;
    private EditText titleView, _subjectView;
    private EditText locationView, addressView, zipView, cityView, countryView, latitudeView, longitudeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_terulia);

        Util.setupActionBar(this, R.string.title_activity_subscribe_tertulia, true);

        titleView = (EditText) findViewById(R.id.tsTitle);
        locationView = (EditText) findViewById(R.id.tsLocationName);
        addressView = (EditText) findViewById(R.id.newTertuliaLocationAddress);
        zipView = (EditText) findViewById(R.id.tsLocationZip);
        cityView = (EditText) findViewById(R.id.tsLocationCity);
        countryView = (EditText) findViewById(R.id.tsLocationCountry);
        latitudeView = (EditText) findViewById(R.id.tsLocationLatitude);
        longitudeView = (EditText) findViewById(R.id.tsLocationLongitude);

        if (savedInstanceState != null) restoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(DATA_TITLE, titleView.getText().toString());
        outState.putString(DATA_LOCATIONVIEW, locationView.getText().toString());
        outState.putString(DATA_ZIPVIEW, zipView.getText().toString());
        outState.putString(DATA_CITYVIEW, cityView.getText().toString());
        outState.putString(DATA_COUNTRYVIEW, countryView.getText().toString());
        outState.putString(DATA_LATITUDEVIEW, latitudeView.getText().toString());
        outState.putString(DATA_LONGITUDEVIEW, longitudeView.getText().toString());
    }

    public void onClickSubmitSearch(View view) {
        Log.d("trt", "in onClickSubmitSearch");

        RTertulia rtertulia = new RTertulia();

        rtertulia.name = titleView.getText().toString();
        rtertulia.locationName = locationView.getText().toString();
        rtertulia.zip = zipView.getText().toString();
        rtertulia.city = cityView.getText().toString();
        rtertulia.country = countryView.getText().toString();
        String value = latitudeView.getText().toString();
        rtertulia.latitude = Double.parseDouble(TextUtils.isEmpty(value) ? "0" : value);
        value = longitudeView.getText().toString();
        rtertulia.longitude = Double.parseDouble(TextUtils.isEmpty(value) ? "0" : value);

        JsonElement postParameters = new Gson().toJsonTree(rtertulia);

        MobileServiceClient cli = Util.getMobileServiceClient(this);
        ListenableFuture<JsonElement> future = cli.invokeApi(apiEndPoint, postParameters);
        Futures.addCallback(future, new Callback());

    }

    public void onClickCancelSearch(View view) {
        Log.d("trt", "New tertulia creation cancelled");
        finish();
    }

    // region Private Classes

    public class Callback implements FutureCallback<JsonElement> {
        @Override
        public void onFailure(Throwable e) {
            Util.longToast(SubscribeTeruliaActivity.this, getEMsg(SubscribeTeruliaActivity.this, e.getMessage()));
            Util.logd("New tertulia creation failed");
            Util.logd(e.getMessage());
            setResult(RESULT_FAIL);
            finish();
        }

        @Override
        public void onSuccess(JsonElement result) {
            Util.logd(result.toString());
            Util.longToast(SubscribeTeruliaActivity.this, "IMPLEMENT SUBSCRIBE NEXT ACTIVITY");
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
        restoreTextViewState(savedInstanceState, locationView, DATA_LOCATIONVIEW);
        restoreTextViewState(savedInstanceState, zipView, DATA_ZIPVIEW);
        restoreTextViewState(savedInstanceState, cityView, DATA_CITYVIEW);
        restoreTextViewState(savedInstanceState, countryView, DATA_COUNTRYVIEW);
        restoreTextViewState(savedInstanceState, latitudeView, DATA_LATITUDEVIEW);
        restoreTextViewState(savedInstanceState, longitudeView, DATA_LONGITUDEVIEW);
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
