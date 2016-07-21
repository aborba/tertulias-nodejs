package pt.isel.s1516v.ps.apiaccess.flow;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutionException;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.TertuliasArrayRvAdapter;
import pt.isel.s1516v.ps.apiaccess.helpers.CircleTransform;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.domain.Tertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiMe;

public class GetMeCallback implements FutureCallback<JsonElement> {
    final Context ctx;
    final String rel;
    final Futurizable<JsonElement> future;
    final FutureCallback<JsonElement> futureCallback;
    final View rootView;
    final ImageView faceView;

    public GetMeCallback(Context ctx, String rel, Futurizable<JsonElement> future, FutureCallback<JsonElement> futureCallback, ImageView faceView) {
        this.ctx = ctx;
        this.rel = rel;
        this.future = future;
        this.futureCallback = futureCallback;
        this.faceView = faceView;
        rootView = ((Activity)ctx).getWindow().getDecorView().findViewById(android.R.id.content);
    }

    @Override
    public void onFailure(Throwable t) {
        String message = t.getMessage();
        Util.longSnack(rootView, message);
    }

    @Override
    public void onSuccess(JsonElement result) {
        ApiMe apiMe = new Gson().fromJson(result, ApiMe.class);
        Util.logd(apiMe.me.picture);
        if (apiMe.me.picture != null) {
            Picasso.with(ctx).load(apiMe.me.picture).transform(new CircleTransform()).into(faceView);
            faceView.setVisibility(View.VISIBLE);
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
}
