package pt.isel.s1516v.ps.apiaccess;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.common.base.Predicate;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import java.util.HashMap;
import java.util.LinkedList;

import pt.isel.s1516v.ps.apiaccess.helpers.Error;
import pt.isel.s1516v.ps.apiaccess.helpers.LoginStatus;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Tertulia;
import pt.isel.s1516v.ps.apiaccess.support.raw.RHome;
import pt.isel.s1516v.ps.apiaccess.support.raw.RTertulia;

public class MainActivity extends AppCompatActivity implements TertuliasApi {

    public static RHome rHome;
    public static HashMap<String, String> baseRoutes = new HashMap<>();

    private final static String SAVED_TERTULIAS = "tertulias";
    private final static String SAVED_BASE_ROUTES = "baseRoutes";
    private final static String API_HOME_END_POINT = "home";

    private ListView listView;
    private ArrayAdapter<Tertulia> listViewAdapter;
    private Tertulia[] tertulias;
    private MobileServiceUser mUser;
    private LoginStatus loginStatus = null;

//    region Activity lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) loadInstanceState(savedInstanceState);

        Util.setupActionBar(this, R.string.title_activity_list_tertulias, false);

        setupViews();

        loginStatus = new LoginStatus(
                this,
                (ImageView) findViewById(R.id.mtl_authenticatedView),
                new Predicate() {
                    @Override
                    public boolean apply(Object input) {
                        return Util.getMobileServiceClient(MainActivity.this).getCurrentUser() == null;
                    }
                }
        );

        doLoginAndFetch(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NewTertuliaActivity.REQUEST_CODE:
                if (resultCode == RESULT_FAIL) return;
                requestTertuliasList(this);
                break;
            default:
                break;
        }
    }

//    endregion

//  region Instance State management

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(SAVED_TERTULIAS, tertulias);
        outState.putSerializable(SAVED_BASE_ROUTES, baseRoutes);
    }

    private void loadInstanceState(Bundle inState) {
        if (inState.containsKey(SAVED_TERTULIAS))
            tertulias = (Tertulia[]) inState.getParcelableArray(SAVED_TERTULIAS);
        if (inState.containsKey(SAVED_BASE_ROUTES))
            baseRoutes = (HashMap<String, String>) inState.getSerializable(SAVED_BASE_ROUTES);
    }

//  endregion

//    region Click Handlers

    public void onClickNewTertulia(final View view) {
        if (baseRoutes.containsKey(POST_TERTULIAS)) {
            Intent intent = new Intent(this, NewTertuliaActivity.class);
            intent.putExtra(NewTertuliaActivity.END_POINT_LABEL, baseRoutes.get(POST_TERTULIAS));
            intent.putExtra(NewTertuliaActivity.MY_TERTULIAS, tertulias);
            startActivityForResult(intent, NewTertuliaActivity.REQUEST_CODE);
        }
    }

    public void onClickSearchTertulia(final View view) {
        Intent intent = new Intent(this, SubscribeTeruliaActivity.class);
        intent.putExtra(NewTertuliaActivity.END_POINT_LABEL, API_HOME_END_POINT);
        startActivityForResult(intent, NewTertuliaActivity.REQUEST_CODE);
    }

    public void onClickLogin(final View view) {
        doLoginAndFetch(this);
    }

    public void onClickLogout(final View view) {
        doLogout(this);
    }

    public void onClickRefresh(final View view) {
        requestTertuliasList(this);
    }

//    endregion

//     region Private Classes

    private class PrivateListViewItemClickListener implements AdapterView.OnItemClickListener {
        final View view;

        public PrivateListViewItemClickListener(View view) {
            this.view = view;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TertuliasArrayAdapter.ViewHolder viewHolder = (TertuliasArrayAdapter.ViewHolder) view.getTag();
            HashMap<String, HashMap<String, String>> links = viewHolder.getLinks();
            if (links == null || !links.containsKey("details") || !links.get("details").containsKey("href")) {
                Util.longSnack(view, R.string.activity_list_tertulias_toast_no_details);
                return;
            }
            Intent intent = new Intent(MainActivity.this, TertuliaDetailsActivity.class);
            intent.putExtra(TertuliaDetailsActivity.SELF_LINK, links.get("details").get("href"));
            startActivity(intent);
        }
    }

    private class TertuliasListCallback implements FutureCallback<JsonElement> {
        @Override
        public void onFailure(Throwable e) {
            Context ctx = MainActivity.this;
            Util.longToast(ctx, getEMsg(ctx, e.getMessage()));
        }

        @Override
        public void onSuccess(JsonElement result) {
            new AsyncTask<JsonElement, Void, Tertulia[]>() {
                @Override
                protected Tertulia[] doInBackground(JsonElement... params) {
                    RTertulia[] rtertulias = new Gson().fromJson(params[0], RTertulia[].class);
                    LinkedList<Tertulia> tertulias = new LinkedList<>();
                    for (RTertulia rtertulia : rtertulias) {
                        Tertulia tertulia = new Tertulia(rtertulia);
                        tertulias.add(tertulia);
                    }
                    return tertulias.toArray(new Tertulia[tertulias.size()]);
                }

                @Override
                protected void onPostExecute(Tertulia[] tertulias) {
                    MainActivity.this.tertulias = tertulias;
                    ArrayAdapter<Tertulia> adapter = new TertuliasArrayAdapter(MainActivity.this, tertulias);
                    listView.setAdapter(adapter);
                }
            }.execute(result);
        }
    }

