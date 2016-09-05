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

package pt.isel.s1516v.ps.apiaccess.sendmessage;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonElement;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.contentprovider.TertuliasCRUD;
import pt.isel.s1516v.ps.apiaccess.contentprovider.TertuliasContract;
import pt.isel.s1516v.ps.apiaccess.flow.Futurizable;
import pt.isel.s1516v.ps.apiaccess.flow.GetData;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;

public class SendMessageActivity extends Activity implements TertuliasApi {

    public final static int ACTIVITY_REQUEST_CODE = TERTULIA_DETAILS_RETURN_CODE;
    public final static String SELF_LINK = LINK_SELF;
    private final static String MESSAGE_INSTANCE_STATE_LABEL = "message";

    private ApiTertuliaMessage apiMessage;
    private TertuliaNewMessage message;
    private SmUiManager uiManager;
    private ApiLinks apiLinks;

    // region Activity Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        if (savedInstanceState != null && savedInstanceState.containsKey(MESSAGE_INSTANCE_STATE_LABEL))
            apiMessage = savedInstanceState.getParcelable(MESSAGE_INSTANCE_STATE_LABEL);

        uiManager = new SmUiManager(this);

        Util.setupToolBar(this, (Toolbar) uiManager.getView(SmUiManager.UIRESOURCE.TOOLBAR),
                R.string.title_activity_send_message,
                Util.IGNORE, Util.IGNORE, null, true);

        restoreInstanceState(savedInstanceState);
        if (apiMessage != null)
            uiManager.set(apiMessage);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (apiMessage != null) {
            outState.putParcelable(MESSAGE_INSTANCE_STATE_LABEL, apiMessage);
        }
        super.onSaveInstanceState(outState);
    }

    protected void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null)
            return;
        if (savedInstanceState.containsKey(MESSAGE_INSTANCE_STATE_LABEL))
            apiMessage = savedInstanceState.getParcelable(MESSAGE_INSTANCE_STATE_LABEL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickSendMessage(View view) {
        Log.d("trt", "in onClickSendMessage");
        if (! isVerifications_0_Passed())
            return;

        int tertulia = getIntent().getIntExtra(INTENT_TERTULIA_ID, -1);
        message = new TertuliaNewMessage(tertulia, uiManager.getMessage(), Util.getMyKey());
        Uri result = TertuliasCRUD.create(this, TertuliasContract.MyNotifications.CONTENT_URI, message);
        Util.logd(result.toString());

//        try {
//            TertuliaMessage[] messages = TertuliasCRUD.read(this, TertuliasContract.MyNotifications.CONTENT_URI, TertuliaMessage.class);
//            Util.logd(String.valueOf(messages.length));
//        } catch (IllegalAccessException | InstantiationException e) {
//            e.printStackTrace();
//        }


//        Cursor cursor = getContentResolver().query(TertuliasContract.Notifications.CONTENT_URI, TertuliasContract.Notifications.PROJECTION_ALL, null, null, null);
//        TertuliaMessage[] messages = TertuliaMessage.getData(cursor);
//        cursor.close();

//        apiMessage = new ApiTertuliaMessage(uiManager.getMessage(), Util.getMyKey());
//        ApiLinks apiLinks = getIntent().getParcelableExtra(INTENT_LINKS);
//        MobileServiceClient cli = Util.getMobileServiceClient(this);
//        JsonElement body = new Gson().toJsonTree(apiMessage);
//        ListenableFuture<JsonElement> rTertuliasFuture = cli.invokeApi(apiLinks.getRoute(LINK_SENDMESSAGE), body, apiLinks.getMethod(LINK_SENDMESSAGE), null);
//        Futures.addCallback(rTertuliasFuture, new SendMessageCallback(false));
    }

    // endregion

    private boolean isVerifications_0_Passed() {
        if (!Util.isConnectivityAvailable(this)) {
            Util.alert(this, R.string.main_activity_no_network_title, R.string.main_activity_no_network);
            return false;
        }
        return true;
    }

    private class SendMessageCallback implements FutureCallback<JsonElement> {
        private final boolean isRetry;

        SendMessageCallback(boolean isRetry) {
            this.isRetry = isRetry;
        }

        @Override
        public void onSuccess(JsonElement result) {
            finish();
        }

        @Override
        public void onFailure(Throwable t) {
            Context ctx = SendMessageActivity.this;
            if (! isRetry && ( ! Util.isCurrentTokenValid(ctx) || Util.isApiError(t, 401))) { // && ! Util.isCurrentTokenValid(ctx)) {
                Util.longSnack(uiManager.getRootView(), R.string.main_activity_token_expired);
                Futurizable<JsonElement> future = new GetData<>(ctx, LINK_SELF, apiLinks, uiManager);
                Util.refreshToken(ctx, uiManager.getRootView(), future, new SendMessageCallback(true));
                return;
            }
            Util.longSnack(uiManager.getRootView(), t.getMessage());
        }
    }
}
