package pt.isel.s1516v.ps.apiaccess.tertuliaedition;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

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
import pt.isel.s1516v.ps.apiaccess.helpers.GeoPosition;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionMonthly;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionWeekly;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundle;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundleMonthly;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundleMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundleWeekly;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.DialogFragmentResult;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.MonthlyActivity;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.MonthlywActivity;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ScheduleSelectionDialog;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.WeeklyActivity;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.api.CrApiMonthlySchedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.api.CrApiMonthlyWSchedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiAddress;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthly;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthlyW;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiSchedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiWeekly;
import pt.isel.s1516v.ps.apiaccess.tertuliaedition.api.EdApiWeeklySchedule;
import pt.isel.s1516v.ps.apiaccess.tertuliaedition.ui.EdUiManager;

public class EditTertuliaActivity extends Activity implements TertuliasApi, DialogFragmentResult {

    public final static int ACTIVITY_REQUEST_CODE = EDIT_TERTULIA_RETURN_CODE;
    private final static String TERTULIA_INSTANCE_STATE_LABEL = "tertulia";

    public static final String INTENT_LINKS = "ApiLinkArray";
    public static final String INTENT_TERTULIA = "ReadTertulia";
    public static final String INTENT_TERTULIAS = "tertulias";

    private static final String APILINKS_KEY = LINK_UPDATE;

