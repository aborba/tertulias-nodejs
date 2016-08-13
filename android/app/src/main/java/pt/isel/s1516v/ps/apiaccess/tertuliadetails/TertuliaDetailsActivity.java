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

import java.util.ArrayList;
import java.util.Arrays;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Error;
import pt.isel.s1516v.ps.apiaccess.helpers.GeoPosition;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.memberinvitation.SearchContactsActivity;
import pt.isel.s1516v.ps.apiaccess.memberinvitation.ViewMembersActivity;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaScheduleMonthlyD;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaScheduleMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaScheduleWeekly;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundle;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundleMonthlyD;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundleMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundleWeekly;
import pt.isel.s1516v.ps.apiaccess.tertuliadetails.ui.DtUiManager;
import pt.isel.s1516v.ps.apiaccess.tertuliaedition.EditTertuliaActivity;

public class TertuliaDetailsActivity extends Activity implements TertuliasApi {

    public final static int ACTIVITY_REQUEST_CODE = TERTULIA_DETAILS_RETURN_CODE;
    public final static String SELF_LINK = LINK_SELF;
    private final static String TERTULIA_INSTANCE_STATE_LABEL = "tertulia";
    private DtUiManager uiManager;
    private ApiLinks apiLinks;
    private TertuliaEdition tertulia;

    // region Activity Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tertulia_details);

        if (savedInstanceState != null && savedInstanceState.containsKey(TERTULIA_INSTANCE_STATE_LABEL))
            tertulia = savedInstanceState.getParcelable(TERTULIA_INSTANCE_STATE_LABEL);

        uiManager = new DtUiManager(this);

        Toolbar toolbar = (Toolbar) uiManager.getView(DtUiManager.UIRESOURCE.TOOLBAR);

        Util.setupToolBar(this, (Toolbar) uiManager.getView(DtUiManager.UIRESOURCE.TOOLBAR),
                R.string.title_activity_tertulia_details,
                Util.IGNORE, Util.IGNORE, null, true);

        apiLinks = new ApiLinks(Util.extractParcelableArray(getIntent(), INTENT_LINKS, ApiLink.class));

