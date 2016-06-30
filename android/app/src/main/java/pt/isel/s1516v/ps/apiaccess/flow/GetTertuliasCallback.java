package pt.isel.s1516v.ps.apiaccess.flow;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import pt.isel.s1516v.ps.apiaccess.MainActivity;
import pt.isel.s1516v.ps.apiaccess.TertuliasArrayAdapter;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.domain.Tertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaListItem;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliasList;

public class GetTertuliasCallback implements FutureCallback<JsonElement> {
    final Context ctx;
    final ListView listView;
    final Futurizable<JsonElement> future;
    final FutureCallback<JsonElement> futureCallback;
    final View rootView;

    public GetTertuliasCallback(Context ctx, ListView listView, Futurizable<JsonElement> future, FutureCallback<JsonElement> futureCallback) {
        this.ctx = ctx;
        this.listView = listView;
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
        new AsyncTask<JsonElement, Void, Tertulia[]>() {
            @Override
            protected Tertulia[] doInBackground(JsonElement... params) {
                ApiTertuliasList apiTertuliasList = new Gson().fromJson(params[0], ApiTertuliasList.class);
                LinkedList<Tertulia> tertulias = new LinkedList<>();
                for (ApiTertuliaListItem apiTertuliaListItem : apiTertuliasList.apiTertuliaListItems) {
                    Tertulia tertulia = new Tertulia(apiTertuliaListItem);
                    tertulias.add(tertulia);
                }
                return tertulias.toArray(new Tertulia[tertulias.size()]);
            }

            @Override
            protected void onPostExecute(Tertulia[] tertulias) {
                MainActivity.tertulias = tertulias;
                ArrayAdapter<Tertulia> adapter = new TertuliasArrayAdapter((Activity)ctx, tertulias);
                listView.setAdapter(adapter);
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
