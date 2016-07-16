package pt.isel.s1516v.ps.apiaccess.flow;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import pt.isel.s1516v.ps.apiaccess.MainActivity;
import pt.isel.s1516v.ps.apiaccess.TertuliasArrayRvAdapter;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.domain.Tertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaListItem;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliasList;

public class GetTertuliasCallback implements FutureCallback<JsonElement> {
    final Context ctx;
    final RecyclerView listView;
    final TextView emptyView;
    final ProgressBar progressBar;
    final Futurizable<JsonElement> future;
    final FutureCallback<JsonElement> futureCallback;
    final View rootView;

    public GetTertuliasCallback(Context ctx, RecyclerView listView, TextView emptyView, ProgressBar progressBar, Futurizable<JsonElement> future, FutureCallback<JsonElement> futureCallback) {
        this.ctx = ctx;
        this.listView = listView;
        this.emptyView = emptyView;
        this.progressBar = progressBar;
        this.future = future;
        this.futureCallback = futureCallback;
        rootView = ((Activity) ctx).getWindow().getDecorView().findViewById(android.R.id.content);
    }

    @Override
    public void onFailure(Throwable t) {
        String message = t.getMessage();
        Util.longSnack(rootView, message);
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

        new AsyncTask<JsonElement, Void, Tertulia[]>() {
            @Override
            protected Tertulia[] doInBackground(JsonElement... params) {
                ApiTertuliasList apiTertuliasList = new Gson().fromJson(params[0], ApiTertuliasList.class);
                LinkedList<Tertulia> tertulias = new LinkedList<>();
                for (ApiTertuliaListItem apiTertuliaListItem : apiTertuliasList.items) {
                    Tertulia tertulia = new Tertulia(apiTertuliaListItem);
                    tertulias.add(tertulia);
                }
                return tertulias.toArray(new Tertulia[tertulias.size()]);
            }

            @Override
            protected void onPostExecute(Tertulia[] tertulias) {
                MainActivity.tertulias = tertulias;
                TertuliasArrayRvAdapter adapter = new TertuliasArrayRvAdapter((Activity)ctx, tertulias != null ? tertulias : new Tertulia[0]);
                listView.setAdapter(adapter);
                if (tertulias == null || tertulias.length == 0) {
                    listView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    listView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.INVISIBLE);
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
}
