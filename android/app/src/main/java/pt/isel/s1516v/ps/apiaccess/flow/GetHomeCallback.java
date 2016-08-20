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
import android.util.Pair;
import android.view.View;

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
    private final MaUiManager uiManager;
    private final String rel;
    private final Futurizable<JsonElement> future;
    private final FutureCallback<JsonElement> futureCallback;
    private final boolean isRetry;
    private final View rootView;

    public GetHomeCallback(Context ctx, MaUiManager uiManager, String rel, Futurizable<JsonElement> future, FutureCallback<JsonElement> futureCallback, boolean isRetry) {
        this.ctx = ctx;
        this.uiManager = uiManager;
        this.rel = rel;
        this.future = future;
        this.futureCallback = futureCallback;
        this.isRetry = isRetry;

        rootView = ((Activity)ctx).getWindow().getDecorView().findViewById(android.R.id.content);
    }

    @Override
    public void onSuccess(JsonElement result) {
        uiManager.hideProgressBar();
        if (result != null) {
            Util.longSnack(rootView, R.string.main_activity_routes_defined);
            ApiLinks apiLinks = new Gson().fromJson(result, ApiLinks.class);
            MainActivity.apiHome.swap(apiLinks);
        }
        if (future != null) {
            if (futureCallback != null)
                Futures.addCallback(future.getFuture(), futureCallback);
            else
                try {
                    future.getFuture().get();
                } catch (InterruptedException | ExecutionException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void onFailure(Throwable t) {
        // TODO: Refresh token
        if (! isRetry && Util.isApiError(t, 401)) { // && ! Util.isCurrentTokenValid(ctx)) {
            Util.longSnack(rootView, R.string.main_activity_token_expired);
            GetData<JsonElement> getHome = new GetData<>(ctx, MainActivity.API_ROOT_END_POINT, null);
            GetHomeCallback getHomeCallback = new GetHomeCallback(ctx, uiManager, null, future, futureCallback, true);
            Util.refreshToken(ctx, uiManager.getRootView(), getHome, getHomeCallback);
            return;
        }
        uiManager.hideProgressBar();
        Util.longSnack(rootView, t.getMessage());
        futureCallback.onFailure(t);
    }

}
