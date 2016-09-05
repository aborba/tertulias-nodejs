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
import android.os.AsyncTask;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceFeatures;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.HttpConstants;
import com.microsoft.windowsazure.mobileservices.http.MobileServiceHttpClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pt.isel.s1516v.ps.apiaccess.MainActivity;
import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.TertuliasApplication;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiError;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiGoogleCredentials;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.ui.MaUiManager;

public class GetHomeCallback implements FutureCallback<JsonElement> {

    private final static String API_ROOT_END_POINT = "/";

    private final Context ctx;
    private final String rel;
    private final Futurizable<JsonElement> future;
    private final FutureCallback<JsonElement> futureCallback;
    private final MaUiManager uiManager;
    private final boolean isRetry;
    private final View rootView;

    public GetHomeCallback(Context ctx, String rel, Futurizable<JsonElement> future, FutureCallback<JsonElement> futureCallback, MaUiManager uiManager, boolean isRetry) {
        this.ctx = ctx;
        this.rel = rel;
        this.future = future;
        this.futureCallback = futureCallback;
        this.uiManager = uiManager;
        this.isRetry = isRetry;

        rootView = Util.getRootView(ctx);
    }

    @Override
    public void onSuccess(JsonElement result) {
        if (result != null) {
            Util.longSnack(rootView, R.string.main_activity_routes_defined);
            new AsyncTask<JsonElement, Void, ApiLinks>() {

                @Override
                protected ApiLinks doInBackground(JsonElement... params) {
                    JsonElement result = params[0];
                    ApiLinks apiLinks = new Gson().fromJson(result, ApiLinks.class);
                    return apiLinks;
                }

                @Override
                protected void onPostExecute(ApiLinks apiLinks) {
                    MainActivity.apiHome.swap(apiLinks);
                    if (future != null) {
                        if (futureCallback != null) {
                            uiManager.showProgressBar();
                            Futures.addCallback(future.getFuture(), futureCallback);
                            return;
                        }
                        else
                            try {
                                future.getFuture().get();
                                return;
                            } catch (InterruptedException | ExecutionException | IllegalArgumentException e) {
                                Util.longSnack(rootView, R.string.main_activity_error_remote_dialog);
                                e.printStackTrace();
                            }
                    }
                    uiManager.hideProgressBar();
                }
            }.execute(result);
        }
    }

    @Override
    public void onFailure(Throwable t) {
        boolean isTokenValid = Util.isCurrentTokenValid(ctx);
        Date expirationDate = Util.getTokenExpirationDate(Util.getAuthenticationToken(ctx));
        Date currentDate = new Date();
        if (isRetry) {
            uiManager.hideProgressBar();
            Util.longSnack(rootView, R.string.main_activity_status_access_error + "\n" + t.getMessage());
            futureCallback.onFailure(t);
            return;
        }
        boolean invalidToken = ! Util.isCurrentTokenValid(ctx) || Util.isApiError(t, 401);
        if (invalidToken || Util.isErrorCause(t, "timeout")) {
            Util.longSnack(rootView, invalidToken ? R.string.main_activity_token_expired : R.string.main_activity_server_timeout);
            GetData<JsonElement> getHome = new GetData<>(ctx, MainActivity.API_ROOT_END_POINT, null, uiManager);
            GetHomeCallback getHomeCallback = new GetHomeCallback(ctx, null, future, futureCallback, uiManager, true);
            Util.refreshToken(ctx, uiManager.getRootView(), getHome, getHomeCallback);
            return;
        }
        futureCallback.onFailure(t);
    }

}
