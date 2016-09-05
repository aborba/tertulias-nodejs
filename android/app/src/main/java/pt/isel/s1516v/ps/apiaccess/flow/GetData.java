/*
 * Copyright (c) 2016 António Borba da Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package pt.isel.s1516v.ps.apiaccess.flow;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.HttpConstants;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.ui.MaUiManager;
import pt.isel.s1516v.ps.apiaccess.ui.UiManager;

public class GetData<T extends JsonElement> implements Futurizable<T> {
    private final Context ctx;
    private final String rel;
    private final ApiLinks apiLinks;
    private final UiManager uiManager;

    public GetData(Context ctx, String rel, ApiLinks apiLinks, UiManager uiManager) {
        this.ctx = ctx;
        this.rel = rel;
        this.apiLinks = apiLinks;
        this.uiManager = uiManager;
    }

    @Override
    public ListenableFuture<T> getFuture() {
        if (uiManager != null)
            uiManager.showProgressBar();
        if (rel == null)
            return new ListenableFuture<T>() {
                @Override
                public void addListener(Runnable listener, Executor executor) {
                    executor.execute(listener);
                }

                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return true;
                }

                @Override
                public boolean isCancelled() {
                    return true;
                }

                @Override
                public boolean isDone() {
                    return true;
                }

                @Override
                public T get() throws InterruptedException, ExecutionException {
                    return null;
                }

                @Override
                public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                    return null;
                }
            }; // throw new IllegalArgumentException();

        MobileServiceClient cli = Util.getMobileServiceClient(ctx);

        String targetRoute = apiLinks == null ? rel : apiLinks.getRoute(rel);
        JsonElement body = null;
        String targetMethod = apiLinks == null ? HttpConstants.GetMethod : apiLinks.getMethod(rel);
        List<Pair<String, String>> parameters = new LinkedList<>();

        return (ListenableFuture<T>) cli.invokeApi(targetRoute, body, targetMethod, parameters);
    }
}