//     endregion

//     region Private Methods

    private void setupViews() {
        listView = (ListView) findViewById(R.id.mtl_ListView);
        listViewAdapter = new TertuliasArrayAdapter(this, tertulias != null ? tertulias : new Tertulia[0]);
        listView.setOnItemClickListener(new PrivateListViewItemClickListener(listView));
        listView.setAdapter(listViewAdapter);
    }

    private static String getEMsg(Context ctx, String msg) {
        if (!Util.isJson(msg)) return msg;
        Error error = new Gson().fromJson(msg, Error.class);
        return error.getStatusCodeMessage(ctx);
    }

    private void updateStatusAlert(Boolean isLogin, int alertMessage) {
        loginStatus.set(isLogin);
        Util.longSnack(findViewById(android.R.id.content), alertMessage);
    }

    private void request(Context ctx, String key, String httpMethod, FutureCallback<JsonElement> callback) {
        MobileServiceClient cli = Util.getMobileServiceClient(ctx);
        if (!baseRoutes.containsKey(key)) {
            Util.longToast(ctx, R.string.main_activity_server_error_exiting);
            finish();
        }
        ListenableFuture<JsonElement> future = cli.invokeApi(baseRoutes.get(key), null, httpMethod, null);
        if (callback != null) Futures.addCallback(future, callback);
    }

    private void requestTertuliasList(Context ctx) {
        request(ctx, GET_TERTULIAS, HTTP_GET, new TertuliasListCallback());
    }

//    region User session management

    private void doLoginAndFetch(final Context ctx) {
        final MobileServiceClient cli = Util.getMobileServiceClient(this);
        loginStatus.update();

        if (Util.isSignedIn(ctx) || cli.isLoginInProgress()) {
            requestTertuliasList(MainActivity.this);
            return;
        }

        Futures.addCallback(

                cli.login(MobileServiceAuthenticationProvider.Google),

                new FutureCallback<MobileServiceUser>() {

                    @Override
                    public void onFailure(Throwable e) {
                        mUser = null;
                        loginStatus.reset();
                        updateStatusAlert(false, R.string.main_activity_login_failed_message);
                    }

                    @Override
                    public void onSuccess(MobileServiceUser user) {
                        mUser = user;

                        MobileServiceClient cli = Util.getMobileServiceClient(MainActivity.this);
                        ListenableFuture<JsonElement> future = cli.invokeApi(API_HOME_END_POINT, null, HTTP_GET, null);
                        Futures.addCallback(future, new FutureCallback<JsonElement>() {
                            @Override
                            public void onFailure(Throwable e) {
                                mUser = null;
                                loginStatus.reset();
                                updateStatusAlert(false, R.string.main_activity_login_failed_message);
                            }

                            @Override
                            public void onSuccess(JsonElement result) {
                                rHome = new Gson().fromJson(result, RHome.class);
                                if (rHome == null) {
                                    Util.longToast(MainActivity.this, R.string.main_activity_server_error_exiting);
                                    finish();
                                }
                                rHome.exposeMap(baseRoutes);
                                request(MainActivity.this, POST_REGISTRATION, HTTP_POST, null); // User registration
                                requestTertuliasList(MainActivity.this);
                            }
                        });

                        loginStatus.set(R.string.main_activity_login_succeed_message);
                    }
                }
        );
    }

    private void doLogout(Context ctx) {

        loginStatus.update();

        Futures.addCallback(

                Util.getMobileServiceClient(this).logout(),

                new FutureCallback<MobileServiceUser>() {
                    @Override
                    public void onFailure(Throwable e) {
                        updateStatusAlert(LoginStatus.SIGNED_IN, R.string.main_activity_logout_failed_message);
                    }

                    @Override
                    public void onSuccess(MobileServiceUser user) {
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.removeAllCookie();
                        mUser = null;
                        tertulias = new Tertulia[0];

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter<Tertulia> adapter = new TertuliasArrayAdapter(MainActivity.this, tertulias);
                                listView.setAdapter(adapter);
                            }
                        };
                        Looper.prepare();
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(runnable);

                        loginStatus.reset(R.string.main_activity_logout_succeed_message);
                    }
                }
        );
    }

//    endregion

//    endregion
}
