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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import java.util.HashMap;

import pt.isel.s1516v.ps.apiaccess.about.AboutActivity;
import pt.isel.s1516v.ps.apiaccess.contentprovider.TertuliasContract;
import pt.isel.s1516v.ps.apiaccess.flow.AuthorizationCallback;
import pt.isel.s1516v.ps.apiaccess.flow.GetData;
import pt.isel.s1516v.ps.apiaccess.flow.GetHomeCallback;
import pt.isel.s1516v.ps.apiaccess.flow.GetMeCallback;
import pt.isel.s1516v.ps.apiaccess.flow.GetTertuliasCallback;
import pt.isel.s1516v.ps.apiaccess.flow.PostRegisterCallback;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.notifications.NotificationsToken;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaListItem;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.syncadapter.TertuliasTableObserver;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.NewTertuliaActivity;
import pt.isel.s1516v.ps.apiaccess.tertuliadetails.TertuliaDetailsActivity;
import pt.isel.s1516v.ps.apiaccess.tertuliasubscription.SearchPublicTertuliaActivity;
import pt.isel.s1516v.ps.apiaccess.ui.DrawerManager;
import pt.isel.s1516v.ps.apiaccess.ui.MaUiManager;

public class MainActivity extends Activity implements TertuliasApi {

    public static final String SHARED_PREFS_FILE = "tertulias_main";
    public static Me me;

    public final static long TOKEN_EXPIRATION_GUARD = 15000;
    public static ApiLinks apiHome = new ApiLinks(null);
    public static ApiLinks apiLinks = new ApiLinks(null);

    private final static String INSTANCE_KEY_TERTULIA = "tertulias";
    public final static String API_ROOT_END_POINT = "/";

    public static final String TERTULIAS_PREF = "tertulias";

    public static TertuliaListItem[] tertulias;
    public static MaUiManager uiManager = null;

    public static Account account;

//  region Activity lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Util.getRootView(this);
        loadInstanceState(savedInstanceState);

        uiManager = new MaUiManager(this);

        DrawerManager drawerManager = new DrawerManager(this,
                uiManager.getResource(MaUiManager.UIRESOURCE.DRAWER_LAYOUT),
                uiManager.getResource(MaUiManager.UIRESOURCE.DRAWER_MENU_LIST),
                uiManager.getResource(MaUiManager.UIRESOURCE.USER_PICTURE));

        drawerManager.prepareMenu(R.array.main_activity_drawer_list_items, new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Login
                        doLoginAndFetch(MainActivity.this, uiManager);
                        uiManager.getDrawerManager().close();
                        break;
                    case 1: // Logout
                        doLogout(MainActivity.this);
                        uiManager.getDrawerManager().open();
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

        if (Util.isSignedIn(this))
            uiManager.setLoggedIn(R.id.ma_userImage);

        if (!Util.isConnectivityAvailable(this)) {
            Util.alert(this, R.string.main_activity_no_network_title, R.string.main_activity_no_network);
            return;
        }

        DrawerLayout drawerLayout = (DrawerLayout) uiManager.getView(MaUiManager.UIRESOURCE.DRAWER_LAYOUT);
        TextView aboutView = (TextView) drawerLayout.findViewById(R.id.ma_about);
        aboutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });

        ContentResolver contentResolver = getContentResolver();
        Uri uri = new Uri.Builder()
                .scheme(TertuliasContract.Notifications.RESOURCE)
                .authority(TertuliasContract.AUTHORITY)
                .path(TertuliasContract.Notifications.RESOURCE)
                .build();
        TertuliasTableObserver tableObserver = new TertuliasTableObserver(null);
        contentResolver.registerContentObserver(uri, true, tableObserver);

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
                NotificationsToken.regenerate(this);
                break;
            default:
                break;
        }
    }

