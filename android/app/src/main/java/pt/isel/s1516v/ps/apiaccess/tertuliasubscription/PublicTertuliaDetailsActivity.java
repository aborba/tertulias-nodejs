package pt.isel.s1516v.ps.apiaccess.tertuliasubscription;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
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
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionMonthly;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionWeekly;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundle;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundleMonthly;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundleMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundleWeekly;
import pt.isel.s1516v.ps.apiaccess.tertuliadetails.PlacePresentationActivity;
import pt.isel.s1516v.ps.apiaccess.tertuliasubscription.ui.SbUiManager;

public class PublicTertuliaDetailsActivity extends Activity implements TertuliasApi {

    public final static int ACTIVITY_REQUEST_CODE = SUBSCRIBE_PUBLIC_TERTULIA_RETURN_CODE;
    public final static String SELF_LINK = LINK_SELF;
    public final static String LINKS = INTENT_LINKS;
    public final static String LINK_ACTION = LINK_SUBSCRIBE;
    private final static String TERTULIA_INSTANCE_STATE_LABEL = "tertulia";
    SbUiManager uiManager;
    private TertuliaEdition tertulia;

    // region Activity Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_tertulia_details);

        if (savedInstanceState != null && savedInstanceState.containsKey(TERTULIA_INSTANCE_STATE_LABEL))
            tertulia = savedInstanceState.getParcelable(TERTULIA_INSTANCE_STATE_LABEL);

        EnumMap<SbUiManager.UIRESOURCE, Integer> viewsMap = SbUiManager.getDictionary();
        viewsMap.put(SbUiManager.UIRESOURCE.TITLE, R.id.ptda_title);
        viewsMap.put(SbUiManager.UIRESOURCE.SUBJECT, R.id.ptda_subject);
        viewsMap.put(SbUiManager.UIRESOURCE.LOCATION, R.id.ptda_locationName);
        viewsMap.put(SbUiManager.UIRESOURCE.ADDRESS, R.id.ptda_address);
        viewsMap.put(SbUiManager.UIRESOURCE.ZIP, R.id.ptda_zip);
        viewsMap.put(SbUiManager.UIRESOURCE.CITY, R.id.ptda_city);
        viewsMap.put(SbUiManager.UIRESOURCE.COUNTRY, R.id.ptda_country);
        viewsMap.put(SbUiManager.UIRESOURCE.LATITUDE, R.id.ptda_latitude);
        viewsMap.put(SbUiManager.UIRESOURCE.LONGITUDE, R.id.ptda_longitude);
        viewsMap.put(SbUiManager.UIRESOURCE.SCHEDULE, R.id.ptda_schedule);
        uiManager = new SbUiManager(this, viewsMap);

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.toolbar),
                R.string.title_activity_public_tertulia_details,
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
        intent.putExtra(PlacePresentationActivity.INTENT_LABEL, uiManager.getTextViewValue(SbUiManager.UIRESOURCE.LOCATION));
        intent.putExtra(PlacePresentationActivity.INTENT_SNIPPET, String.format("%s, %s",
                uiManager.getTextViewValue(SbUiManager.UIRESOURCE.ADDRESS),
                uiManager.getTextViewValue(SbUiManager.UIRESOURCE.CITY))
        );
        startActivity(intent);
    }

    public void onSubscribeButtonClicked(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_dialog_subscribe_public_tertulia)
                .setMessage(R.string.message_dialog_subscribe_public_tertulia)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Parcelable[] parcelables = getIntent().getParcelableArrayExtra(LINKS);
                        ApiLink[] apiLinks = new ApiLink[parcelables.length];
                        for (int i = 0; i < parcelables.length; i++)
                            apiLinks[i] = (ApiLink) parcelables[i];
                        String apiEndPoint = null;
                        String apiMethod = null;
                        for (ApiLink apiLink : apiLinks)
                            if (apiLink.rel.equals(LINK_ACTION)) {
                                apiEndPoint = apiLink.href;
                                apiMethod = apiLink.method;
                                break;
                            }
                        if (apiEndPoint == null || apiMethod.isEmpty()) {
                            Util.longSnack(findViewById(android.R.id.content), R.string.main_activity_routes_undefined);
                            return;
                        }
                        Futures.addCallback(Util.getMobileServiceClient(PublicTertuliaDetailsActivity.this)
                                        .invokeApi(apiEndPoint, null, apiMethod, null)
                                , new SubscriptionCallback(findViewById(android.R.id.content)));
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public class SubscriptionCallback implements FutureCallback<JsonElement> {

        final View rootView;

        public SubscriptionCallback(View rootView) {
            this.rootView = rootView;
        }

        @Override
        public void onSuccess(JsonElement result) {
            setResult(RESULT_SUCCESS);
            finish();
        }

        @Override
        public void onFailure(Throwable e) {
            Util.longSnack(rootView, e.getMessage());
            Util.logd("Public tertulia subscription failed");
            Util.logd(e.getMessage());
            setResult(RESULT_FAIL);
        }
    }

    private static String getEMsg(Context ctx, String msg) {
        if (!Util.isJson(msg)) return msg;
        Error error = new Gson().fromJson(msg, Error.class);
        return error.getStatusCodeMessage(ctx);
    }

    private class TertuliaPresentation implements FutureCallback<JsonElement> {
        @Override
        public void onFailure(Throwable e) {
            Context ctx = PublicTertuliaDetailsActivity.this;
            Util.longSnack(uiManager.getRootView(), getEMsg(ctx, e.getMessage()));
        }

        @Override
        public void onSuccess(JsonElement result) {
            new AsyncTask<JsonElement, Void, TertuliaEdition>(){
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
    }
}
