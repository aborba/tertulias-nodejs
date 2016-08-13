/*
 * Copyright (c) 2016 António Borba da Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package pt.isel.s1516v.ps.apiaccess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import java.util.Set;

import pt.isel.s1516v.ps.apiaccess.flow.GetData;
import pt.isel.s1516v.ps.apiaccess.flow.GetHomeCallback;
import pt.isel.s1516v.ps.apiaccess.flow.GetMeCallback;
import pt.isel.s1516v.ps.apiaccess.flow.GetTertuliasCallback;
import pt.isel.s1516v.ps.apiaccess.flow.LoginCallback;
import pt.isel.s1516v.ps.apiaccess.flow.PostRegisterCallback;
import pt.isel.s1516v.ps.apiaccess.helpers.LoginStatus;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaListItem;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.NewTertuliaActivity;
import pt.isel.s1516v.ps.apiaccess.tertuliadetails.TertuliaDetailsActivity;
import pt.isel.s1516v.ps.apiaccess.tertuliasubscription.SearchPublicTertuliaActivity;
import pt.isel.s1516v.ps.apiaccess.ui.DrawerManager;
import pt.isel.s1516v.ps.apiaccess.ui.MaUiManager;

public class MainActivity extends Activity implements TertuliasApi {

    public static ApiLinks apiHome = new ApiLinks(null);
    public static ApiLinks apiLinks = new ApiLinks(null);

    private final static String INSTANCE_KEY_TERTULIA = "tertulias";
    private final static String API_ROOT_END_POINT = "/";

    public static final String SHARED_PREFS_FILE = "access";
    public static final String USERID_PREF = "userid";
    public static final String TOKEN_PREF = "token";
    public static final String TERTULIAS_PREF = "tertulias";

    public static TertuliaListItem[] tertulias;
    private LoginStatus loginStatus = null;

    public static MaUiManager uiManager = null;

//  region Activity lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiManager = new MaUiManager(this);

        DrawerManager drawerManager = new DrawerManager(this,
                uiManager.uiResources.get(MaUiManager.UIRESOURCE.DRAWER_LAYOUT),
                uiManager.uiResources.get(MaUiManager.UIRESOURCE.DRAWER_MENU_LIST),
                uiManager.uiResources.get(MaUiManager.UIRESOURCE.USER_PICTURE));

        drawerManager.prepareMenu(R.array.main_activity_drawer_list_items, new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Login
                        doLoginAndFetch(MainActivity.this, uiManager);
                        uiManager.getDrawerManager().close();
                        break;
                    case 1: // Logout
                        doLogout(MainActivity.this);
                        break;
                    case 2: // Refresh
                        requestTertuliasList(MainActivity.this);
                        uiManager.getDrawerManager().close();
                        break;
                    default:
                }
            }
        });

        uiManager.setDrawerManager(drawerManager);
        uiManager.swapAdapter(new TertuliasArrayAdapter(this, tertulias != null ? tertulias : new TertuliaListItem[0]));

        setupToolbar();

        if (!Util.isSignedIn(this))
            uiManager.setUserPicture(R.mipmap.tertulias);

        if (!Util.isConnectivityAvailable(this)) {
            Util.alert(this, R.string.main_activity_no_network_title, R.string.main_activity_no_network);
            return;
        }

        loadInstanceState(savedInstanceState);

        doLoginAndFetch(this, uiManager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NewTertuliaActivity.ACTIVITY_REQUEST_CODE:
            case SearchPublicTertuliaActivity.ACTIVITY_REQUEST_CODE:
            case TertuliaDetailsActivity.ACTIVITY_REQUEST_CODE:
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
        cacheUserToken(Util.getMobileServiceClient(this).getCurrentUser());
        cacheTertulias(tertulias);
        outState.putParcelableArray(INSTANCE_KEY_TERTULIA, tertulias);
    }

    private void loadInstanceState(Bundle inState) {
        if (inState == null || !inState.containsKey(INSTANCE_KEY_TERTULIA))
            return;
        Parcelable[] parcelables = inState.getParcelableArray(INSTANCE_KEY_TERTULIA);
        if (parcelables == null) {
            tertulias = new TertuliaListItem[0];
            return;
        }
        tertulias = new TertuliaListItem[parcelables.length];
        for (int i = 0; i < parcelables.length; i++)
            tertulias[i] = (TertuliaListItem) parcelables[i];
    }

//  endregion

//    region Click Handlers

    public void onClickNewTertulia(final View view) {
        if (apiLinks == null || apiLinks.isEmpty()) {
            Util.longSnack(findViewById(android.R.id.content), R.string.main_activity_routes_undefined);
            return;
        }
        Intent intent = new Intent(this, NewTertuliaActivity.class);
        intent.putExtra(NewTertuliaActivity.INTENT_LINKS, apiLinks);
//        intent.putExtra(NewTertuliaActivity.ROUTE_END_POINT_LABEL, apiLinks.getRoute(LINK_CREATE));
//        intent.putExtra(NewTertuliaActivity.ROUTE_METHOD_LABEL, apiLinks.getMethod(LINK_CREATE));
        intent.putExtra(NewTertuliaActivity.INTENT_TERTULIAS, getTrimmedLowerCaseNames(tertulias));
        startActivityForResult(intent, NewTertuliaActivity.ACTIVITY_REQUEST_CODE);
    }

    public void onClickSearchTertulia(final View view) {
        if (apiLinks == null || apiLinks.isEmpty()) {
            Util.longSnack(findViewById(android.R.id.content), R.string.main_activity_routes_undefined);
            return;
        }
        Intent intent = new Intent(this, SearchPublicTertuliaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(SearchPublicTertuliaActivity.INTENT_LINKS, apiLinks);
        startActivityForResult(intent, SearchPublicTertuliaActivity.ACTIVITY_REQUEST_CODE);
    }

//    endregion

//     region Private Methods

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.mtl_toolbar);
        Util.setupToolBar(toolbar,
                R.string.title_activity_list_tertulias,
                R.string.title_activity_list_tertulias,
                Util.IGNORE, // R.menu.activity_main_menu,
                null
        );
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( ! uiManager.getDrawerManager().isOpen())
                    uiManager.getDrawerManager().open();
            }
        });
    }

    private void updateStatusAlert(Boolean isLogin, int alertMessage) {
        if (loginStatus != null)
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
        uiManager.showProgressBar();
        request(ctx, apiHome.getRoute("tertulias"), apiHome.getMethod("tertulias"),
                new GetTertuliasCallback(ctx, uiManager, null, null));
    }

    //    region User session management

    private void requestLogin(final Context ctx, final FutureCallback<MobileServiceUser> callback) {
        final MobileServiceClient cli = Util.getMobileServiceClient(this);
        if (Util.isTokenValid(Util.getAuthenticationToken(cli), 15000) || cli.isLoginInProgress()) {
            callback.onSuccess(cli.getCurrentUser());
            return;
        }
        Util.lockOrientation(this);
        Util.logd("Login in");
        Futures.addCallback(
                cli.login(MobileServiceAuthenticationProvider.Google),
                new FutureCallback<MobileServiceUser>() {
                    @Override
                    public void onSuccess(MobileServiceUser user) {
                        Util.logd("Login in ok");
                        Util.unlockOrientation(MainActivity.this);
                        Util.logd("Proceeding on next callback");
                        callback.onSuccess(user);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Util.unlockOrientation(MainActivity.this);
                        callback.onFailure(t);
                    }
                }
        );
    }

    private void doLoginAndFetch(Context ctx, MaUiManager uiManager) {
        Util.logd("Login and fetch content");
        uiManager.showProgressBar();
        GetData<JsonElement> getTertulias = new GetData<>(ctx, "tertulias", apiHome);
        GetTertuliasCallback getTertuliasCallback = new GetTertuliasCallback(ctx, uiManager, null, null);

        GetData<JsonElement> getMe = new GetData<>(ctx, "me", apiHome);
        GetMeCallback getMeCallback = new GetMeCallback(ctx, null, getTertulias, getTertuliasCallback, uiManager);

        GetData<JsonElement> postRegister = new GetData<>(ctx, "registration", apiHome);
        PostRegisterCallback postRegisterCallback = new PostRegisterCallback(ctx, null, getMe, getMeCallback);

        GetData<JsonElement> getHome = new GetData<>(ctx, API_ROOT_END_POINT, null);
        GetHomeCallback getHomeCallback = new GetHomeCallback(ctx, null, postRegister, postRegisterCallback);

        LoginCallback loginCallback = new LoginCallback(ctx, null, getHome, getHomeCallback);

        MobileServiceClient cli = Util.getMobileServiceClient(this);
        if (cli == null || ! loadCachedUserToken(cli)) {
            Util.logd("Null cli or no token.");
            requestLogin(ctx, loginCallback);
        }
        else
            loginCallback.onSuccess(cli.getCurrentUser());
    }

    private void doLogout(final Context ctx) {
        Futures.addCallback(
                Util.getMobileServiceClient(this).logout(),
                new FutureCallback<MobileServiceUser>() {

                    @Override
                    public void onSuccess(MobileServiceUser user) {
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.removeAllCookie();
                        clearCacheUserToken();
                        Util.getMobileServiceClient(MainActivity.this).setCurrentUser(null);
                        clearCacheTertulias();
                        tertulias = new TertuliaListItem[0];

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                uiManager.getDrawerManager().resetIcon();
                                uiManager.swapAdapter(new TertuliasArrayAdapter(MainActivity.this, tertulias != null ? tertulias : new TertuliaListItem[0]));
                            }
                        };
                        Looper.prepare();
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(runnable);

                        if (loginStatus != null)
                            loginStatus.reset(R.string.main_activity_logout_succeed_message);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        updateStatusAlert(LoginStatus.SIGNED_IN, R.string.main_activity_logout_failed_message);
                    }
                }
        );
    }

    //    endregion

    private void cacheTertulias(TertuliaListItem[] tertulias)
    {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String tertuliasJson = tertulias == null ? null : new Gson().toJson(tertulias);
        editor.putString(TERTULIAS_PREF, tertuliasJson);
        editor.commit();
    }

    private void clearCacheTertulias()
    {
        cacheTertulias(null);
    }

    private TertuliaListItem[] loadCachedTertulias()
    {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String tertuliasJson = prefs.getString(TERTULIAS_PREF, null);
        if (tertuliasJson == null)
            return null;
        TertuliaListItem[] tertulias = new Gson().fromJson(tertuliasJson, TertuliaListItem[].class);
        return tertulias;
    }

    private void cacheUserToken(String user, String token)
    {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USERID_PREF, user);
        editor.putString(TOKEN_PREF, token);
        editor.commit();
    }

    private void cacheUserToken(MobileServiceUser user)
    {
        cacheUserToken(user.getUserId(), user.getAuthenticationToken());
    }

    private void clearCacheUserToken()
    {
        cacheUserToken(null, null);
    }

    private boolean loadCachedUserToken(MobileServiceClient client)
    {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String userId = prefs.getString(USERID_PREF, null);
        if (userId == null)
            return false;
        String token = prefs.getString(TOKEN_PREF, null);
        if (token == null)
            return false;
        if (! Util.isTokenValid(token, 1000))
            return false;

        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        client.setCurrentUser(user);

        return true;
    }

//    endregion

//  region private static methods

    private static String[] getTrimmedLowerCaseNames(TertuliaListItem[] tertulias) {
        String[] names = new String[tertulias.length];
        for (int i = 0; i < tertulias.length; i++)
            names[i] = tertulias[i].name.toLowerCase().trim();
        return names;
    }

//  endregion
}
