package pt.isel.s1516v.ps.apiaccess.flow;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;

import java.util.concurrent.ExecutionException;

import pt.isel.s1516v.ps.apiaccess.MainActivity;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;

public class GetHomeCallback implements FutureCallback<JsonElement> {
    final Context ctx;
    final String rel;
    final Futurizable<JsonElement> future;
    final FutureCallback<JsonElement> futureCallback;
    final View rootView;
    private static boolean isRetryingForTokenExpired;

    public GetHomeCallback(Context ctx, String rel, Futurizable<JsonElement> future, FutureCallback<JsonElement> futureCallback) {
        this.ctx = ctx;
        this.rel = rel;
        this.future = future;
        this.futureCallback = futureCallback;
        rootView = ((Activity)ctx).getWindow().getDecorView().findViewById(android.R.id.content);
    }

    @Override
    public void onFailure(Throwable t) {
        if (!isRetryingForTokenExpired) {
            isRetryingForTokenExpired = true;
            Util.longSnack(rootView, "Login token expired; Retrying..."); // TODO: strings
            MobileServiceClient cli = Util.getMobileServiceClient(ctx);
            cli.setCurrentUser(null);
            Futures.addCallback(
                    cli.login(MobileServiceAuthenticationProvider.Google),
                    new LoginCallback(ctx, null, null, null)
            );
            return;
        }
        String message = t.getMessage();
        Util.longSnack(rootView, message);
    }

    @Override
    public void onSuccess(JsonElement result) {
        isRetryingForTokenExpired = false;
        MainActivity.apiHome.swap(new Gson().fromJson(result, ApiLinks.class));
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
}
