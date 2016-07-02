package pt.isel.s1516v.ps.apiaccess.flow;

import com.google.common.util.concurrent.ListenableFuture;

public interface Futurizable<T> {
    ListenableFuture<T> getFuture();
    //ListenableFuture<T> getFuture(String route, String method);
}
