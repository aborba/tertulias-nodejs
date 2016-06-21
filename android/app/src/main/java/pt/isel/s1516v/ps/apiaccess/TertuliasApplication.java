package pt.isel.s1516v.ps.apiaccess;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

public class TertuliasApplication extends Application {

    private static TertuliasApplication mAppInstance;

    public TertuliasApplication() {
        mAppInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // region Static stuff

    public static TertuliasApplication getApplication() {
        return mAppInstance;
    }

    // endregion
}