        if (tertulia != null)
            uiManager.set(tertulia);
        else
            refreshDataAndViews();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EditTertuliaActivity.ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    Util.longSnack(uiManager.getRootView(), R.string.edit_tertulia_toast_updated);
                    refreshDataAndViews();
                } else
                    Util.longSnack(uiManager.getRootView(), R.string.tertulia_details_not_updated);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void refreshDataAndViews() {
        MobileServiceClient cli = Util.getMobileServiceClient(this);
        ListenableFuture<JsonElement> rTertuliasFuture = cli.invokeApi(apiLinks.getRoute(LINK_SELF), null, apiLinks.getMethod(LINK_SELF), null);
        Futures.addCallback(rTertuliasFuture, new TertuliaPresentation());
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
        if (!tertulia.role.name.toLowerCase().equals("owner")) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_activity_edit_tertulia)
                    .setMessage(R.string.message_dialog_edit_tertulia_owner_warning)
                    .setIcon(android.R.drawable.ic_lock_lock)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }
        ApiLink[] apiLinks = Util.extractParcelableArray(getIntent(), INTENT_LINKS, ApiLink.class);
        if (apiLinks == null) {
            Util.longSnack(view, R.string.tertulia_details_undefined_links);
            return;
        }
        Intent intent = new Intent(this, EditTertuliaActivity.class);
        intent.putExtra(EditTertuliaActivity.INTENT_LINKS, apiLinks);
        intent.putExtra(EditTertuliaActivity.INTENT_TERTULIA, tertulia);
        startActivityForResult(intent, EditTertuliaActivity.ACTIVITY_REQUEST_CODE);
    }

    public void onClickSubmitMembers(View view) {
        Log.d("trt", "in onClickSubmitMembers");
        Intent intent = new Intent(this, ViewMembersActivity.class);
        ArrayList<ApiLink> links = new ArrayList<>(Arrays.asList(tertulia.links));
        intent.putParcelableArrayListExtra(ViewMembersActivity.INTENT_LINKS, links);
        startActivity(intent);
    }

    public void onClickUnsubscribe(View view) {
        if (tertulia.role.name.toLowerCase().equals("owner")) {
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

    // endregion

    public class UnsubscriptionCallback implements FutureCallback<JsonElement> {

        final View rootView;

        public UnsubscriptionCallback(View rootView) {
            this.rootView = rootView;
        }

        @Override
        public void onSuccess(JsonElement result) {
            Util.longSnack(rootView, R.string.message_dialog_unsubscribe_complete);
            setResult(RESULT_OK);
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

    public class UpdateCallback implements FutureCallback<JsonElement> {

        final View rootView;

        public UpdateCallback(View rootView) {
            this.rootView = rootView;
        }

        @Override
        public void onSuccess(JsonElement result) {
            Util.longSnack(rootView, R.string.edit_tertulia_toast_updated);
            setResult(RESULT_OK);
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
            new AsyncTask<JsonElement, Void, TertuliaEdition>() {
                @Override
                protected TertuliaEdition doInBackground(JsonElement... params) {
                    ApiTertuliaEditionBundle apiTertulia = new Gson().fromJson(params[0], ApiTertuliaEditionBundle.class);
                    tertulia = new TertuliaEdition(apiTertulia.tertulia, apiTertulia.links);
                    return tertulia;
                }

                @Override
                protected void onPostExecute(TertuliaEdition tertulia) {
                    uiManager.set(tertulia);
                }
            }.execute(result);
        }
    }

    private class TertuliaPresentation implements FutureCallback<JsonElement> {
        @Override
        public void onSuccess(JsonElement result) {
            new AsyncTask<JsonElement, Void, TertuliaEdition>() {
                @Override
                protected TertuliaEdition doInBackground(JsonElement... params) {
                    ApiTertuliaEditionBundle apiTertulia = new Gson().fromJson(params[0], ApiTertuliaEditionBundle.class);
                    switch (apiTertulia.tertulia.sc_name.toUpperCase()) {
                        case "WEEKLY":
                            ApiTertuliaEditionBundleWeekly apiReadTertuliaWeekly = new Gson().fromJson(params[0], ApiTertuliaEditionBundleWeekly.class);
                            TertuliaScheduleWeekly tertuliaScheduleWeekly = new TertuliaScheduleWeekly(apiReadTertuliaWeekly.weekly);
                            tertulia = new TertuliaEdition(apiReadTertuliaWeekly.tertulia, tertuliaScheduleWeekly, apiReadTertuliaWeekly.links);
                            break;
                        case "MONTHLYD":
                            ApiTertuliaEditionBundleMonthlyD apiReadTertuliaMonthly = new Gson().fromJson(params[0], ApiTertuliaEditionBundleMonthlyD.class);
                            TertuliaScheduleMonthlyD tertuliaScheduleMonthlyD = new TertuliaScheduleMonthlyD(apiReadTertuliaMonthly.monthly);
                            tertulia = new TertuliaEdition(apiReadTertuliaMonthly.tertulia, tertuliaScheduleMonthlyD, apiReadTertuliaMonthly.links);
                            break;
                        case "MONTHLYW":
                            ApiTertuliaEditionBundleMonthlyW apiReadTertuliaMonthlyW = new Gson().fromJson(params[0], ApiTertuliaEditionBundleMonthlyW.class);
                            TertuliaScheduleMonthlyW tertuliaScheduleMonthlyW = new TertuliaScheduleMonthlyW(apiReadTertuliaMonthlyW.monthlyw);
                            tertulia = new TertuliaEdition(apiReadTertuliaMonthlyW.tertulia, tertuliaScheduleMonthlyW, apiReadTertuliaMonthlyW.links);
                            break;
                        case "YEARLY":
                        case "YEARLW":
                            break;
                        default:
                            throw new IllegalArgumentException();
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
            Context ctx = TertuliaDetailsActivity.this;
            Util.longSnack(uiManager.getRootView(), getEMsg(ctx, e.getMessage()));
        }
    }
}
