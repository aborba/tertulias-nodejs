package pt.isel.s1516v.ps.apiaccess.flow;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.concurrent.ExecutionException;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiMe;
import pt.isel.s1516v.ps.apiaccess.ui.MaUiManager;

public class GetMeCallback implements FutureCallback<JsonElement> {
    final Context ctx;
    final String rel;
    final Futurizable<JsonElement> future;
    final FutureCallback<JsonElement> futureCallback;
    final View rootView;
    final MaUiManager uiClient;

    public GetMeCallback(Context ctx, String rel, Futurizable<JsonElement> future, FutureCallback<JsonElement> futureCallback, MaUiManager uiClient) {
        this.ctx = ctx;
        this.rel = rel;
        this.future = future;
        this.futureCallback = futureCallback;
        this.uiClient = uiClient;
        rootView = ((Activity)ctx).getWindow().getDecorView().findViewById(android.R.id.content);
    }

    @Override
    public void onSuccess(JsonElement result) {
        ApiMe apiMe = new Gson().fromJson(result, ApiMe.class);
        if (apiMe.me.picture != null) {
            Util.logd(apiMe.me.picture);
            uiClient.drawer.setIcon(apiMe.me.picture);
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
        String message = t.getMessage();
        Util.longSnack(rootView, message);
    }

}
