package pt.isel.s1516v.ps.apiaccess.flow;

import android.content.Context;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.HttpConstants;

import pt.isel.s1516v.ps.apiaccess.MainActivity;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;

public class PostRegister implements Futurizable<JsonElement> {
    private final Context ctx;
    private String rel;

    public PostRegister(Context ctx, String rel) {
        this.ctx = ctx;
        this.rel = rel;
    }

    @Override
    public ListenableFuture<JsonElement> getFuture() {
        MobileServiceClient cli = Util.getMobileServiceClient(ctx);
        MobileServiceUser user = cli.getCurrentUser();
        return cli.invokeApi(MainActivity.apiHome.getRoute(rel), null, MainActivity.apiHome.getMethod(rel), null);
    }

//    @Override
//    public ListenableFuture<JsonElement> getFuture(String route, String method) {
//        throw new UnsupportedOperationException();
//    }
}
