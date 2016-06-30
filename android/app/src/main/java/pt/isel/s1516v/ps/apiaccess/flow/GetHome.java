package pt.isel.s1516v.ps.apiaccess.flow;

import android.content.Context;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.HttpConstants;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;

public class GetHome implements Futurizable<JsonElement> {
    private static final String API_HOME = "/";
    private final Context ctx;

    public GetHome(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public ListenableFuture<JsonElement> getFuture() {
        MobileServiceClient cli = Util.getMobileServiceClient(ctx);
        MobileServiceUser user = cli.getCurrentUser();
        return cli.invokeApi("/", null, HttpConstants.GetMethod, null);
    }

    @Override
    public ListenableFuture<JsonElement> getFuture(String route, String method) {
        throw new UnsupportedOperationException();
    }
}
