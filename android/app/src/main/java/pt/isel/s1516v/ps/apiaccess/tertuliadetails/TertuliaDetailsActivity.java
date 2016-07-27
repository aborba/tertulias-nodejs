package pt.isel.s1516v.ps.apiaccess.tertuliadetails;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.util.EnumMap;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Error;
import pt.isel.s1516v.ps.apiaccess.helpers.GeoPosition;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.ReadMonthly;
import pt.isel.s1516v.ps.apiaccess.support.domain.ReadMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.domain.ReadTertulia;
import pt.isel.s1516v.ps.apiaccess.support.domain.ReadWeekly;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiReadTertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiReadTertuliaMonthly;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiReadTertuliaMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiReadTertuliaWeekly;
import pt.isel.s1516v.ps.apiaccess.tertuliadetails.ui.DtUiManager;

public class TertuliaDetailsActivity extends Activity implements TertuliasApi {

    public final static int ACTIVITY_REQUEST_CODE = TERTULIA_DETAILS_RETURN_CODE;
    public final static String SELF_LINK = LINK_SELF;
    private final static String TERTULIA_INSTANCE_STATE_LABEL = "tertulia";
    DtUiManager uiManager;
    private ReadTertulia tertulia;

    // region Activity Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tertulia_details);

        if (savedInstanceState != null && savedInstanceState.containsKey(TERTULIA_INSTANCE_STATE_LABEL))
            tertulia = savedInstanceState.getParcelable(TERTULIA_INSTANCE_STATE_LABEL);

