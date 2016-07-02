package pt.isel.s1516v.ps.apiaccess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import pt.isel.s1516v.ps.apiaccess.flow.GetHome;
import pt.isel.s1516v.ps.apiaccess.flow.GetHomeCallback;
import pt.isel.s1516v.ps.apiaccess.flow.GetData;
import pt.isel.s1516v.ps.apiaccess.flow.GetTertuliasCallback;
import pt.isel.s1516v.ps.apiaccess.flow.LoginCallback;
import pt.isel.s1516v.ps.apiaccess.flow.PostRegister;
import pt.isel.s1516v.ps.apiaccess.flow.PostRegisterCallback;
import pt.isel.s1516v.ps.apiaccess.helpers.LoginStatus;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Tertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;

public class MainActivity extends Activity implements TertuliasApi {

    public static ApiLinks apiHome = new ApiLinks(null);
    public static ApiLinks apiLinks = null;

    private final static String SAVED_TERTULIAS = "tertulias";
    private final static String API_ROOT_END_POINT = "/";

    private RecyclerView recyclerView;
    private TertuliasArrayRvAdapter listViewAdapter;
    public static Tertulia[] tertulias;
    private MobileServiceUser mUser;
    private LoginStatus loginStatus = null;

//    region Activity lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!Util.isConnectivityAvailable(this)) {
            Util.alert(this, R.string.main_activity_no_network_title, R.string.main_activity_no_network);
            return;
        }
        setupViews();
        if (savedInstanceState != null) loadInstanceState(savedInstanceState);
        doLoginAndFetch(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.amm_login:
                doLoginAndFetch(this);
                return true;
            case R.id.amm_logout:
                doLogout(this);
                return true;
            case R.id.amm_reload:
                requestTertuliasList(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NewTertuliaActivity.INTENT_REQUEST_CODE:
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
    }

    private void loadInstanceState(Bundle inState) {
        if (inState.containsKey(SAVED_TERTULIAS))
            tertulias = (Tertulia[]) inState.getParcelableArray(SAVED_TERTULIAS);
    }

//  endregion

//    region Click Handlers

    public void onClickNewTertulia(final View view) {

        if (apiLinks == null) {
            Util.longSnack(findViewById(android.R.id.content), R.string.main_activity_routes_undefined);
            return;
        }
        Intent intent = new Intent(this, NewTertuliaActivity.class);
        intent.putExtra(NewTertuliaActivity.ROUTE_END_POINT_LABEL, apiLinks.getRoute(LINK_CREATE));
        intent.putExtra(NewTertuliaActivity.ROUTE_METHOD_LABEL, apiLinks.getMethod(LINK_CREATE));
        intent.putExtra(NewTertuliaActivity.MY_TERTULIAS, tertulias);
        startActivityForResult(intent, NewTertuliaActivity.INTENT_REQUEST_CODE);
    }

    public void onClickSearchTertulia(final View view) {
        /*
        Intent intent = new Intent(this, SubscribeTeruliaActivity.class);
        intent.putExtra(NewTertuliaActivity.ROUTE_END_POINT_LABEL, API_HOME_END_POINT);
        startActivityForResult(intent, NewTertuliaActivity.INTENT_REQUEST_CODE);
        */
    }

//    endregion

//     region Private Methods

    private void setupViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.mtl_toolbar);
        Util.setupToolBar(toolbar,
                R.string.title_activity_list_tertulias,
                R.string.title_activity_list_tertulias,
                R.menu.activity_main_menu);
        recyclerView = (RecyclerView) findViewById(R.id.mtl_RecyclerView);
        listViewAdapter = new TertuliasArrayRvAdapter(this, tertulias != null ? tertulias : new Tertulia[0]);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
    }

    private void updateStatusAlert(Boolean isLogin, int alertMessage) {
        loginStatus.set(isLogin);
        Util.longSnack(findViewById(android.R.id.content), alertMessage);
    }

    private void request(Context ctx, String route, String httpMethod, FutureCallback<JsonElement> callback) {
        if (route == null || httpMethod == null) {
            Util.longSnack(((Activity) ctx).getWindow().getDecorView().findViewById(android.R.id.content),
                    R.string.main_activity_server_error_exiting);
            finish();
        }
        if (!Util.isConnectivityAvailable(this)) {
            Util.alert(this, R.string.main_activity_no_network_title, R.string.main_activity_no_network);
            return;
        }

        MobileServiceClient cli = Util.getMobileServiceClient(ctx);
        ListenableFuture<JsonElement> future = cli.invokeApi(route, null, httpMethod, null);
        if (callback != null) Futures.addCallback(future, callback);
    }

    private void requestTertuliasList(Context ctx) {
        if (apiHome == null) {
            Util.longSnack(findViewById(android.R.id.content), R.string.main_activity_routes_undefined);
            return;
        }
        request(ctx, apiHome.getRoute("tertulias"), apiHome.getMethod("tertulias"), new GetTertuliasCallback(ctx, recyclerView, null, null));
    }

    //    region User session management

    private void requestLogin(final Context ctx, FutureCallback<MobileServiceUser> callback) {
        final MobileServiceClient cli = Util.getMobileServiceClient(this);
        if (Util.isSignedIn(ctx) || cli.isLoginInProgress()) {
            callback.onSuccess(cli.getCurrentUser());
            return;
        }
        Futures.addCallback(
                cli.login(MobileServiceAuthenticationProvider.Google),
                callback
        );
    }

    private void doLoginAndFetch(final Context ctx) {
        GetData<JsonElement> getTertulias = new GetData<>(ctx, "tertulias", apiHome);
        GetTertuliasCallback getTertuliasCallback = new GetTertuliasCallback(ctx, recyclerView, null, null);

        GetData<JsonElement> postRegister = new GetData<>(ctx, "registration", apiHome);
        PostRegisterCallback postRegisterCallback = new PostRegisterCallback(ctx, null, getTertulias, getTertuliasCallback);

        GetData<JsonElement> getHome = new GetData<>(ctx, API_ROOT_END_POINT, null);
        GetHomeCallback getHomeCallback = new GetHomeCallback(ctx, null, postRegister, postRegisterCallback);

        LoginCallback loginCallback = new LoginCallback(ctx, null, getHome, getHomeCallback);
        requestLogin(ctx, loginCallback);
    }

    private void doLogout(Context ctx) {
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
                                listViewAdapter = new TertuliasArrayRvAdapter(MainActivity.this, tertulias != null ? tertulias : new Tertulia[0]);
                                recyclerView.setAdapter(listViewAdapter);
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
