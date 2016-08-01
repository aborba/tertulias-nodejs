package pt.isel.s1516v.ps.apiaccess.flow;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import pt.isel.s1516v.ps.apiaccess.MainActivity;
import pt.isel.s1516v.ps.apiaccess.TertuliasArrayRvAdapter;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaListItem;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaListItem;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliasList;
import pt.isel.s1516v.ps.apiaccess.ui.MaUiManager;

public class GetTertuliasCallback implements FutureCallback<JsonElement> {
    final Context ctx;
    final MaUiManager uiManager;
    final Futurizable<JsonElement> future;
    final FutureCallback<JsonElement> futureCallback;

    public GetTertuliasCallback(Context ctx, MaUiManager uiManager, Futurizable<JsonElement> future, FutureCallback<JsonElement> futureCallback) {
        this.ctx = ctx;
        this.uiManager = uiManager;
        this.future = future;
        this.futureCallback = futureCallback;
    }

    @Override
    public void onSuccess(JsonElement result) {

        new AsyncTask<JsonElement, Void, ApiLink[]>() {
            @Override
            protected ApiLink[] doInBackground(JsonElement... params) {
                ApiTertuliasList apiTertuliasList = new Gson().fromJson(params[0], ApiTertuliasList.class);
                return apiTertuliasList.links;
            }

            @Override
            protected void onPostExecute(ApiLink[] links) {
                MainActivity.apiLinks = new ApiLinks(links);
            }
        }.execute(result);

        new AsyncTask<JsonElement, Void, TertuliaListItem[]>() {
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
                TertuliasArrayRvAdapter adapter = new TertuliasArrayRvAdapter((Activity)ctx, tertulias != null ? tertulias : new TertuliaListItem[0]);
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
            }
        }.execute(result);
    }

    @Override
    public void onFailure(Throwable t) {
        String message = t.getMessage();
        Util.longSnack(uiManager.getRootView(), message);
    }

}
