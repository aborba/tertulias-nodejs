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

package pt.isel.s1516v.ps.apiaccess.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pt.isel.s1516v.ps.apiaccess.contentprovider.TertuliasCRUD;
import pt.isel.s1516v.ps.apiaccess.contentprovider.TertuliasContract;
import pt.isel.s1516v.ps.apiaccess.flow.Futurizable;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.sendmessage.ApiTertuliaNewMessage;
import pt.isel.s1516v.ps.apiaccess.sendmessage.TertuliaNewMessage;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;

public class TertuliasSyncAdapter extends AbstractThreadedSyncAdapter {

    private final Context ctx;
    private final ContentResolver contentResolver;

    public TertuliasSyncAdapter(Context ctx, boolean autoInitialize) {
        super(ctx, autoInitialize);
        Util.logd("SYNC ADAPTER: constructor 2pars");
        this.ctx = ctx;
        contentResolver = ctx.getContentResolver();
    }

    public TertuliasSyncAdapter(Context ctx, boolean autoInitialize, boolean allowParallelSyncs) {
        super(ctx, autoInitialize, allowParallelSyncs);
        Util.logd("SYNC ADAPTER: constructor 3pars");
        this.ctx = ctx;
        contentResolver = ctx.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Util.logd("SYNC ADAPTER: onPerformSync for account " + account.name);

        ArrayList<ContentProviderOperation> batch;

//        try {
            syncNewMessages(contentResolver);
//            batch = getBatchSyncMyNotifications(contentResolver);
//            if (batch != null)
//                provider.applyBatch(batch);
//
//        } catch (RemoteException | OperationApplicationException e) {
//            Util.logd(e.getMessage());
//        }
    }

    // region Send New Messages

    public void syncNewMessages(ContentResolver resolver) {
        Util.logd("SYNC ADAPTER: syncNewMessages");

        Collection<TertuliaNewMessage> messages = getNewMessages(ctx);
        if (messages.size() == 0)
            return;

        MobileServiceClient cli = Util.getMobileServiceClient(ctx);
        Util.logd("User: " + cli.getCurrentUser().toString());
        Util.logd("Token: " + cli.getCurrentUser().toString());
        String rel = TertuliasApi.LINK_SENDMESSAGE;
        Uri uri = TertuliasContract.MyNotifications.CONTENT_URI;

        for (TertuliaNewMessage message : messages) {
            ApiTertuliaNewMessage apiMessage = new ApiTertuliaNewMessage(message, Util.getMyKey());
            JsonElement body = new Gson().toJsonTree(apiMessage);
            ApiLinks apiLinks = new ApiLinks(new ApiLink[] { new ApiLink(rel, "POST", String.format("/tertulias/%d/message", message.tertulia))}); // TODO: melhorar - tem de vir de tertulia
            Bag bag = new Bag(ctx, message, cli, apiLinks, rel, body, uri);

            ListenableFuture<JsonElement> future = new GetFuturizable(bag).getFuture(); // TODO: verificar a renovação de tokens
            SendMessageCallback futureCallback = new SendMessageCallback(bag, false);
            Futures.addCallback(future, futureCallback);
        }
    }

    private class SendMessageCallback implements FutureCallback<JsonElement> {
        private final Bag bag;
        private final boolean isRetry;

        SendMessageCallback(Bag bag, boolean isRetry) {
            this.bag = bag;
            this.isRetry = isRetry;
        }

        @Override
        public void onSuccess(JsonElement result) {
            TertuliasCRUD.delete(bag.ctx, bag.successUri, bag.message);
        }

        @Override
        public void onFailure(Throwable t) {
            if (! isRetry && ( ! Util.isCurrentTokenValid(ctx) || Util.isApiError(t, 401))) {
                Futurizable<JsonElement> future = new GetFuturizable(bag);
                SendMessageCallback futureCallback = new SendMessageCallback(bag, true);
                Util.refreshToken(ctx, null, future, futureCallback);
                return;
            }
            Util.logd("Sync Permanent Failure: " + t.getMessage());
        }
    }

    public class GetFuturizable implements Futurizable<JsonElement> {
        private final Bag bag;

        public GetFuturizable(Bag bag) {
            this.bag = bag;
        }

        @Override
        public ListenableFuture<JsonElement> getFuture() {
            String targetRoute = bag.apiLinks.getRoute(bag.rel);
            String targetMethod = bag.apiLinks.getMethod(bag.rel);
            return bag.cli.invokeApi(targetRoute, null, targetMethod, null);
        }
    }

    private class Bag {
        final Context ctx;
        final TertuliaNewMessage message;
        final MobileServiceClient cli;
        final ApiLinks apiLinks;
        final String rel;
        final JsonElement body;
        final Uri successUri;

        public Bag(Context ctx, TertuliaNewMessage message, MobileServiceClient cli, ApiLinks apiLinks, String rel, JsonElement body, Uri successUri) {
            this.ctx = ctx;
            this.message = message;
            this.cli = cli;
            this.apiLinks = apiLinks;
            this.rel = rel;
            this.body = body;
            this.successUri = successUri;
        }
    }

    // endregion

    @Nullable
    private ArrayList<ContentProviderOperation> getBatchSyncMyNotifications(ContentResolver resolver) throws RemoteException {
        Util.logd("SYNC ADAPTER: getBatchSyncMyNotifications");
        Collection<TertuliaNewMessage> messages = getNewMessages(ctx);

        Util.logd("getBatchSyncMyNotifications: " + String.valueOf(messages.size()));
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(TertuliasContract.MyNotifications.CONTENT_URI);
        for (int i = 0; i < messages.size(); i++) {

        }
        return batch;
    }

//    @Nullable
//    private ArrayList<ContentProviderOperation> getBatchSyncNotifications(ContentResolver resolver) throws RemoteException {
//        Util.logd("SYNC ADAPTER: getBatchSyncNotifications");
//        Map<Integer, TertuliaMessage> localMessages = new HashMap<>();
//        Collection<TertuliaMessage> messagesToInsert = new LinkedList<>();
//        Collection<TertuliaMessage> messagesToUpdate = new LinkedList<>();
//        Collection<TertuliaMessage> messagesToDelete = new LinkedList<>();
//        ArrayList<ContentProviderOperation> batch = new ArrayList<>();
//
//        return batch;
//    }

    private static Set<TertuliaNewMessage> getNewMessages(Context ctx) {
        try {
            Uri uri = TertuliasContract.MyNotifications.CONTENT_URI;
            String[] projection = TertuliasContract.MyNotifications.PROJECTION_ALL;
            TertuliaNewMessage[] messages = TertuliasCRUD.read(ctx, uri, projection, TertuliaNewMessage.class);
            Set<TertuliaNewMessage> messagesSet = new HashSet<>(Arrays.asList(messages));
            return messagesSet;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
