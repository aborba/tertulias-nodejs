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

package pt.isel.s1516v.ps.apiaccess.helpers;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.HttpConstants;
import com.microsoft.windowsazure.mobileservices.http.MobileServiceHttpClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import pt.isel.s1516v.ps.apiaccess.FutureCallback2;
import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.flow.Futurizable;
import pt.isel.s1516v.ps.apiaccess.memberinvitation.ViewMembersActivity;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaListItem;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiError;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiGoogleCredentials;
import pt.isel.s1516v.ps.apiaccess.support.remote.JwtPayload;

public class Util {

    private final static String msAppUrl = "https://tertulias.azurewebsites.net";

    private static MobileServiceClient mClient;

    public static void longToast(Context ctx, int message) {
        Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
    }

    public static void longToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
    }

    public static void longSnack(View ctx, String message) {
        Snackbar snackbar = Snackbar.make(ctx, message, Snackbar.LENGTH_LONG);
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                .setTextColor(Color.WHITE);
        snackbar.setAction("Action", null).show();
    }

    public static void longSnack(View ctx, int message) {
        Snackbar snackbar = Snackbar.make(ctx, message, Snackbar.LENGTH_LONG);
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                .setTextColor(Color.WHITE);
        snackbar.setAction("Action", null).show();
    }

    public static void alert(Context ctx, int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx).setMessage(message);
        if (title > 0) builder.setTitle(title);
        builder.create().show();
    }

    public static void alert(Context ctx, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx).setMessage(message);
        if (title != null && title.length() > 0) builder.setTitle(title);
        builder.create().show();
    }

    public static void lockPortrait(Context ctx) {
        ((Activity) ctx).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        lockOrientation(ctx);
    }

    public static void lockOrientation(Context ctx) {
        Util.logd("Locking screen orientation");
        int screenOrientation;
        int rotation = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            case Surface.ROTATION_270:
            default:
                screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
        }
        ((Activity) ctx).setRequestedOrientation(screenOrientation);
    }

    // region Token

    public static void refreshToken(final Context ctx, final View rootView, final Futurizable<JsonElement> future, final FutureCallback<JsonElement> futureCallback) {

        ServiceFilter serviceFilter = new ServiceFilter() {
            @Override
            public ListenableFuture handleRequest(ServiceFilterRequest request, NextServiceFilterCallback next) {
                //request.addHeader("X-Custom-Header", "Header Value");

                ListenableFuture nextFuture = next.onNext(request);
                Futures.addCallback(
                        nextFuture,
                        new FutureCallback() {

                            @Override
                            public void onSuccess(Object response) {
                                if (response != null) {
                                    String content = ((ServiceFilterResponse)response).getContent();
                                    if (content != null) {
                                        ApiGoogleCredentials credentials = new Gson().fromJson(content, ApiGoogleCredentials.class);
                                        Util.setCredentials(ctx, credentials);
                                        Util.cacheCredentialsAsync(ctx, credentials);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Util.longSnack(rootView, t.getMessage());
                                Util.logd("ServiceFilter on refresh token failed");
                                Util.logd(t.getMessage());
                            }
                        }
                );
                return nextFuture;
            }
        };

        MobileServiceClient cli = Util.getMobileServiceClient(ctx).withFilter(serviceFilter);
        MobileServiceHttpClient httpClient = new MobileServiceHttpClient(cli);
        String path = "/.auth/refresh";
        byte[] content = null;
        String httpMethod = HttpConstants.GetMethod;
        List<Pair<String, String>> requestHeaders = null; // new LinkedList<>();
        List<Pair<String, String>> parameters = new LinkedList<>();
        parameters.add(new Pair<>("access_type", "offline"));
        ListenableFuture<ServiceFilterResponse> serviceFilterResponseListenableFuture = httpClient.request(path, content, httpMethod, requestHeaders, parameters);
        Futures.addCallback(
                serviceFilterResponseListenableFuture,
                new FutureCallback<ServiceFilterResponse>() {

                    @Override
                    public void onSuccess(ServiceFilterResponse result) {
                        Util.logd(result.toString());
                        Futures.addCallback(future.getFuture(), futureCallback);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Util.logd(t.getMessage());
                        Util.longSnack(rootView, t.getMessage());
                    }
                }
        );
    }

    public static String getAuthenticationToken(MobileServiceClient cli) {
        if (cli == null)
            return null;
        MobileServiceUser mobileServiceUser = cli.getCurrentUser();
        return mobileServiceUser == null ? null : mobileServiceUser.getAuthenticationToken();
    }

    public static boolean isTokenValid(String token, long withinMillis) {
        if (token == null)
            return false;
        String[] tokenParts = token.split("\\.");
        if (tokenParts.length <= 1)
            return false;
        String jwt = tokenParts[1];
        jwt = jwt.replace('-', '+').replace('_', '/');
        switch (jwt.length() % 4) {
            case 0:
                break;
            case 2:
                jwt += "==";
                break;
            case 3:
                jwt += "=";
                break;
            default:
                throw new IllegalArgumentException("Invalid base64url string.");
        }
        String payloadString = new String(Base64.decode(jwt, Base64.DEFAULT));
        JwtPayload payload = new Gson().fromJson(payloadString, JwtPayload.class);
        Date expirationTime = new Date(payload.exp * 1000);
        Date currentTime = new Date();
        Long expirationTimeInMillis = expirationTime.getTime();
        Long currentTimeInMillis = currentTime.getTime();
        Long currentTimeOutInMillis = currentTimeInMillis - withinMillis;
        boolean result = expirationTimeInMillis > currentTimeOutInMillis;
        Util.logd(String.format(Locale.getDefault(), "Expiration: %s\nCurrent: %s", expirationTime.toString(), currentTime.toString()));
        return result;
    }

    public static boolean isCurrentTokenValid(Context ctx, long withinMillis) {
        String token = getMobileServiceClient(ctx).getCurrentUser().getAuthenticationToken();
        return isTokenValid(token, withinMillis);
    }

    public static boolean isCurrentTokenValid(Context ctx) {
        return isCurrentTokenValid(ctx, 0);
    }

    // endregion

    public static void unlockOrientation(Context ctx) {
        Util.logd("Unlocking screen orientation");
        ((Activity) ctx).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public static boolean isJson(String str) {
        return isJsonArray(str) || isJsonObject(str);
    }

    public static void logd(String message) {
        Log.d("Trt", message);
    }

    public static boolean isSignedIn(Context ctx) {
        return getMobileServiceClient(ctx).getCurrentUser() != null;
    }

    public static MobileServiceClient getMobileServiceClient(Context ctx) {
        if (mClient == null) {
            try {
                mClient = new MobileServiceClient(msAppUrl, ctx);
                mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                    @Override
                    public OkHttpClient createOkHttpClient() {
                        OkHttpClient okHttpClient = new OkHttpClient();
                        okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
                        okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
                        return okHttpClient;
                    }
                });
            } catch (MalformedURLException e) {
                Toast.makeText(ctx, R.string.app_mobile_service_creation_error, Toast.LENGTH_LONG).show();
            }
        }
        return mClient;
    }

    public static final int IGNORE = -1;

    public static void setupToolBar(@NonNull Toolbar toolbar, int title, int subtitle, int menu, Toolbar.OnMenuItemClickListener menuItemClickListener) {
        if (toolbar == null)
            throw new IllegalArgumentException();
        if (title != IGNORE) toolbar.setTitle(title);
        if (subtitle != IGNORE) toolbar.setSubtitle(subtitle);
        if (menu != IGNORE) {
            toolbar.inflateMenu(menu);
            if (menuItemClickListener != null)
                toolbar.setOnMenuItemClickListener(menuItemClickListener);
        }
        toolbar.setLogo(R.mipmap.tertulias);
    }

    public static void setupToolBar(final Context ctx, @NonNull Toolbar toolbar, int title, int subtitle,
                                    int menu, Toolbar.OnMenuItemClickListener menuselection, boolean backBehaviour) {
        setupToolBar(toolbar, title, subtitle, menu, menuselection);
        if (backBehaviour) {
            Drawable backArrow = ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.ic_arrow_back_black_24dp, null);
            toolbar.setNavigationIcon(backArrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) ctx).finish();
                }
            });
        }
    }

    public static void setupToolBar(final Context ctx, @NonNull Toolbar toolbar, int title, int subtitle,
                                    int menu, Toolbar.OnMenuItemClickListener menuselection, boolean backBehaviour, SearchView searchView) {
        setupToolBar(ctx, toolbar, title, subtitle, menu, menuselection, backBehaviour);
        if (searchView != null) {
            SearchManager searchManager = (SearchManager) ctx.getSystemService(Context.SEARCH_SERVICE);
            ComponentName componentName = ((Activity) ctx).getComponentName();
            SearchableInfo searchableInfo = searchManager.getSearchableInfo(componentName);
            searchView.setSearchableInfo(searchableInfo);
            searchView.setIconifiedByDefault(false);
        }
    }

    public static void setupActionBar(AppCompatActivity ctx, int title, boolean isSetupAsUpEnabled) {
        ActionBar actionBar = ctx.getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(isSetupAsUpEnabled);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.tertulias);
        actionBar.setTitle(title);
        actionBar.show();
    }

    public static void setupActionBar(AppCompatActivity ctx, int title, int toolbarId, boolean isSetupAsUpEnabled) {
        Toolbar toolbar = (Toolbar) ctx.findViewById(toolbarId);
        ctx.setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.tertulias);
        toolbar.setTitle(title);
    }

    public static <T extends RecyclerView.ViewHolder> void setupAdapter(Activity ctx, RecyclerView recyclerView, RecyclerView.Adapter<T> viewAdapter) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(viewAdapter);
        viewAdapter.notifyDataSetChanged();
    }

    public static boolean isConnectivityAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void request(Activity ctx, String route, String httpMethod, List<Pair<String, String>> parameters, FutureCallback<JsonElement> callback) {
        if (route == null || httpMethod == null) {
            Util.longSnack(ctx.getWindow().getDecorView().findViewById(android.R.id.content),
                    R.string.main_activity_server_error_exiting);
            ctx.finish();
        }
        if (!Util.isConnectivityAvailable(ctx)) {
            Util.alert(ctx, R.string.main_activity_no_network_title, R.string.main_activity_no_network);
            return;
        }

        MobileServiceClient cli = Util.getMobileServiceClient(ctx);
        ListenableFuture<JsonElement> future = cli.invokeApi(route, null, httpMethod, parameters);
        if (callback != null) Futures.addCallback(future, callback);
    }

    public static void request(Activity ctx, String route, String httpMethod, JsonElement body, FutureCallback<JsonElement> callback) {
        if (route == null || httpMethod == null) {
            Util.longSnack(ctx.getWindow().getDecorView().findViewById(android.R.id.content),
                    R.string.main_activity_server_error_exiting);
            ctx.finish();
        }
        if (!Util.isConnectivityAvailable(ctx)) {
            Util.alert(ctx, R.string.main_activity_no_network_title, R.string.main_activity_no_network);
            return;
        }

        MobileServiceClient cli = Util.getMobileServiceClient(ctx);
        ListenableFuture<JsonElement> future = cli.invokeApi(route, null, httpMethod, null);
        if (callback != null) Futures.addCallback(future, callback);
    }

    public static double string2Double(String value) {
        return Double.parseDouble(TextUtils.isEmpty(value) ? "0.0" : value);
    }

    public static String getEMsg(Context ctx, String msg) {
        if (!Util.isJson(msg)) return msg;
        Error error = new Gson().fromJson(msg, Error.class);
        return error.getStatusCodeMessage(ctx);
    }

    public static <T extends Parcelable> void insertParcelableArray(Intent intent, String extraLabel, T[] t) {
        intent.putExtra(ViewMembersActivity.INTENT_LINKS, t);
    }

    public static <T> T[] extractParcelableArray(Intent intent, String extraLabel, Class<T> ct) {
        T[] t = null;
        if (intent.hasExtra(extraLabel)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(ViewMembersActivity.INTENT_LINKS);
            if (parcelables != null) {
                t = (T[]) Array.newInstance(ct, parcelables.length);
                for (int i = 0; i < parcelables.length; i++)
                    t[i] = (T) parcelables[i];
            }
        }
        return t;
    }

    // region Fetch

    public static final Bitmap getBitMap(String urlString) throws IOException {
        return getBitMap(new URL(urlString));
    }

    public static final Bitmap getBitMap(URL url) throws IOException {
        HttpURLConnection _httpURLConnection = null;
        try {
            _httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream _inputStream = _httpURLConnection.getInputStream();
            return BitmapFactory.decodeStream(_inputStream);
        } finally {
            if (_httpURLConnection != null) _httpURLConnection.disconnect();
        }
    }

    // endregion

    // region Private stuff

    private static boolean isJsonArray(String str) {
        if (!isMatch(str, "[", "]")) return false;
        String subStr = str.substring(1, str.length() - 1);
        return isJsonObject(subStr) || !subStr.contains(":");
    }

    private static boolean isJsonObject(String str) {
        return isMatch(str, "{", ":", "}") || str.contains(":");
    }

    private static boolean isMatch(String... args) {
        switch (args.length) {
            case 3:
                return args[0].startsWith(args[1]) && args[0].endsWith(args[2]);
            case 4:
                return isMatch(args[0], args[1], args[3]) && args[0].contains(args[2]);
            default:
                return false;
        }
    }

    // endregion

    public static boolean isZipCode(String target, String country) {
        switch (country.toLowerCase()) {
            case "czech republic":
                return target.matches("\\d{3}\\s\\d{2}");
            case "netherlands":
                target = target.toUpperCase();
                if (target.startsWith("NL-"))
                    target = target.substring(3);
                return target.matches("\\d{4}\\s[A-Z]{2}");
            case "portugal":
            default:
                return target.matches("^\\d{4}-\\d{3}");
        }

    }

    // region Credentials

    public static void cacheCredentials(final Context ctx, String user, String token) {
        SharedPreferences prefs = ctx.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USERID_PREF, user);
        editor.putString(TOKEN_PREF, token);
        editor.commit();
    }

    public static void cacheCredentials(Context ctx, MobileServiceUser user) {
        cacheCredentials(ctx, user.getUserId(), user.getAuthenticationToken());
    }

    public static void cacheCredentials(Context ctx, ApiGoogleCredentials credentials) {
        cacheCredentials(ctx, credentials.getUserSid(), credentials.getToken());
    }

    public static void clearCachedCredentials(Context ctx) {
        cacheCredentials(ctx, null, null);
    }

    public static String[] getCachedCredentials(final Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String user = prefs.getString(USERID_PREF, null);
        String token = prefs.getString(TOKEN_PREF, null);
        return new String[] { user, token };
    }

    private static void publishCachedCredentials(final Context ctx, String[] credentials, final FutureCallback2 futureCallback2) {
        MobileServiceUser msu = new MobileServiceUser(credentials[0]);
        msu.setAuthenticationToken(credentials[1]);
        if (credentials[0] == null) {
            Util.getMobileServiceClient(ctx).setCurrentUser(null);
            futureCallback2.onSuccess(ctx, null);
            return;
        }
        Util.getMobileServiceClient(ctx).setCurrentUser(msu);
        futureCallback2.onSuccess(ctx, msu);
    }

    public static void loadCachedCredentials(final Context ctx, final FutureCallback2 futureCallback2) {
        String[] credentials = getCachedCredentials(ctx);
        publishCachedCredentials(ctx, credentials, futureCallback2);
    }

    public static void cacheCredentialsAsync(final Context ctx, String user, String token) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                cacheCredentials(ctx, params[0], params[1]);
                return null;
            }
        }.execute(user, token);
    }

    public static void cacheCredentialsAsync(Context ctx, MobileServiceUser user) {
        cacheCredentialsAsync(ctx, user.getUserId(), user.getAuthenticationToken());
    }

    public static void cacheCredentialsAsync(Context ctx, ApiGoogleCredentials credentials) {
        cacheCredentialsAsync(ctx, credentials.getUserSid(), credentials.getToken());
    }

    public static void clearCachedCredentialsAsync(Context ctx) {
        cacheCredentialsAsync(ctx, null, null);
    }

    public static void loadCachedCredentialsAsync(final Context ctx, final FutureCallback2 futureCallback2) {
        new AsyncTask<Context, Void, String[]>() {
            @Override
            protected String[] doInBackground(Context... ctx) {
                return getCachedCredentials(ctx[0]);
            }

            @Override
            protected void onPostExecute(String[] credentials) {
                publishCachedCredentials(ctx, credentials, futureCallback2);
            }
        }.execute(ctx);
    }

    public static void setCredentials(Context ctx, String user, String token) {
        MobileServiceClient cli = getMobileServiceClient(ctx);
        MobileServiceUser msu = new MobileServiceUser(user);
        msu.setAuthenticationToken(token);
        cli.setCurrentUser(msu);
    }

    public static void setCredentials(Context ctx, ApiGoogleCredentials credentials) {
        setCredentials(ctx, credentials.getUserSid(), credentials.getToken());
    }

    private static final String SHARED_PREFS_FILE = "tertulias_access";
    private static final String USERID_PREF = "userid";
    private static final String TOKEN_PREF = "token";

    // endregion

    public static String[] getTrimmedLowerCaseNames(TertuliaListItem[] tertulias) {
        String[] names = new String[tertulias.length];
        for (int i = 0; i < tertulias.length; i++)
            names[i] = tertulias[i].name.toLowerCase().trim();
        return names;
    }

    public static ApiError getApiError(Throwable t) {
        return new Gson().fromJson(t.getMessage(), ApiError.class);
    }

    public static int getApiErrorCode(Throwable t) {
        return getApiError(t).code;
    }

    public static boolean isApiError(Throwable t, int code) {
        return getApiErrorCode(t) == code;
    }

}
