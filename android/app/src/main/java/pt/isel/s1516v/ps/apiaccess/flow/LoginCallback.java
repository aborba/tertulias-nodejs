package pt.isel.s1516v.ps.apiaccess.flow;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import java.util.concurrent.ExecutionException;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.raw.RHome;

public class LoginCallback implements FutureCallback<MobileServiceUser> {

    final Futurizable<JsonElement> future;
    final FutureCallback<JsonElement> futureCallback;
    final Context ctx;
    final View rootView;

    public LoginCallback(Context ctx) {
        this(ctx, null, null);
    }

    public LoginCallback(Context ctx, Futurizable<JsonElement> future, FutureCallback<JsonElement> futureCallback) {
        this.ctx = ctx;
        this.future = future;
        this.futureCallback = futureCallback;
        rootView = ((Activity)ctx).getWindow().getDecorView().findViewById(android.R.id.content);
    }

    @Override
    public void onFailure(Throwable e) {
        Util.longSnack(rootView, R.string.main_activity_login_failed_message);
    }

    @Override
    public void onSuccess(MobileServiceUser user) {
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
