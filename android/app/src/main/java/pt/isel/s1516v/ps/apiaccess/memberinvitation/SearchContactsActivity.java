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
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Error;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.tertuliasubscription.PublicTertuliaDetailsActivity;

public class SearchContactsActivity extends Activity
        implements TertuliasApi {

    public final static int ACTIVITY_REQUEST_CODE = SEARCH_CONTACTS_RETURN_CODE;
    public static final String DATA_SEARCH = "SubscribeTertulia_Search";

    private static final String APILINKS_CREATEVOUCHERS_KEY = LINK_POSTVAUCHER;
    private static final String APILINKS_GETVOUCHERS_KEY = LINK_GETVAUCHERS;
    private static final int REQUEST_LOCATION = 2;

    private String apiEndPoint, apiMethod;
    private ApiLinks apiLinks;

    private SearchView searchView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private ContactsArrayAdapter viewAdapter;
    private ContactListItem[] contacts;
    private ContactSelected[] selectedContacts;

    // region Activity Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);

        if (savedInstanceState != null) restoreInstanceState(savedInstanceState);

        progressBar = (ProgressBar) findViewById(R.id.sca_progressbar);
        searchView = (SearchView) findViewById(R.id.sca_search);
        searchView.setSubmitButtonEnabled(true);
        emptyView = (TextView) findViewById(R.id.sca_empty_view);

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.sca_toolbar),
                R.string.title_activity_search_public_tertulia,
                Util.IGNORE, Util.IGNORE, null, true, searchView);

        recyclerView = (RecyclerView) findViewById(R.id.sca_RecyclerView);
        viewAdapter = new ContactsArrayAdapter(this, contacts != null ? contacts : new ContactListItem[0]);
        Util.setupAdapter(this, recyclerView, viewAdapter);

        apiLinks = getIntent().getParcelableExtra(INTENT_LINKS);

        handleIntent(getIntent());

