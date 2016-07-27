package pt.isel.s1516v.ps.apiaccess.tertuliacreation;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.Arrays;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Error;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.api.CrApiMonthlySchedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.api.CrApiMonthlyWSchedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.api.CrApiWeeklySchedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiAddress;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthly;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthlyW;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiSchedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiWeekly;

public class NewTertuliaActivity extends Activity implements TertuliasApi, DialogFragmentResult {

    public final static int ACTIVITY_REQUEST_CODE = NEW_TERTULIA_RETURN_CODE;
    private final static String INSTANCE_KEY_TERTULIA = "tertulia";

    public static final String INTENT_TERTULIAS = "Tertulias";

    private String apiEndPoint, apiMethod;
    private EditText titleView, subjectView, locationView, addressView, zipView, cityView, countryView, latitudeView, longitudeView;
    private TextView scheduleView;
    private CheckBox privacyView;

    private CrUiTertulia crUiTertulia;
    private int scheduleType;
    private CrUiSchedule crUiSchedule;
    ApiLinks apiLinks;

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
        zipView = (EditText) findViewById(R.id.tda_zip);
        cityView = (EditText) findViewById(R.id.tda_city);
        countryView = (EditText) findViewById(R.id.tda_country);
        latitudeView = (EditText) findViewById(R.id.tda_latitude);
        longitudeView = (EditText) findViewById(R.id.tda_longitude);
        scheduleView = (TextView) findViewById(R.id.tda_schedule);
        privacyView = (CheckBox) findViewById(R.id.nta_IsPrivate);

        if (savedInstanceState != null) restoreInstanceState(savedInstanceState);

        apiLinks = getIntent().getParcelableExtra(LINKS_LABEL);
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
                INSTANCE_KEY_TERTULIA,
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
        crUiTertulia = new CrUiTertulia(titleView, subjectView,
                locationView, addressView, zipView, cityView, countryView, latitudeView, longitudeView,
                scheduleType, crUiSchedule,
                privacyView);

        if (!isNameValid(crUiTertulia.name, getIntent().getStringArrayExtra(INTENT_TERTULIAS))) {
            Util.longSnack(view, R.string.new_tertulia_toast_invalid_name);
            return;
        }

        if (crUiSchedule == null) {
            Util.longSnack(view, R.string.new_tertulia_toast_no_schedule_selected);
            return;
        }

        JsonElement postParameters;
        String apiLinksKey = null;
        switch (scheduleType) {
            case WEEKLY:
                CrUiWeekly crWeekly = (CrUiWeekly) crUiSchedule;
//                ApiCreateTertuliaWeekly tertuliaWeekly = new ApiCreateTertuliaWeekly(this, crUiTertulia, crWeekly);
                CrApiWeeklySchedule tertuliaWeekly = new CrApiWeeklySchedule(this, crUiTertulia, crWeekly);
                postParameters = new Gson().toJsonTree(tertuliaWeekly);
                apiLinksKey = LINK_CREATE_WEEKLY;
                break;
            case MONTHLY:
                CrUiMonthly crMonthly = (CrUiMonthly) crUiSchedule;
//                ApiCreateTertuliaMonthly tertuliaMonthly = new ApiCreateTertuliaMonthly(crUiTertulia, crMonthly);
                CrApiMonthlySchedule tertuliaMonthly = new CrApiMonthlySchedule(crUiTertulia, crMonthly);
                postParameters = new Gson().toJsonTree(tertuliaMonthly);
                apiLinksKey = LINK_CREATE_MONTHLY;
                break;
            case MONTHLYW:
                CrUiMonthlyW crMonthlyW = (CrUiMonthlyW) crUiSchedule;
//                ApiCreateTertuliaMonthlyW tertuliaMonthlyW = new ApiCreateTertuliaMonthlyW(this, crUiTertulia, crMonthlyW);
                CrApiMonthlyWSchedule tertuliaMonthlyW = new CrApiMonthlyWSchedule(this, crUiTertulia, crMonthlyW);
                postParameters = new Gson().toJsonTree(tertuliaMonthlyW);
                apiLinksKey = LINK_CREATE_MONTHLYW;
                break;
            case YEARLY:
                apiLinksKey = LINK_CREATE_YEARLY;
                throw new UnsupportedOperationException();
            case YEARLYW:
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
                locationView.setText(place.getName());
                CrUiAddress crUiAddress = new CrUiAddress(place.getAddress().toString());
                addressView.setText(crUiAddress.address);
                zipView.setText(crUiAddress.zip);
                cityView.setText(crUiAddress.city);
                countryView.setText(crUiAddress.country);
                latitudeView.setText(String.format(Locale.getDefault(), "%.6f", place.getLatLng().latitude));
                longitudeView.setText(String.format(Locale.getDefault(), "%.6f", place.getLatLng().longitude));
                break;
            case WeeklyActivity.ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_FAIL)
                    return;
                scheduleType = data.getIntExtra("type", -1);
                CrUiWeekly crWeekly = data.getParcelableExtra("result");
                crUiSchedule = crWeekly;
                scheduleView.setText(crWeekly.toString());
                break;
            case MonthlyActivity.ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_FAIL)
                    return;
                scheduleType = data.getIntExtra("type", -1);
                CrUiMonthly crMonthly = data.getParcelableExtra("result");
                crUiSchedule = crMonthly;
                scheduleView.setText(crMonthly.toString());
                break;
            case MonthlywActivity.ACTIVITY_REQUEST_CODE:
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
                startActivityForResult(intent, WeeklyActivity.ACTIVITY_REQUEST_CODE);
                break;
            case MONTHLY:
                intent = new Intent(this, MonthlyActivity.class);
                startActivityForResult(intent, MonthlyActivity.ACTIVITY_REQUEST_CODE);
                break;
            case MONTHLYW:
                intent = new Intent(this, MonthlywActivity.class);
                startActivityForResult(intent, MonthlywActivity.ACTIVITY_REQUEST_CODE);
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
        crUiTertulia = savedInstanceState.getParcelable(INSTANCE_KEY_TERTULIA);
        crUiTertulia.updateViews(titleView, subjectView,
                locationView, addressView, zipView, cityView, countryView, latitudeView, longitudeView,
                scheduleView, privacyView);
        crUiSchedule = crUiTertulia.crUiSchedule;
        scheduleType = crUiTertulia.getScheduleType(this);
    }

    // endregion

}
