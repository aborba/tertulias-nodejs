package pt.isel.s1516v.ps.apiaccess.tertuliadetails;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import pt.isel.s1516v.ps.apiaccess.support.domain.Tertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertulia;

public class TertuliaDetailsActivity extends Activity implements TertuliasApi {

    public final static int REQUEST_CODE = TERTULIA_DETAILS_RETURN_CODE;
    public final static String SELF_LINK = "SELF_LINK";
    private final static String TERTULIA = "tertulia";
    private TextView titleView, subjectView, roleView, locationView, addressView, zipView, cityView, countryView, latitudeView, longitudeView, scheduleView;
    private CheckBox isPrivateView;
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
                Util.IGNORE, Util.IGNORE, null, true);

        ApiLink selfLink = getIntent().getParcelableExtra(SELF_LINK);
        titleView = (TextView) findViewById(R.id.tda_Title);
        subjectView = (TextView) findViewById(R.id.tda_Subject);
        roleView = (TextView) findViewById(R.id.tda_Role);
        locationView = (TextView) findViewById(R.id.tda_LocationName);
        addressView = (TextView) findViewById(R.id.tda_Address);
        zipView = (TextView) findViewById(R.id.tda_Zip);
        cityView = (TextView) findViewById(R.id.tda_City);
        countryView = (TextView) findViewById(R.id.tda_Country);
        latitudeView = (TextView) findViewById(R.id.tda_Latitude);
        longitudeView = (TextView) findViewById(R.id.tda_Longitude);
        scheduleView = (TextView) findViewById(R.id.tda_Schedule);
        isPrivateView = (CheckBox) findViewById(R.id.tda_IsPrivate);
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

    public void onClickMapLookup(View view) {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            String latitude = latitudeView.getText().toString();
            String longitude = longitudeView.getText().toString();
            if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)) {
                Util.longSnack(view, R.string.tertulia_details_undefined_coordinates);
                return;
            }
            LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            builder.setLatLngBounds(new LatLngBounds(latLng, latLng));
            startActivity(builder.build(this));
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
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

    public void onClickUnsubscribe(View view) {
        if (tertulia.role_type.toLowerCase().equals("owner")) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_dialog_unsubscribe_public_tertulia)
                    .setMessage(R.string.message_dialog_unsubscribe_public_tertulia_owner_warning)
                    .setIcon(android.R.drawable.ic_lock_lock)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_dialog_unsubscribe_public_tertulia)
                .setMessage(R.string.message_dialog_unsubscribe_public_tertulia)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        for (ApiLink apiLink : tertulia.links) {
                            if (apiLink.rel.equals(LINK_UNSUBSCRIBE)) {
                                String apiEndPoint = apiLink.href;
                                String apiMethod = apiLink.method;
                                Futures.addCallback(Util.getMobileServiceClient(TertuliaDetailsActivity.this)
                                                .invokeApi(apiEndPoint, null, apiMethod, null)
                                        , new UnsubscriptionCallback(findViewById(android.R.id.content)));
                                return;
                            }
                        }
                        Util.longSnack(rootView, "Oops! Unsubscribe link not found."); // TODO: strings.xml
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public class UnsubscriptionCallback implements FutureCallback<JsonElement> {

        final View rootView;

        public UnsubscriptionCallback(View rootView) {
            this.rootView = rootView;
        }

        @Override
        public void onSuccess(JsonElement result) {
            Util.longSnack(rootView, "Unsubscribed from Tertulia"); // TODO: strings.xml
            setResult(RESULT_SUCCESS);
            finish();
        }

        @Override
        public void onFailure(Throwable e) {
            Util.longSnack(rootView, e.getMessage());
            Util.logd("Public tertulia unsubscription failed");
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
            Context ctx = TertuliaDetailsActivity.this;
            Util.longSnack(rootView, getEMsg(ctx, e.getMessage()));
        }

        @Override
        public void onSuccess(JsonElement result) {
            new AsyncTask<JsonElement, Void, Tertulia>() {
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
            if (!TextUtils.isEmpty(tertulia.scheduleDescription))
                scheduleText += " - " + tertulia.scheduleDescription;
        } else scheduleText = tertulia.scheduleDescription;
        scheduleView.setText(scheduleText);
        roleView.setText(tertulia.role_type);
        isPrivateView.setChecked(tertulia.isPrivate);
    }

}
