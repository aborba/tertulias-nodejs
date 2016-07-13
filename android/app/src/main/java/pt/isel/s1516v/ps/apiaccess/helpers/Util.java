package pt.isel.s1516v.ps.apiaccess.helpers;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
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
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
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

    public static void lockOrientation(Context ctx) {
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
        ((Activity)ctx).setRequestedOrientation(screenOrientation);
    }

    public static String getAuthenticationToken(MobileServiceClient cli) {
        if (cli == null)
            return null;
        MobileServiceUser usr = cli.getCurrentUser();
        if (usr == null)
            return null;
        return usr.getAuthenticationToken();
    }

    public static boolean isTokenValid(String token, int withinMillis) {
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
                jwt += "=="; break;
            case 3:
                jwt += "="; break;
            default:
                throw new RuntimeException("Invalid base64url string.");
        }
        String payloadString = new String(Base64.decode(jwt, Base64.DEFAULT));
        JwtPayload payload = new Gson().fromJson(payloadString, JwtPayload.class);
        return payload.exp * 1000 > new Date().getTime() + withinMillis;
    }

    public static void unlockOrientation(Context ctx) {
        ((Activity)ctx).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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
        SearchManager searchManager = (SearchManager) ctx.getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = ((Activity)ctx).getComponentName();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(componentName);
        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconifiedByDefault(false);
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
            Util.longSnack(((Activity) ctx).getWindow().getDecorView().findViewById(android.R.id.content),
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
            Util.longSnack(((Activity) ctx).getWindow().getDecorView().findViewById(android.R.id.content),
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

    public static String getEMsg(Context ctx, String msg) {
        if (!Util.isJson(msg)) return msg;
        Error error = new Gson().fromJson(msg, Error.class);
        return error.getStatusCodeMessage(ctx);
    }

    public static <T> T[] extractParcelableArray(Intent intent, String extraLabel, Class<T> ct) {
        T[] t = null;
        if (intent.hasExtra(extraLabel)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(extraLabel);
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
        String subStr = str.substring(1, str.length()-1);
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

}
