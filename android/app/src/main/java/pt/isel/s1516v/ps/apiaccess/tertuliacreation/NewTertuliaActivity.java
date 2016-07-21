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
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Error;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Tertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiCreateTertuliaMonthly;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiCreateTertuliaMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiCreateTertuliaWeekly;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiAddress;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthly;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthlyW;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiSchedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiWeekly;

public class NewTertuliaActivity extends Activity implements TertuliasApi, DialogFragmentResult {

    public final static int REQUEST_CODE = NEW_TERTULIA_RETURN_CODE;
    private final static String TERTULIA_KEY = "tertulia";

    public static final String MY_TERTULIAS = "MyTertulias";

    private static final String APILINKS_KEY = LINK_CREATE;

    private String apiEndPoint, apiMethod;
    private EditText titleView, subjectView, locationView, addressView, zipView, cityView, countryView, latitudeView, longitudeView;
    private TextView scheduleView;
    private CheckBox privacyView;

    private CrUiTertulia crUiTertulia;
    private int scheduleType;
    private CrUiSchedule crUiSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tertulia);

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.nta_toolbar),
                R.string.title_activity_new_tertulia,
                Util.IGNORE, Util.IGNORE, null, true);

        titleView = (EditText) findViewById(R.id.nta_Title);
        subjectView = (EditText) findViewById(R.id.nta_Subject);
        locationView = (EditText) findViewById(R.id.nta_LocationName);
        addressView = (EditText) findViewById(R.id.nta_Address);
        zipView = (EditText) findViewById(R.id.tda_Zip);
        cityView = (EditText) findViewById(R.id.tda_City);
        countryView = (EditText) findViewById(R.id.tda_Country);
        latitudeView = (EditText) findViewById(R.id.tda_Latitude);
        longitudeView = (EditText) findViewById(R.id.tda_Longitude);
        scheduleView = (TextView) findViewById(R.id.tda_Schedule);
        privacyView = (CheckBox) findViewById(R.id.nta_IsPrivate);

        if (savedInstanceState != null) restoreInstanceState(savedInstanceState);

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
        outState.putParcelable(
                TERTULIA_KEY,
                new CrUiTertulia(titleView, subjectView,
                        locationView, addressView, zipView, cityView, countryView, latitudeView, longitudeView,
                        scheduleType, crUiSchedule,
                        privacyView)
        );
    }

    public void onClickMapLookup(View view) {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            String latitude = latitudeView.getText().toString();
            String longitude = longitudeView.getText().toString();
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
        String value = latitudeView.getText().toString();
        Double latitude = Double.parseDouble(TextUtils.isEmpty(value) ? "0" : value);
        value = longitudeView.getText().toString();
        Double longitude = Double.parseDouble(TextUtils.isEmpty(value) ? "0" : value);
        String name = titleView.getText().toString();

        if (!isNameValid(name)) {
            Util.longSnack(findViewById(android.R.id.content), R.string.new_tertulia_toast_invalid_name);
            return;
        }

        JsonElement postParameters;
        switch (scheduleType) {
            case WEEKLY:
                CrUiWeekly crWeekly = crUiSchedule != null ? (CrUiWeekly) crUiSchedule : new CrUiWeekly(-1, -1);
                ApiCreateTertuliaWeekly tertuliaWeekly = new ApiCreateTertuliaWeekly(
                        name,
                        subjectView.getText().toString(),
                        locationView.getText().toString(),
                        addressView.getText().toString(),
                        zipView.getText().toString(),
                        cityView.getText().toString(),
                        countryView.getText().toString(),
                        String.valueOf(latitude),
                        String.valueOf(longitude),
                        crWeekly.getWeekDay(this), // weekDay
                        crWeekly.skip, // skip
                        privacyView.isChecked());
                postParameters = new Gson().toJsonTree(tertuliaWeekly);
                break;
            case MONTHLY:
                CrUiMonthly crMonthly = crUiSchedule != null ? (CrUiMonthly) crUiSchedule : new CrUiMonthly(-1, true, -1);
                ApiCreateTertuliaMonthly tertuliaMonthly = new ApiCreateTertuliaMonthly(
                        titleView.getText().toString(),
                        subjectView.getText().toString(),
                        locationView.getText().toString(),
                        addressView.getText().toString(),
                        zipView.getText().toString(),
                        cityView.getText().toString(),
                        countryView.getText().toString(),
                        String.valueOf(latitude),
                        String.valueOf(longitude),
                        crMonthly.dayNr, // dayNr
                        crMonthly.isFromStart, // fromStart
                        crMonthly.skip, // skip
                        privacyView.isChecked());
                postParameters = new Gson().toJsonTree(crMonthly);
                break;
            case MONTHLYW:
                CrUiMonthlyW crMonthlyW = crUiSchedule != null ? (CrUiMonthlyW) crUiSchedule : new CrUiMonthlyW(-1, -1, true, -1);
                ApiCreateTertuliaMonthlyW tertuliaMonthlyW = new ApiCreateTertuliaMonthlyW(
                        titleView.getText().toString(),
                        subjectView.getText().toString(),
                        locationView.getText().toString(),
                        addressView.getText().toString(),
                        zipView.getText().toString(),
                        cityView.getText().toString(),
                        countryView.getText().toString(),
                        String.valueOf(latitude),
                        String.valueOf(longitude),
                        crMonthlyW.getWeekDay(this), // weekDay
                        crMonthlyW.weekNr, // weekNr
                        crMonthlyW.isFromStart, // fromStart
                        crMonthlyW.skip, // skip
                        privacyView.isChecked());
                postParameters = new Gson().toJsonTree(tertuliaMonthlyW);
                break;
            case YEARLY:
                throw new UnsupportedOperationException();
            case YEARLYW:
                throw new UnsupportedOperationException();
            default:
                throw new IllegalArgumentException();
        }

        Futures.addCallback(Util.getMobileServiceClient(this)
                .invokeApi(apiEndPoint, postParameters, apiMethod, null)
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
                CrUiAddress crUiAddress = new CrUiAddress(place.getAddress().toString());
                addressView.setText(crUiAddress.address);
                zipView.setText(crUiAddress.zip);
                cityView.setText(crUiAddress.city);
                countryView.setText(crUiAddress.country);
                latitudeView.setText(String.format(Locale.getDefault(), "%.6f", place.getLatLng().latitude));
                longitudeView.setText(String.format(Locale.getDefault(), "%.6f", place.getLatLng().longitude));
                break;
            case WeeklyActivity.REQUEST_CODE:
                if (resultCode == RESULT_FAIL)
                    return;
                scheduleType = data.getIntExtra("type", -1);
                CrUiWeekly crWeekly = data.getParcelableExtra("result");
                crUiSchedule = crWeekly;
                scheduleView.setText(crWeekly.toString());
                break;
            case MonthlyActivity.REQUEST_CODE:
                if (resultCode == RESULT_FAIL)
                    return;
                scheduleType = data.getIntExtra("type", -1);
                CrUiMonthly crMonthly = data.getParcelableExtra("result");
                crUiSchedule = crMonthly;
                scheduleView.setText(crMonthly.toString());
                break;
            case MonthlywActivity.REQUEST_CODE:
                if (resultCode == RESULT_FAIL)
                    return;
                scheduleType = data.getIntExtra("type", -1);
                CrUiMonthlyW crMonthlyW = data.getParcelableExtra("result");
                crUiSchedule = crMonthlyW;
                scheduleView.setText(crMonthlyW.toString());
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
                intent = new Intent(this, WeeklyActivity.class);
                startActivityForResult(intent, WeeklyActivity.REQUEST_CODE);
                break;
            case MONTHLY:
                intent = new Intent(this, MonthlyActivity.class);
                startActivityForResult(intent, MonthlyActivity.REQUEST_CODE);
                break;
            case MONTHLYW:
                intent = new Intent(this, MonthlywActivity.class);
                startActivityForResult(intent, MonthlywActivity.REQUEST_CODE);
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
        crUiTertulia = savedInstanceState.getParcelable(TERTULIA_KEY);
        crUiTertulia.updateViews(titleView, subjectView,
                locationView, addressView, zipView, cityView, countryView, latitudeView, longitudeView,
                scheduleView, privacyView);
        crUiSchedule = crUiTertulia.crUiSchedule;
        scheduleType = crUiTertulia.getScheduleType(this);
    }

    // endregion

}
