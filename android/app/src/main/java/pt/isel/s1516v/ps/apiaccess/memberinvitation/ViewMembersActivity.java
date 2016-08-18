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

package pt.isel.s1516v.ps.apiaccess.memberinvitation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.util.ArrayList;
import java.util.Arrays;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Error;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.memberinvitation.ui.VmUiManager;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;

public class ViewMembersActivity extends Activity implements TertuliasApi {

    public final static int ACTIVITY_REQUEST_CODE = SEARCH_PUBLIC_TERTULIA_RETURN_CODE;
    public static final String DATA_SEARCH = "SubscribeTertulia_Search";

    private static final String APILINKS_KEY = LINK_SEARCHPUBLIC;
    private static final int REQUEST_LOCATION = 2;

    private VmUiManager uiManager;
    private ApiLinks apiLinks;
    private ContactListItem[] contacts;

    private ContactsArrayAdapter viewAdapter;

    // region Activity Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_members);

        if (savedInstanceState != null) restoreInstanceState(savedInstanceState);

        uiManager = new VmUiManager(this);

        Util.setupToolBar(this, (Toolbar) uiManager.getView(VmUiManager.UIRESOURCE.TOOLBAR),
                R.string.title_activity_view_members,
                Util.IGNORE, Util.IGNORE, null, true);

        viewAdapter = new ContactsArrayAdapter(this, contacts != null ? contacts : new ContactListItem[0]);
        Util.setupAdapter(this, (RecyclerView) uiManager.getView(VmUiManager.UIRESOURCE.RECYCLE), viewAdapter);

        handleIntent(getIntent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            default:
                Util.longSnack(findViewById(android.R.id.content), R.string.new_tertulia_toast_cancel);
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onClickInviteContacts(View view) {
        Log.d("trt", "in onClickSubmitMembers");
        Intent intent = new Intent(this, SearchContactsActivity.class);
        intent.putParcelableArrayListExtra(SearchContactsActivity.INTENT_LINKS, new ArrayList<ApiLink>(Arrays.asList(apiLinks.get())));
        startActivityForResult(intent, SearchContactsActivity.ACTIVITY_REQUEST_CODE);
    }

    public void onClickEditMembers(View view) {
        Log.d("trt", "in onClickEditMembers");
        Intent intent = new Intent(this, SearchContactsActivity.class);
        intent.putParcelableArrayListExtra(SearchContactsActivity.INTENT_LINKS, new ArrayList<ApiLink>(Arrays.asList(apiLinks.get())));
        startActivityForResult(intent, SearchContactsActivity.ACTIVITY_REQUEST_CODE);
    }

    public void onClickCancel(View view) {
        Log.d("trt", "in onClickEditMembers");
        finish();
    }

    // endregion

    // region Private Methods

    private void refreshDataAndViews() {
        MobileServiceClient cli = Util.getMobileServiceClient(this);
        ListenableFuture<JsonElement> rTertuliasFuture = cli.invokeApi(apiLinks.getRoute(LINK_SELF), null, apiLinks.getMethod(LINK_SELF), null);
        Futures.addCallback(rTertuliasFuture, new MembersPresentation());
    }

    // endregion

    // region Private Classes

    // endregion

    // region Private Methods

    private void handleIntent(Intent intent) {
        if (intent.hasExtra(INTENT_LINKS)) {
            ApiLink[] links = Util.extractParcelableArray(getIntent(), INTENT_LINKS, ApiLink.class);
            apiLinks = new ApiLinks(links);
        }
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
    }

    private static String getEMsg(Context ctx, String msg) {
        if (!Util.isJson(msg)) return msg;
        Error error = new Gson().fromJson(msg, Error.class);
        return error.getStatusCodeMessage(ctx);
    }

    // endregion

    private class MembersPresentation implements FutureCallback<JsonElement> {
        @Override
        public void onSuccess(JsonElement result) {
            new AsyncTask<JsonElement, Void, ContactListItem[]>() {
                @Override
                protected ContactListItem[] doInBackground(JsonElement... params) {
                    ContactListItem[] contacts = new Gson().fromJson(params[0], ContactListItem[].class);
                    return contacts;
                }

                @Override
                protected void onPostExecute(ContactListItem[] contacts) {
                    uiManager.set(contacts);
                }
            }.execute(result);
        }

        @Override
        public void onFailure(Throwable e) {
            Context ctx = ViewMembersActivity.this;
            Util.longSnack(uiManager.getRootView(), getEMsg(ctx, e.getMessage()));
        }
    }

}