    private TertuliaEdition tertulia;
    private EdUiManager uiManager;
    private ApiLinks apiLinks;
    private String apiEndPoint, apiMethod;
    private int scheduleType;
    private CrUiSchedule crUiSchedule;
    private CrUiTertulia crUiTertulia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tertulia);

        Intent intent = getIntent();

        if (!intent.hasExtra(INTENT_LINKS)) {
            setResult(RESULT_FAIL);
            finish();
        }
        apiLinks = new ApiLinks(Util.extractParcelableArray(intent, INTENT_LINKS, ApiLink.class));

        if (savedInstanceState != null && savedInstanceState.containsKey(TERTULIA_INSTANCE_STATE_LABEL))
            tertulia = savedInstanceState.getParcelable(TERTULIA_INSTANCE_STATE_LABEL);

        uiManager = new EdUiManager(this);

        Util.setupToolBar(this, (Toolbar) uiManager.getView(EdUiManager.UIRESOURCE.TOOLBAR),
                R.string.title_activity_edit_tertulia,
                Util.IGNORE, Util.IGNORE, null, true);

        if (tertulia == null)
            tertulia = intent.getParcelableExtra(INTENT_TERTULIA);

        uiManager.set(tertulia);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (tertulia != null)
            outState.putParcelable(TERTULIA_INSTANCE_STATE_LABEL, tertulia);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                Util.longSnack(uiManager.getRootView(), R.string.edit_tertulia_toast_cancel);
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickMapLookup(View view) {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            String latitude = uiManager.getLatitudeData();
            String longitude = uiManager.getLongitudeData();
            if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
                LatLng latLng = GeoPosition.getLatLng(uiManager);
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

    public void onClickUpdateTertulia(View view) {

//        if (!isNameValid(crUiTertulia.name, getIntent().getStringArrayExtra(INTENT_TERTULIAS))) {
//            Util.longSnack(view, R.string.new_tertulia_toast_invalid_name);
//            return;
//        }

        if (uiManager.getTextViewValue(EdUiManager.UIRESOURCE.SCHEDULE) == null) {
            Util.longSnack(view, R.string.new_tertulia_toast_no_schedule_selected);
            return;
        }

        tertulia = uiManager.update(tertulia);

        JsonElement postParameters;
        String apiLinksKey = null;
        switch (tertulia.scheduleType.name()) {
            case "WEEKLY":
                if ( ! (tertulia instanceof TertuliaEditionWeekly))
                    throw new RuntimeException();
                TertuliaEditionWeekly tertuliaEditionWeekly = (TertuliaEditionWeekly) tertulia;
                EdApiWeeklySchedule tertuliaWeekly = new EdApiWeeklySchedule(this, tertuliaEditionWeekly);
                postParameters = new Gson().toJsonTree(tertuliaWeekly);
                apiLinksKey = LINK_UPDATE;
                break;
            case "MONTHLYD":
                CrUiMonthly crMonthly = (CrUiMonthly) crUiSchedule;
//                ApiCreateTertuliaMonthly tertuliaMonthly = new ApiCreateTertuliaMonthly(crUiTertulia, crMonthly);
                CrApiMonthlySchedule tertuliaMonthly = new CrApiMonthlySchedule(crUiTertulia, crMonthly);
                postParameters = new Gson().toJsonTree(tertuliaMonthly);
                apiLinksKey = LINK_UPDATE;
                break;
            case "MONTHLYW":
                if ( ! (tertulia instanceof TertuliaEditionMonthlyW))
                    throw new RuntimeException();
                TertuliaEditionMonthlyW tertuliaEditionMonthlyW = (TertuliaEditionMonthlyW)tertulia;
                CrApiMonthlyWSchedule tertuliaMonthlyW = new CrApiMonthlyWSchedule(this, tertuliaEditionMonthlyW);
                postParameters = new Gson().toJsonTree(tertuliaMonthlyW);
                apiLinksKey = LINK_UPDATE;
                break;
            case "YEARLY":
                apiLinksKey = LINK_UPDATE;
                throw new UnsupportedOperationException();
            case "YEARLYW":
                apiLinksKey = LINK_UPDATE;
                throw new UnsupportedOperationException();
            default:
                throw new IllegalArgumentException();
        }
        apiEndPoint = apiLinks.getRoute(apiLinksKey);
        apiMethod = apiLinks.getMethod(apiLinksKey);

        Futures.addCallback(Util.getMobileServiceClient(this)
                        .invokeApi(apiEndPoint, postParameters, apiMethod, null)
                , new Callback(uiManager.getRootView()));
    }

    private boolean isNameValid(String name) {
        if (TextUtils.isEmpty(name) ) return false;
        name = name.toLowerCase();
        Parcelable[] tertulias = getIntent().getParcelableArrayExtra(INTENT_TERTULIAS);
        for (Parcelable tertulia : tertulias) {
            String s = ((TertuliaEdition) tertulia).name;
            if (((TertuliaEdition) tertulia).name.toLowerCase().equals(name))
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
                uiManager.setTextViewValue(EdUiManager.UIRESOURCE.LOCATION, place.getName().toString());
                CrUiAddress crUiAddress = new CrUiAddress(place.getAddress().toString());
                uiManager.setTextViewValue(EdUiManager.UIRESOURCE.ADDRESS, crUiAddress.address);
                uiManager.setTextViewValue(EdUiManager.UIRESOURCE.ZIP, crUiAddress.zip);
                uiManager.setTextViewValue(EdUiManager.UIRESOURCE.CITY, crUiAddress.city);
                uiManager.setTextViewValue(EdUiManager.UIRESOURCE.COUNTRY, crUiAddress.country);
                uiManager.setTextViewValue(EdUiManager.UIRESOURCE.LATITUDE, String.format(Locale.getDefault(), "%.6f", place.getLatLng().latitude));
                uiManager.setTextViewValue(EdUiManager.UIRESOURCE.LONGITUDE, String.format(Locale.getDefault(), "%.6f", place.getLatLng().longitude));
                break;
            case WeeklyActivity.ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_FAIL)
                    return;
                CrUiWeekly crWeekly = data.getParcelableExtra("result");
                tertulia = new TertuliaEditionWeekly(tertulia, crWeekly);
                uiManager.set(tertulia);
                break;
            case MonthlyActivity.ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_FAIL)
                    return;
                scheduleType = data.getIntExtra("type", -1);
                CrUiMonthly crMonthly = data.getParcelableExtra("result");
                crUiSchedule = crMonthly;
                uiManager.setTextViewValue(EdUiManager.UIRESOURCE.SCHEDULE, crMonthly.toString());
                break;
            case MonthlywActivity.ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_FAIL)
                    return;
                scheduleType = data.getIntExtra("type", -1);
                CrUiMonthlyW crMonthlyW = data.getParcelableExtra("result");
                crUiSchedule = crMonthlyW;
                uiManager.setTextViewValue(EdUiManager.UIRESOURCE.SCHEDULE, crMonthlyW.toString());
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
        public void onSuccess(JsonElement result) {
            Util.logd("Tertulia updated");
            Util.logd(result.toString());
            setResult(RESULT_SUCCESS);
            finish();
        }

        @Override
        public void onFailure(Throwable e) {
            Util.logd("Tertulia update failed");
            Util.logd(e.getMessage());
            setResult(RESULT_FAIL);
            finish();
        }

    }

    public class UpdateCallback implements FutureCallback<JsonElement> {
        private View view;

        public UpdateCallback(View view) {
            this.view = view;
        }

        @Override
        public void onSuccess(JsonElement result) {
            Util.longSnack(view, R.string.new_tertulia_toast_success);
            Util.logd("Tertulia updated"); // TODO: strings
            Util.logd(result.toString());
            setResult(RESULT_SUCCESS);
            finish();
        }

        @Override
        public void onFailure(Throwable e) {
            Util.longSnack(view, getEMsg(EditTertuliaActivity.this, e.getMessage()));
            Util.logd("Tertulia update failed"); // TODO: strings
            Util.logd(e.getMessage());
            setResult(RESULT_FAIL);
            finish();
        }
    }

    private class TertuliaPresentation implements FutureCallback<JsonElement> {
        @Override
        public void onSuccess(JsonElement result) {
            new AsyncTask<JsonElement, Void, TertuliaEdition>() {
                @Override
                protected TertuliaEdition doInBackground(JsonElement... params) {
                    ApiTertuliaEditionBundle apiTertulia = new Gson().fromJson(params[0], ApiTertuliaEditionBundle.class);
                    tertulia = new TertuliaEdition(apiTertulia.tertulia, apiTertulia.links);
                    if (tertulia.scheduleType != null) {
                        switch (tertulia.scheduleType.name()) {
                            case "WEEKLY":
                                ApiTertuliaEditionBundleWeekly apiReadTertuliaWeekly = new Gson().fromJson(params[0], ApiTertuliaEditionBundleWeekly.class);
                                tertulia = new TertuliaEditionWeekly(apiReadTertuliaWeekly);
                                break;
                            case "MONTHLYD":
                                ApiTertuliaEditionBundleMonthly apiReadTertuliaMonthly = new Gson().fromJson(params[0], ApiTertuliaEditionBundleMonthly.class);
                                tertulia = new TertuliaEditionMonthly(apiReadTertuliaMonthly);
                                break;
                            case "MONTHLYW":
                                ApiTertuliaEditionBundleMonthlyW apiReadTertuliaMonthlyW = new Gson().fromJson(params[0], ApiTertuliaEditionBundleMonthlyW.class);
                                tertulia = new TertuliaEditionMonthlyW(apiReadTertuliaMonthlyW);
                                break;
                            case "YEARLY":
                            case "YEARLW":
                                break;
                            default:
                                throw new IllegalArgumentException();
                        }
                    }
                    return tertulia;
                }

                @Override
                protected void onPostExecute(TertuliaEdition tertulia) {
                    uiManager.set(tertulia);
                }
            }.execute(result);
        }

        @Override
        public void onFailure(Throwable e) {
            Context ctx = EditTertuliaActivity.this;
            Util.longSnack(uiManager.getRootView(), getEMsg(ctx, e.getMessage()));
        }

    }

    // endregion

    // region Private Methods

    private static String getEMsg(Context ctx, String msg) {
        if (!Util.isJson(msg)) return msg;
        Error error = new Gson().fromJson(msg, Error.class);
        return error.getStatusCodeMessage(ctx);
    }

    // endregion

}
