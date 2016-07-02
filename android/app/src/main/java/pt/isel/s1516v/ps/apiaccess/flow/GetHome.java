package pt.isel.s1516v.ps.apiaccess.flow;

import android.content.Context;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.HttpConstants;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;

public class GetHome implements Futurizable<JsonElement> {
    private final Context ctx;
    private final String rel;

    public GetHome(Context ctx, String rel) {
        this.ctx = ctx;
        this.rel = rel;
    }

    @Override
    public ListenableFuture<JsonElement> getFuture() {
        MobileServiceClient cli = Util.getMobileServiceClient(ctx);
        MobileServiceUser user = cli.getCurrentUser();
        return cli.invokeApi(rel, null, HttpConstants.GetMethod, null);
    }

//    @Override
//    public ListenableFuture<JsonElement> getFuture(String route, String method) {
//        throw new UnsupportedOperationException();
//    }
}
