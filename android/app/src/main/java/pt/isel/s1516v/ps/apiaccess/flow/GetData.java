package pt.isel.s1516v.ps.apiaccess.flow;

import android.content.Context;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;

public class GetData<T extends JsonElement> implements Futurizable<T> {
    private final Context ctx;

    public GetData(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public ListenableFuture<T> getFuture() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListenableFuture<T> getFuture(String route, String method) {
        MobileServiceClient cli = Util.getMobileServiceClient(ctx);
        MobileServiceUser user = cli.getCurrentUser();
        return (ListenableFuture<T>) cli.invokeApi(route, null, method, null);
    }
}
