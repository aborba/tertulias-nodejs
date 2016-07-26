package pt.isel.s1516v.ps.apiaccess.tertuliasubscription;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Error;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;
import pt.isel.s1516v.ps.apiaccess.support.domain.ReadTertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertulia;

public class PublicTertuliaDetailsActivity extends Activity implements TertuliasApi {

    public final static int ACTIVITY_REQUEST_CODE = SUBSCRIBE_PUBLIC_TERTULIA_RETURN_CODE;
    public final static String SELF_LINK = LINK_SELF;
    public final static String LINKS = LINKS_LABEL;
    public final static String LINK_ACTION = LINK_SUBSCRIBE;
    private final static String TERTULIA_INSTANCE_STATE_LABEL = "tertulia";
    private TextView titleView, subjectView, locationView, scheduleView;
    private ReadTertulia tertulia;
    private View rootView;

    // region Activity Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_tertulia_details);

        if (savedInstanceState != null && savedInstanceState.containsKey(TERTULIA_INSTANCE_STATE_LABEL))
            tertulia = savedInstanceState.getParcelable(TERTULIA_INSTANCE_STATE_LABEL);

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.tda_toolbar),
                R.string.title_activity_public_tertulia_details,
                Util.IGNORE, Util.IGNORE, null, true);

        ApiLink selfLink = getIntent().getParcelableExtra(SELF_LINK);
        titleView = (TextView) findViewById(R.id.ptda_title);
        subjectView = (TextView) findViewById(R.id.ptda_subject);
        locationView = (TextView) findViewById(R.id.ptda_location);
        scheduleView = (TextView) findViewById(R.id.ptda_schedule);
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
            outState.putParcelable(TERTULIA_INSTANCE_STATE_LABEL, tertulia);
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

    public void onSubscribeButtonClicked(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_dialog_subscribe_public_tertulia)
                .setMessage(R.string.message_dialog_subscribe_public_tertulia)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Parcelable[] parcelables = getIntent().getParcelableArrayExtra(LINKS);
                        ApiLink[] apiLinks = new ApiLink[parcelables.length];
                        for (int i = 0; i < parcelables.length; i++)
                            apiLinks[i] = (ApiLink) parcelables[i];
                        String apiEndPoint = null;
                        String apiMethod = null;
                        for (ApiLink apiLink : apiLinks)
                            if (apiLink.rel.equals(LINK_ACTION)) {
                                apiEndPoint = apiLink.href;
                                apiMethod = apiLink.method;
                                break;
                            }
                        if (apiEndPoint == null || apiMethod.isEmpty()) {
                            Util.longSnack(findViewById(android.R.id.content), R.string.main_activity_routes_undefined);
                            return;
                        }
                        Futures.addCallback(Util.getMobileServiceClient(PublicTertuliaDetailsActivity.this)
                                        .invokeApi(apiEndPoint, null, apiMethod, null)
                                , new SubscriptionCallback(findViewById(android.R.id.content)));
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public class SubscriptionCallback implements FutureCallback<JsonElement> {

        final View rootView;

        public SubscriptionCallback(View rootView) {
            this.rootView = rootView;
        }

        @Override
        public void onSuccess(JsonElement result) {
            setResult(RESULT_SUCCESS);
            finish();
        }

        @Override
        public void onFailure(Throwable e) {
            Util.longSnack(rootView, e.getMessage());
            Util.logd("Public tertulia subscription failed");
            Util.logd(e.getMessage());
            setResult(RESULT_FAIL);
        }
    }

    private static String getEMsg(Context ctx, String msg) {
        if (!Util.isJson(msg)) return msg;
        Error error = new Gson().fromJson(msg, Error.class);
        return error.getStatusCodeMessage(ctx);
    }

    private class TertuliaPresentation implements FutureCallback<JsonElement> {
        @Override
        public void onFailure(Throwable e) {
            Context ctx = PublicTertuliaDetailsActivity.this;
            Util.longSnack(rootView, getEMsg(ctx, e.getMessage()));
        }

        @Override
        public void onSuccess(JsonElement result) {
            new AsyncTask<JsonElement, Void, ReadTertulia>(){
                @Override
                protected ReadTertulia doInBackground(JsonElement... params) {
                    ApiTertulia apiTertulia = new Gson().fromJson(params[0], ApiTertulia.class);
                    tertulia = new ReadTertulia(apiTertulia.tertulia, apiTertulia.links);
                    return tertulia;
                }

                @Override
                protected void onPostExecute(ReadTertulia tertulia) {
                    paintUi(tertulia);
                }
            }.execute(result);
        }
    }

    private void paintUi(ReadTertulia tertulia) {
        titleView.setText(tertulia.name);
        subjectView.setText(tertulia.subject);
        locationView.setText(tertulia.location.toString());
        Schedule schedule = tertulia.getSchedule();
        scheduleView.setText(schedule == null ? "" : schedule.toString());
    }

}