        EnumMap<DtUiManager.UIRESOURCE, Integer> viewsMap = DtUiManager.getDictionary();
        viewsMap.put(DtUiManager.UIRESOURCE.TITLE, R.id.tda_title);
        viewsMap.put(DtUiManager.UIRESOURCE.SUBJECT, R.id.tda_subject);
        viewsMap.put(DtUiManager.UIRESOURCE.ROLE, R.id.tda_role);
        viewsMap.put(DtUiManager.UIRESOURCE.LOCATION, R.id.tda_locationName);
        viewsMap.put(DtUiManager.UIRESOURCE.ADDRESS, R.id.tda_address);
        viewsMap.put(DtUiManager.UIRESOURCE.ZIP, R.id.tda_zip);
        viewsMap.put(DtUiManager.UIRESOURCE.CITY, R.id.tda_city);
        viewsMap.put(DtUiManager.UIRESOURCE.COUNTRY, R.id.tda_country);
        viewsMap.put(DtUiManager.UIRESOURCE.LATITUDE, R.id.tda_latitude);
        viewsMap.put(DtUiManager.UIRESOURCE.LONGITUDE, R.id.tda_longitude);
        viewsMap.put(DtUiManager.UIRESOURCE.SCHEDULE, R.id.tda_schedule);
        viewsMap.put(DtUiManager.UIRESOURCE.PRIVACY, R.id.tda_isPrivate);
        uiManager = new DtUiManager(this, viewsMap);

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.tda_toolbar),
                R.string.title_activity_tertulia_details,
                Util.IGNORE, Util.IGNORE, null, true);

        ApiLink selfLink = getIntent().getParcelableExtra(SELF_LINK);

        if (tertulia != null) uiManager.set(tertulia);
        else {
            MobileServiceClient cli = Util.getMobileServiceClient(this);
            ListenableFuture<JsonElement> rTertuliasFuture = cli.invokeApi(selfLink.href, null, selfLink.method, null);
            Futures.addCallback(rTertuliasFuture, new TertuliaPresentation());
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (tertulia != null) {
            outState.putParcelable(TERTULIA_INSTANCE_STATE_LABEL, tertulia);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickMapLookup(View view) {
        if (!uiManager.isGeo()) {
            Util.longSnack(view, R.string.tertulia_details_undefined_coordinates);
            return;
        }

        Intent intent = new Intent(this, PlacePresentationActivity.class);
        intent.putExtra(PlacePresentationActivity.INTENT_LATITUDE, GeoPosition.getLatitude(uiManager));
        intent.putExtra(PlacePresentationActivity.INTENT_LONGITUDE, GeoPosition.getLongitude(uiManager));
        intent.putExtra(PlacePresentationActivity.INTENT_LABEL, uiManager.getTextViewValue(DtUiManager.UIRESOURCE.LOCATION));
        intent.putExtra(PlacePresentationActivity.INTENT_SNIPPET, String.format("%s, %s",
                uiManager.getTextViewValue(DtUiManager.UIRESOURCE.ADDRESS),
                uiManager.getTextViewValue(DtUiManager.UIRESOURCE.CITY))
        );
        startActivity(intent);
    }

    public void onClickSubmitEvent(View view) {
        Log.d("trt", "in onClickSubmitEvent");
    }

    public void onClickSubmitMessages(View view) {
        Log.d("trt", "in onClickSubmitMessages");
    }

    public void onClickSubmitEdit(View view) {
        Log.d("trt", "in onClickSubmitEdit");
    }

    public void onClickSubmitMembers(View view) {
        Log.d("trt", "in onClickSubmitMembers");
    }

    public void onClickUnsubscribe(View view) {
        if (tertulia.role_type.toLowerCase().equals("owner")) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_dialog_unsubscribe_public_tertulia)
                    .setMessage(R.string.message_dialog_unsubscribe_public_tertulia_owner_warning)
                    .setIcon(android.R.drawable.ic_lock_lock)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_dialog_unsubscribe_public_tertulia)
                .setMessage(R.string.message_dialog_unsubscribe_public_tertulia)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        for (ApiLink apiLink : tertulia.links) {
                            if (apiLink.rel.equals(LINK_UNSUBSCRIBE)) {
                                String apiEndPoint = apiLink.href;
                                String apiMethod = apiLink.method;
                                Futures.addCallback(Util.getMobileServiceClient(TertuliaDetailsActivity.this)
                                                .invokeApi(apiEndPoint, null, apiMethod, null)
                                        , new UnsubscriptionCallback(findViewById(android.R.id.content)));
                                return;
                            }
                        }
                        Util.longSnack(uiManager.getRootView(), "Oops! Unsubscribe link not found."); // TODO: strings.xml
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public class UnsubscriptionCallback implements FutureCallback<JsonElement> {

        final View rootView;

        public UnsubscriptionCallback(View rootView) {
            this.rootView = rootView;
        }

        @Override
        public void onSuccess(JsonElement result) {
            Util.longSnack(rootView, "Unsubscribed from Tertulia"); // TODO: strings.xml
            setResult(RESULT_SUCCESS);
            finish();
        }

        @Override
        public void onFailure(Throwable e) {
            Util.longSnack(rootView, e.getMessage());
            Util.logd("Public tertulia unsubscription failed");
            Util.logd(e.getMessage());
            setResult(RESULT_FAIL);
        }
    }

    private static String getEMsg(Context ctx, String msg) {
        if (!Util.isJson(msg)) return msg;
        Error error = new Gson().fromJson(msg, Error.class);
        return error.getStatusCodeMessage(ctx);
    }

    private class FetchSchedule implements FutureCallback<JsonElement> {
        @Override
        public void onFailure(Throwable e) {
            Context ctx = TertuliaDetailsActivity.this;
            Util.longSnack(uiManager.getRootView(), getEMsg(ctx, e.getMessage()));
        }

        @Override
        public void onSuccess(JsonElement result) {
            new AsyncTask<JsonElement, Void, ReadTertulia>() {
                @Override
                protected ReadTertulia doInBackground(JsonElement... params) {
                    ApiReadTertulia apiTertulia = new Gson().fromJson(params[0], ApiReadTertulia.class);
                    tertulia = new ReadTertulia(apiTertulia.tertulia, apiTertulia.links);
                    return tertulia;
                }

                @Override
                protected void onPostExecute(ReadTertulia tertulia) {
                    uiManager.set(tertulia);
                }
            }.execute(result);
        }
    }

    private class TertuliaPresentation implements FutureCallback<JsonElement> {
        @Override
        public void onFailure(Throwable e) {
            Context ctx = TertuliaDetailsActivity.this;
            Util.longSnack(uiManager.getRootView(), getEMsg(ctx, e.getMessage()));
        }

        @Override
        public void onSuccess(JsonElement result) {
            new AsyncTask<JsonElement, Void, ReadTertulia>() {
                @Override
                protected ReadTertulia doInBackground(JsonElement... params) {
                    ApiReadTertulia apiTertulia = new Gson().fromJson(params[0], ApiReadTertulia.class);
                    tertulia = new ReadTertulia(apiTertulia.tertulia, apiTertulia.links);
                    if (tertulia.scheduleType != null) {
                        switch (tertulia.scheduleType) {
                            case "Weekly":
                                ApiReadTertuliaWeekly apiReadTertuliaWeekly = new Gson().fromJson(params[0], ApiReadTertuliaWeekly.class);
                                tertulia = new ReadWeekly(apiReadTertuliaWeekly);
                                break;
                            case "MonthlyD":
                                ApiReadTertuliaMonthly apiReadTertuliaMonthly = new Gson().fromJson(params[0], ApiReadTertuliaMonthly.class);
                                tertulia = new ReadMonthly(apiReadTertuliaMonthly);
                                break;
                            case "MonthlyW":
                                ApiReadTertuliaMonthlyW apiReadTertuliaMonthlyW = new Gson().fromJson(params[0], ApiReadTertuliaMonthlyW.class);
                                tertulia = new ReadMonthlyW(apiReadTertuliaMonthlyW);
                                break;
                            case "Yearly":
                            case "YearlW":
                                break;
                            default:
                                throw new IllegalArgumentException();
                        }
                    }
                    return tertulia;
                }

                @Override
                protected void onPostExecute(ReadTertulia tertulia) {
                    uiManager.set(tertulia);
                }
            }.execute(result);
        }
    }
}