//        ArrayList<Contact> contacts = getNameEmailDetails(this);
//        for (Contact contact : contacts)
//            Util.logd(contact.toString());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence(DATA_SEARCH, searchView.getQuery());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            default:
                Util.longSnack(findViewById(android.R.id.content), R.string.member_invitation_toast_cancel);
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickInviteContacts(View view) {
        if (contacts == null)
            return;
        boolean[] selections = viewAdapter.getSelection();
        int selectionsCount = 0;
        for (boolean isSelected : selections)
            if (isSelected)
                selectionsCount++;
        selectedContacts = new ContactSelected[selectionsCount];
        for (int i = 0, j = 0; i < contacts.length; i++)
            if (selections[i])
                selectedContacts[j++] = new ContactSelected(contacts[i]);
        if (selectedContacts.length == 0)
            return;

        JsonElement postParameters = new JsonParser().parse(String.format(Locale.getDefault(), "{ \"count\" : %d }", selectedContacts.length));

        apiEndPoint = apiLinks.getRoute(APILINKS_CREATEVOUCHERS_KEY);
        apiMethod = apiLinks.getMethod(APILINKS_CREATEVOUCHERS_KEY);

        Futures.addCallback(Util.getMobileServiceClient(this)
                        .invokeApi(apiEndPoint, postParameters, apiMethod, null)
                , new CreateVauchersCallback(findViewById(android.R.id.content)));


        Log.d("trt", "Contacts invitation selected");
        Util.longSnack(view, R.string.member_invitation_toast_sent);
        finish();
    }

    public void onClickCancel(View view) {
        Log.d("trt", "Contacts invitation cancelled");
        Util.longSnack(view, R.string.member_invitation_toast_cancel);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PublicTertuliaDetailsActivity.ACTIVITY_REQUEST_CODE) {
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            if (resultCode == RESULT_OK) {
                Util.longSnack(rootView, R.string.public_tertulia_details_subscribe_success);
                setResult(RESULT_OK);
                if (searchView != null)
                    requestSearch(searchView.getQuery().toString());
            }
        }
    }

    // endregion

    // region Private Methods

    // endregion

    // region Private Classes

    public class CreateVauchersCallback implements FutureCallback<JsonElement> {
        private View view;

        public CreateVauchersCallback(View view) {
            this.view = view;
        }

        @Override
        public void onSuccess(JsonElement result) {
            Util.logd("Aquisition of invitation voucher succeeded");
            JsonObject jsonObject = result.getAsJsonObject();
            ApiLink[] links = new Gson().fromJson(jsonObject.get("links"), ApiLink[].class);
            apiLinks = new ApiLinks(links);
            apiEndPoint = apiLinks.getRoute(APILINKS_GETVOUCHERS_KEY);
            apiMethod = apiLinks.getMethod(APILINKS_GETVOUCHERS_KEY);

            Futures.addCallback(Util.getMobileServiceClient(SearchContactsActivity.this)
                            .invokeApi(apiEndPoint, null, apiMethod, null)
                    , new GetVauchersCallback(view));
        }

        @Override
        public void onFailure(Throwable e) {
            Util.longSnack(view, getEMsg(SearchContactsActivity.this, e.getMessage()));
            Util.logd("Aquisition of invitation voucher failed");
            Util.logd(e.getMessage());
            setResult(RESULT_FAIL);
            finish();
        }

    }

    public class GetVauchersCallback implements FutureCallback<JsonElement> {
        private View view;

        public GetVauchersCallback(View view) {
            this.view = view;
        }

        @Override
        public void onSuccess(JsonElement result) {
            Util.logd("Aquisition of invitation vouchers succeeded");
            JsonArray jsonArray = result.getAsJsonObject().get("vouchers").getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject item = jsonArray.get(i).getAsJsonObject();
                selectedContacts[i].vaucher = item.get("voucher").getAsString();
                selectedContacts[i].tertulia = item.get("tertulia").getAsString();
                selectedContacts[i].subject = item.get("subject").getAsString();
            }
            sendEmail(selectedContacts);
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onFailure(Throwable e) {
            Util.longSnack(view, getEMsg(SearchContactsActivity.this, e.getMessage()));
            Util.logd("Aquisition of invitation voucher failed");
            Util.logd(e.getMessage());
            setResult(RESULT_FAIL);
            finish();
        }

    }

    // endregion

    // region Private Methods

    private void sendEmail(ContactSelected[] contacts) {
        if (contacts == null || contacts.length == 0)
            return;
        String baseUri = "http://tertulias.azurewebsites.net/private_invitation/";
        String subject = "Invitation to join a Tertulia: " + contacts[0].tertulia;
        // TODO: Strings
        String messageTemplate =
                "Dear %s,\n" +
                "This is an invitation for you to join Tertulia %s.\n" +
                "The Tertulia is about %s.\n" +
                "In order to join, please subscribe on the following link:\n" +
                "<%s>\n" +
                "and download the app at:\n" +
                " - Android: " + "\n" +
                "\nThis invitation is private and is valid for one month for a single subscription.\n" +
                "Should you have any questions, please don't hesitate to contact the service provider at <mailto://antonio_borba@hotmail.com>.\n" +
                "\nSee you there";
        for (ContactSelected contact : contacts) {
            String messageBody = String.format(Locale.getDefault(), messageTemplate, contact.name, contact.tertulia, contact.subject, baseUri + contact.vaucher);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto:"));
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { contact.email });
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, messageBody);
            try {
                startActivity(Intent.createChooser(intent, "Send mail..."));
                finish();
            } catch (android.content.ActivityNotFoundException ex) {
                Util.longSnack(SearchContactsActivity.this.findViewById(android.R.id.content), "There is no email client installed.");
            }
        }
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null && query != null)
                searchView.setQuery(query, false);
            progressBar.setVisibility(View.VISIBLE);
            ArrayList<ContactListItem> contactsList = getFilteredContacts(this, query);
            contacts = contactsList.toArray(new ContactListItem[contactsList.size()]);
            viewAdapter = new ContactsArrayAdapter(this, contacts);
            recyclerView.swapAdapter(viewAdapter, true);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public ArrayList<ContactListItem> getFilteredContacts(Context ctx, String filter) {
        ArrayList<ContactListItem> contacts = new ArrayList<>();
        HashSet<String> emailsHs = new HashSet<>();
        Cursor cur = getFilteredContactsCursor(ctx, filter);
        int idSlot = cur.getColumnIndex(ContactsContract.RawContacts._ID);
        int nameSlot = cur.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME);
        int emailSlot = cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
        int photo1Slot = cur.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);
        int photo2Slot = cur.getColumnIndex(ContactsContract.CommonDataKinds.Photo.CONTACT_ID);
        if (cur.moveToFirst())
            do {
                int id = cur.getInt(idSlot);
                String name = cur.getString(nameSlot);
                String email = cur.getString(emailSlot);
                String photo1 = cur.getString(photo1Slot);
                String photo2 = cur.getString(photo2Slot);
                if (emailsHs.add(email.toLowerCase()))
                    contacts.add(new ContactListItem(id, name, email, photo1));
            } while (cur.moveToNext());
        cur.close();
        return contacts;
    }

    public Cursor getFilteredContactsCursor(Context ctx, String filter) {
        ContentResolver resolver = ctx.getContentResolver();

        String[] projection = new String[]{
                ContactsContract.RawContacts._ID,
                ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID
        };

        String filterStatement = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''"
                + " AND " + ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME + " LIKE ? ";
        String[] filterParams = new String[]{"%" + filter + "%"};

        String sort = "CASE WHEN " + ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, " + ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME + ", "
                + ContactsContract.CommonDataKinds.Email.DATA + " COLLATE NOCASE";

        return resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, filterStatement, filterParams, sort);
    }

    private void requestSearch(String query) {
//        ApiLinks apiLinks = getIntent().getParcelableExtra(INTENT_LINKS);
//        String apiEndPoint = apiLinks.getRoute(APILINKS_KEY);
//        String apiMethod = apiLinks.getMethod(APILINKS_KEY);
//        List<Pair<String, String>> parameters = new LinkedList<>();
//        parameters.add(new Pair<>("query", query));
//        parameters.add(new Pair<>("latitude", String.valueOf(currentLatitude)));
//        parameters.add(new Pair<>("longitude", String.valueOf(currentLongitude)));
//        Util.request(this, apiEndPoint, apiMethod, parameters, new SearchCallback());
    }

    private static String getEMsg(Context ctx, String msg) {
        if (!Util.isJson(msg)) return msg;
        Error error = new Gson().fromJson(msg, Error.class);
        return error.getStatusCodeMessage(ctx);
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        if (searchView == null)
            return;
        searchView.setQuery(savedInstanceState.getCharSequence(DATA_SEARCH), false);
    }

    // endregion

}
