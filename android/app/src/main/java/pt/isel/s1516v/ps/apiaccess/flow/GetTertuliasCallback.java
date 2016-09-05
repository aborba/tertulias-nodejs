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

package pt.isel.s1516v.ps.apiaccess.flow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import pt.isel.s1516v.ps.apiaccess.MainActivity;
import pt.isel.s1516v.ps.apiaccess.TertuliasArrayAdapter;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.notifications.NotificationSettings;
import pt.isel.s1516v.ps.apiaccess.notifications.RegistrationIntentService;
import pt.isel.s1516v.ps.apiaccess.notifications.TertuliasNotificationsHandler;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaListItem;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaListItem;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliasList;
import pt.isel.s1516v.ps.apiaccess.ui.MaUiManager;

public class GetTertuliasCallback implements FutureCallback<JsonElement> {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private final Context ctx;
    private final Futurizable<JsonElement> future;
    private final FutureCallback<JsonElement> futureCallback;
    private final MaUiManager uiManager;
    private final boolean isRetry;
    private int countDown = 2;

    public GetTertuliasCallback(Context ctx, Futurizable<JsonElement> future, FutureCallback<JsonElement> futureCallback, MaUiManager uiManager, boolean isRetry) {
        this.ctx = ctx;
        this.future = future;
        this.futureCallback = futureCallback;
        this.uiManager = uiManager;
        this.isRetry = isRetry;
    }

    @Override
    public void onSuccess(final JsonElement result) {
        new ExtractApiLinksAsync().execute(result);
        new ExtractTertuliasAsync().execute(result);
    }

    @Override
    public void onFailure(Throwable t) {
        uiManager.hideProgressBar();
        String message = t.getMessage();
        Util.longSnack(uiManager.getRootView(), message);
    }

    private class ExtractApiLinksAsync extends AsyncTask<JsonElement, Void, ApiLink[]> {
        @Override
        protected ApiLink[] doInBackground(JsonElement... params) {
            ApiTertuliasList apiTertuliasList = new Gson().fromJson(params[0], ApiTertuliasList.class);
            return apiTertuliasList.links;
        }

        @Override
        protected void onPostExecute(ApiLink[] links) {
            MainActivity.apiLinks = new ApiLinks(links);
            if (--countDown == 0)
                uiManager.hideProgressBar();
        }
    }

    private class ExtractTertuliasAsync extends AsyncTask<JsonElement, Void, TertuliaListItem[]> {
        @Override
        protected TertuliaListItem[] doInBackground(JsonElement... params) {
            ApiTertuliasList apiTertuliasList = new Gson().fromJson(params[0], ApiTertuliasList.class);
            LinkedList<TertuliaListItem> tertulias = new LinkedList<>();
            for (ApiTertuliaListItem apiTertuliaListItem : apiTertuliasList.items) {
                TertuliaListItem tertulia = new TertuliaListItem(apiTertuliaListItem);
                tertulias.add(tertulia);
            }
            return tertulias.toArray(new TertuliaListItem[tertulias.size()]);
        }

        @Override
        protected void onPostExecute(TertuliaListItem[] tertulias) {
            MainActivity.tertulias = tertulias;

            NotificationsManager.handleNotifications(ctx, NotificationSettings.SenderId, TertuliasNotificationsHandler.class);
            registerWithNotificationHubs();

            TertuliasArrayAdapter adapter = new TertuliasArrayAdapter((Activity)ctx, tertulias != null ? tertulias : new TertuliaListItem[0]);
            uiManager.swapAdapter(adapter)
                    .setEmpty(tertulias == null || tertulias.length == 0)
                    .hideProgressBar();
            if (future != null) {
                if (futureCallback != null) {
                    Futures.addCallback(future.getFuture(), futureCallback);
                    return;
                }
                try {
                    future.getFuture().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            if (--countDown == 0)
                uiManager.hideProgressBar();
        }
    }

    private void registerWithNotificationHubs()
    {
        Util.logd(" Registering with Notification Hubs");

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(ctx, RegistrationIntentService.class);
            ctx.startService(intent);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(ctx);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog((Activity) ctx, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Util.logd("This device is not supported by Google Play Services.");
                Util.longToast(ctx, "This device is not supported by Google Play Services."); // TODO Strings
            }
            return false;
        }
        return true;
    }

}
