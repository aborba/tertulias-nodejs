package pt.isel.s1516v.ps.apiaccess.flow;

import android.content.Context;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.HttpConstants;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;

public class GetData<T extends JsonElement> implements Futurizable<T> {
    private final Context ctx;
    private final String rel;
    private final ApiLinks apiLinks;

    public GetData(Context ctx, String rel, ApiLinks apiLinks) {
        this.ctx = ctx;
        this.rel = rel;
        this.apiLinks = apiLinks;
    }

    @Override
    public ListenableFuture<T> getFuture() {
        if (rel == null)
            throw new IllegalArgumentException();
        String targetRoute = apiLinks == null ? rel : apiLinks.getRoute(rel);
        String targetMethod = apiLinks == null ? HttpConstants.GetMethod : apiLinks.getMethod(rel);
        MobileServiceClient cli = Util.getMobileServiceClient(ctx);
        MobileServiceUser user = cli.getCurrentUser();
        return (ListenableFuture<T>) cli.invokeApi(targetRoute, null, targetMethod, null);
    }
}
