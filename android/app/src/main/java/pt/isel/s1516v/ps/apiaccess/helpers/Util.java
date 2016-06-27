package pt.isel.s1516v.ps.apiaccess.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.internal.Network;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.TertuliasApplication;

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
        Snackbar.make(ctx, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

    public static void longSnack(View ctx, int message) {
        Snackbar.make(ctx, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
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
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(isSetupAsUpEnabled);
//        actionBar.setDisplayUseLogoEnabled(true);
    }

    public static boolean isOnline(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // region Private stuff

    private static boolean isJsonArray(String str) {
        if (!isMatch(str, "[", "]")) return false;
        String subStr = str.substring(1, str.length()-1);
        if (isJsonObject(subStr)) return true;
        return !subStr.contains(":");
    }

    private static boolean isJsonObject(String str) {
        if (isMatch(str, "{", ":", "}")) return true;
        return str.contains(":");
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
