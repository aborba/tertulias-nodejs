package pt.isel.s1516v.ps.apiaccess;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

public class StaticUtil {

    public static boolean isGooglePlayServicesAvailable(Context ctx) {
        int result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ctx);
        return result == ConnectionResult.SUCCESS;
    }

    private final static String msAppUrl = "https://tertulias.azurewebsites.net";
    private static MobileServiceClient mobileServiceClient;

    public static MobileServiceClient getMobileServiceClient(Context ctx) {
        if (mobileServiceClient == null) {
            try {
                mobileServiceClient = new MobileServiceClient(msAppUrl, ctx);
                mobileServiceClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                    @Override
                    public OkHttpClient createOkHttpClient() {
                        OkHttpClient okHttpClient = new OkHttpClient();
                        okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
                        okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
                        return okHttpClient;
                    }
                });
            } catch (MalformedURLException e) {
                Toast.makeText(ctx, "Mobile service creation error", Toast.LENGTH_LONG).show();
            }
        }
        return mobileServiceClient;
    }


}
