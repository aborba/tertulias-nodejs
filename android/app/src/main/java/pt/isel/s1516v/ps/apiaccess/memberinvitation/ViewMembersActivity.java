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

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.tertuliasubscription.PublicTertulia;
import pt.isel.s1516v.ps.apiaccess.tertuliasubscription.PublicTertuliaArrayAdapter;
import pt.isel.s1516v.ps.apiaccess.tertuliasubscription.PublicTertuliaDetailsActivity;
import pt.isel.s1516v.ps.apiaccess.tertuliasubscription.gson.ApiSearchList;
import pt.isel.s1516v.ps.apiaccess.tertuliasubscription.gson.ApiSearchListItem;

public class ViewMembersActivity extends Activity implements TertuliasApi {

    public final static int ACTIVITY_REQUEST_CODE = SEARCH_PUBLIC_TERTULIA_RETURN_CODE;
    public static final String DATA_SEARCH = "SubscribeTertulia_Search";

    private static final String APILINKS_KEY = LINK_SEARCHPUBLIC;
    private static final int REQUEST_LOCATION = 2;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private PublicTertuliaArrayAdapter viewAdapter;
    private PublicTertulia[] publicTertulias;

    private ApiLink[] links;

    // region Activity Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_members);

        if (savedInstanceState != null) restoreInstanceState(savedInstanceState);

        progressBar = (ProgressBar) findViewById(R.id.vma_progressbar);
        emptyView = (TextView) findViewById(R.id.vma_empty_view);

        Util.setupToolBar(this, (Toolbar) findViewById(R.id.toolbar),
                R.string.title_activity_view_members,
                Util.IGNORE, Util.IGNORE, null, true);

        recyclerView = (RecyclerView) findViewById(R.id.vma_RecyclerView);
        viewAdapter = new PublicTertuliaArrayAdapter(this, publicTertulias != null ? publicTertulias : new PublicTertulia[0]);
        Util.setupAdapter(this, recyclerView, viewAdapter);

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
        intent.putParcelableArrayListExtra(SearchContactsActivity.INTENT_LINKS, new ArrayList<ApiLink>(Arrays.asList(links)));
        startActivityForResult(intent, SearchContactsActivity.ACTIVITY_REQUEST_CODE);
    }

    public void onClickEditMembers(View view) {
        Log.d("trt", "in onClickEditMembers");
        Intent intent = new Intent(this, SearchContactsActivity.class);
        intent.putParcelableArrayListExtra(SearchContactsActivity.INTENT_LINKS, new ArrayList<ApiLink>(Arrays.asList(links)));
        startActivityForResult(intent, SearchContactsActivity.ACTIVITY_REQUEST_CODE);
    }

    public void onClickCancel(View view) {
        Log.d("trt", "in onClickEditMembers");
        finish();
    }

    // endregion

    // region Private Methods

    // endregion

    // region Private Classes

    // endregion

    // region Private Methods

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            progressBar.setVisibility(View.VISIBLE);
        }
        if (intent.hasExtra(INTENT_LINKS)) {
            Object[] objects = getIntent().getParcelableArrayListExtra(INTENT_LINKS).toArray();
            links = Arrays.copyOf(objects, objects.length, ApiLink[].class);
        }
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
    }

    // endregion

}