//    endregion

    public static final String ACCOUNT_TYPE = "pt.isel.s1516v.ps.apiaccess";
    public static final String ACCOUNT = "Tertulias";

    public static Account CreateSyncAccount(Context ctx) {
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) ctx.getSystemService(ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

//  region Instance State management

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Util.cacheCredentials(this, Util.getMobileServiceClient(this).getCurrentUser());
//        cacheTertulias(tertulias);
        outState.putParcelableArray(INSTANCE_KEY_TERTULIA, tertulias);
    }

    private void loadInstanceState(Bundle inState) {
        if (inState == null || ! inState.containsKey(INSTANCE_KEY_TERTULIA))
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
        intent.putExtra(NewTertuliaActivity.INTENT_TERTULIAS, Util.getTrimmedLowerCaseNames(tertulias));
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.ma_toolbar);
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
                if (!uiManager.getDrawerManager().isOpen())
                    uiManager.getDrawerManager().open();
            }
        });
    }

    private void updateStatusAlert(int alertMessage) {
        Util.longSnack(uiManager.getRootView(), alertMessage);
    }

    private void request(Context ctx, String route, String httpMethod, FutureCallback<JsonElement> callback) {
        if (route == null || httpMethod == null) {
            Util.longSnack(Util.getRootView(this),
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
                new GetTertuliasCallback(ctx, null, null, uiManager, false));
    }

    //    region User session management

    private void getAuthorization(final Context ctx, final FutureCallback<MobileServiceUser> authorizationCallback) {
        final MobileServiceClient cli = Util.getMobileServiceClient(ctx);

        Util.loadCachedCredentials(ctx,
            new FutureCallback2<Context, MobileServiceUser>() {

                @Override
                public void onSuccess(final Context ctx, MobileServiceUser user) {
                    if (user != null) {
                        String token = user.getAuthenticationToken();
                        boolean isTokenValid = Util.isTokenValid(token, 0);
                        if ( ! isTokenValid) {
                            if (! Util.isLoginRequired(ctx)) {
                                String rel = apiHome == null || apiHome.isEmpty() ? API_ROOT_END_POINT : null;
                                GetData<JsonElement> getHome = new GetData<>(ctx, rel, null, uiManager);
                                Util.refreshToken(ctx, uiManager.getRootView(), getHome, prepareMainFlow(ctx));
                                return;
                            }
                        } else {
                            Util.logd("Valid user: login bypass");
                            authorizationCallback.onSuccess(user);
                            return;
                        }
                    }

                    Util.lockOrientation(ctx);
                    Util.logd("Login in");
                    HashMap<String, String> parameters = new HashMap<String, String>();
                    parameters.put("access_type", "offline");
                    Futures.addCallback(
                        cli.login(MobileServiceAuthenticationProvider.Google, parameters),
                        new FutureCallback<MobileServiceUser>() {

                            @Override
                            public void onSuccess(MobileServiceUser user) {
                                Util.logd("Login in ok");
                                Util.unlockOrientation(MainActivity.this);
                                Util.cacheCredentials(ctx, user);
                                if (authorizationCallback != null) {
                                    Util.logd("Proceeding on next callback");
                                    authorizationCallback.onSuccess(user);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Util.unlockOrientation(MainActivity.this);
                                if (authorizationCallback != null) {
                                    Util.logd("Proceeding failure on next callback");
                                    authorizationCallback.onFailure(t);
                                }
                            }
                        }
                    );
                }

                @Override
                public void onFailure(Throwable t) {
                    updateStatusAlert(R.string.main_activity_logout_failed_message);
                    if (authorizationCallback != null) {
                        Util.logd("Proceeding failure on next callback");
                        authorizationCallback.onFailure(t);
                    }
                }
            }
        );
    }

    private void doLoginAndFetch(Context ctx, MaUiManager uiManager) {
        Util.logd("Login and fetch content");

        uiManager.showProgressBar();

        account = CreateSyncAccount(this);
        String authority = TertuliasContract.AUTHORITY;
        ContentResolver.setMasterSyncAutomatically(true);
        ContentResolver.setSyncAutomatically(account, authority, true);

        tertulias = loadCachedTertulias();

        AuthorizationCallback authorizationCallback = prepareMainLoginFlow(ctx);
        getAuthorization(ctx, authorizationCallback);

        if (tertulias != null) {
            TertuliasArrayAdapter arrayAdapter = new TertuliasArrayAdapter((Activity) ctx, tertulias);
            uiManager.swapAdapter(arrayAdapter);
        }

    }

    private static AuthorizationCallback prepareMainLoginFlow(Context ctx) {
        Util.logd("Prepare Login Flow");

        String rel = apiHome == null || apiHome.isEmpty() ? API_ROOT_END_POINT : null;
        GetData<JsonElement> getHome = new GetData<>(ctx, rel, null, uiManager);
        GetHomeCallback getHomeCallback = (GetHomeCallback) prepareMainFlow(ctx);

        AuthorizationCallback authorizationCallback = new AuthorizationCallback(ctx, uiManager, getHome, getHomeCallback);

        return authorizationCallback;
    }

    private static FutureCallback<JsonElement> prepareMainFlow(Context ctx) {
        Util.logd("Prepare Login Flow");

        String rel = tertulias == null || tertulias.length == 0 ? "tertulias" : null;

        GetData<JsonElement> getTertulias = new GetData<>(ctx, rel, apiHome, uiManager);
        GetTertuliasCallback getTertuliasCallback = new GetTertuliasCallback(ctx, null, null, uiManager, false);

        rel = ! uiManager.isUserInfo() ? "me" : null;
        GetData<JsonElement> getMe = new GetData<>(ctx, rel, apiHome, uiManager);
        GetMeCallback getMeCallback = new GetMeCallback(ctx, null, getTertulias, getTertuliasCallback, uiManager, false);

        rel = Util.getMobileServiceClient(ctx).getCurrentUser() != null ? null : "registration";
        GetData<JsonElement> postRegister = new GetData<>(ctx, rel, apiHome, uiManager);
        PostRegisterCallback postRegisterCallback = new PostRegisterCallback(ctx, null, getMe, getMeCallback, uiManager, false);

        GetHomeCallback getHomeCallback = new GetHomeCallback(ctx, null, postRegister, postRegisterCallback, uiManager, false);

        return getHomeCallback;
    }

    private void doLogout(final Context ctx) {
        Futures.addCallback(
                Util.getMobileServiceClient(this).logout(),
                new FutureCallback<MobileServiceUser>() {

                    @Override
                    public void onSuccess(MobileServiceUser user) {
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.removeAllCookie();
                        Util.clearCachedCredentialsAsync(ctx);
                        Util.getMobileServiceClient(MainActivity.this).setCurrentUser(null);
                        clearCacheTertulias();
                        tertulias = new TertuliaListItem[0];
                        me = null;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uiManager.swapAdapter(new TertuliasArrayAdapter(MainActivity.this, tertulias != null ? tertulias : new TertuliaListItem[0]));
                                uiManager.setLoggedOut();
                            }
                        });

//                        Runnable runnable = new Runnable() {
//                            @Override
//                            public void run() {
//                                uiManager.swapAdapter(new TertuliasArrayAdapter(MainActivity.this, tertulias != null ? tertulias : new TertuliaListItem[0]));
//                                uiManager.setLoggedOut();
//                            }
//                        };
//                        if (Looper.myLooper() == null)
//                            Looper.prepare();
//                        if (Looper.myLooper() == Looper.getMainLooper())
//                            runnable.run();
//                        else {
//                            Handler mainHandler = new Handler(Looper.getMainLooper());
//                            mainHandler.post(runnable);
//                        }

                        if (Util.getMobileServiceClient(MainActivity.this).getCurrentUser() == null)
                            Util.longSnack(findViewById(android.R.id.content), R.string.main_activity_logout_succeed_message);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        updateStatusAlert(R.string.main_activity_logout_failed_message);
                    }
                }
        );
    }

    //    endregion

    private void cacheTertulias(TertuliaListItem[] tertulias) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String tertuliasJson = tertulias == null ? null : new Gson().toJson(tertulias);
        editor.putString(TERTULIAS_PREF, tertuliasJson);
        editor.commit();
    }

    private void clearCacheTertulias() {
        cacheTertulias(null);
    }

    @Nullable
    private TertuliaListItem[] loadCachedTertulias() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String tertuliasJson = prefs.getString(TERTULIAS_PREF, null);
        if (tertuliasJson == null)
            return null;
        TertuliaListItem[] tertulias = new Gson().fromJson(tertuliasJson, TertuliaListItem[].class);
        return tertulias;
    }

//    endregion

//  region private static methods

//  endregion

}
