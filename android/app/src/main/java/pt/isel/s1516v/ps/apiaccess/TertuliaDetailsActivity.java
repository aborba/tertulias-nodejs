package pt.isel.s1516v.ps.apiaccess;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.util.LinkedList;

import pt.isel.s1516v.ps.apiaccess.helpers.Error;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Tertulia;
import pt.isel.s1516v.ps.apiaccess.support.raw.RTertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;

public class TertuliaDetailsActivity extends Activity implements TertuliasApi {

    public final static String SELF_LINK = "SELF_LINK";
    private final static String TERTULIA = "tertulia";
    private TextView titleView, subjectView, locationView, scheduleView, roleView;
    private CheckBox privacyView;
    private Tertulia tertulia;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tertulia_details);

        if (savedInstanceState != null && savedInstanceState.containsKey(TERTULIA))
            tertulia = savedInstanceState.getParcelable(TERTULIA);

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.tda_toolbar),
                R.string.title_activity_tertulia_details,
                Util.IGNORE, Util.IGNORE, true);

        ApiLink selfLink = getIntent().getParcelableExtra(SELF_LINK);
        titleView = (TextView) findViewById(R.id.tertuliaDetailsTitle);
        subjectView = (TextView) findViewById(R.id.tertuliaDetailsSubject);
        locationView = (TextView) findViewById(R.id.tertuliaDetailsLocation);
        scheduleView = (TextView) findViewById(R.id.tertuliaDetailsSchedule);
        roleView = (TextView) findViewById(R.id.tertuliaDetailsRole);
        privacyView = (CheckBox) findViewById(R.id.tertuliaDetailsPrivacy);
        rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        if (tertulia != null) paintUi(tertulia);
        else {
            MobileServiceClient cli = Util.getMobileServiceClient(this);
            ListenableFuture<JsonElement> rTertuliasFuture = cli.invokeApi(selfLink.href, null, selfLink.method, null);
            Futures.addCallback(rTertuliasFuture, new TertuliaPresentation());
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (tertulia != null) {
            outState.putParcelable(TERTULIA, tertulia);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickSubmitEvent(View view) {
        Log.d("trt", "in onClickSubmitEvent");
    }

    public void onClickSubmitMessages(View view) {
        Log.d("trt", "in onClickSubmitMessages");
    }

    public void onClickSubmitEdit(View view) {
        Log.d("trt", "in onClickSubmitEdit");
    }

    public void onClickSubmitMembers(View view) {
        Log.d("trt", "in onClickSubmitMembers");
    }

    private static String getEMsg(Context ctx, String msg) {
        if (!Util.isJson(msg)) return msg;
        Error error = new Gson().fromJson(msg, Error.class);
        return error.getStatusCodeMessage(ctx);
    }

    private class TertuliaPresentation implements FutureCallback<JsonElement> {
        @Override
        public void onFailure(Throwable e) {
            Context ctx = TertuliaDetailsActivity.this;
            Util.longSnack(rootView, getEMsg(ctx, e.getMessage()));
        }

        @Override
        public void onSuccess(JsonElement result) {
            new AsyncTask<JsonElement, Void, Tertulia>(){
                @Override
                protected Tertulia doInBackground(JsonElement... params) {
                    ApiTertulia apiTertulia = new Gson().fromJson(params[0], ApiTertulia.class);
                    tertulia = new Tertulia(apiTertulia.tertulia, apiTertulia.links);
                    return tertulia;
                }

                @Override
                protected void onPostExecute(Tertulia tertulia) {
                    paintUi(tertulia);
                }
            }.execute(result);
        }
    }

    private void paintUi(Tertulia tertulia) {
        titleView.setText(tertulia.name);
        subjectView.setText(tertulia.subject);
        locationView.setText(tertulia.location.toString());
        String scheduleText;
        if (!TextUtils.isEmpty(tertulia.scheduleType)) {
            scheduleText = tertulia.scheduleType;
            if (!TextUtils.isEmpty(tertulia.scheduleDescription)) scheduleText += " - " + tertulia.scheduleDescription;
        } else scheduleText = tertulia.scheduleDescription;
        scheduleView.setText(scheduleText);
        roleView.setText(tertulia.role_type);
        privacyView.setChecked(tertulia.isPrivate);
    }

}
