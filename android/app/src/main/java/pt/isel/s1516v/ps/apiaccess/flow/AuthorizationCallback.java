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
import android.view.View;
import android.widget.ProgressBar;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import java.util.concurrent.ExecutionException;

import pt.isel.s1516v.ps.apiaccess.MainActivity;
import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.tertuliadetails.ui.DtUiManager;
import pt.isel.s1516v.ps.apiaccess.ui.MaUiManager;

public class AuthorizationCallback implements FutureCallback<MobileServiceUser> {

    final Context ctx;
    final MaUiManager uiManager;
    final Futurizable<JsonElement> future;
    final FutureCallback<JsonElement> futureCallback;
    final View rootView;

    public AuthorizationCallback(Context ctx, MaUiManager uiManager, Futurizable<JsonElement> future, FutureCallback<JsonElement> futureCallback) {
        this.ctx = ctx;
        this.uiManager = uiManager;
        this.future = future;
        this.futureCallback = futureCallback;

        rootView = Util.getRootView(ctx);
    }

    @Override
    public void onSuccess(MobileServiceUser user) {
        Util.cacheCredentialsAsync(ctx, user);
        if (future != null) {
            if (futureCallback != null) {
                Futures.addCallback(future.getFuture(), futureCallback);
                return;
            }
            else
                try {
                    future.getFuture().get();
                    return;
                } catch (InterruptedException | ExecutionException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
        }
        uiManager.hideProgressBar();
    }

    @Override
    public void onFailure(Throwable t) {
        uiManager.hideProgressBar();
        Util.longSnack(rootView, R.string.main_activity_login_failed_message);
        if (futureCallback != null)
            futureCallback.onFailure(t);
    }

}
