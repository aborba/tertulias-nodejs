package pt.isel.pdm.g04.pf.presentation;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import pt.isel.pdm.g04.pf.R;
import pt.isel.pdm.g04.pf.data.parse.provider.ParseContract;
import pt.isel.pdm.g04.pf.data.parse.provider.objects.LocalParseSubscriptions;
import pt.isel.pdm.g04.pf.data.thoth.database.Schema;
import pt.isel.pdm.g04.pf.data.thoth.provider.ThothContract;
import pt.isel.pdm.g04.pf.helpers.Constants;
import pt.isel.pdm.g04.pf.helpers.Utils;
import pt.isel.pdm.g04.pf.presentation.widget.recyclerview.SpacesItemDecoration;
import pt.isel.pdm.g04.pf.presentation.widget.recyclerview.adapters.TeachersAdapter;
import pt.isel.pdm.g04.pf.syncadapter.parse.ParseSyncAdapter;

public class TeachersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView mRecyclerView;
    private TeachersAdapter mAdapter;
    ArrayList<String> subscriptions = new ArrayList<>();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            outState.putStringArrayList(Constants.Activities.TEACHERS_EXTRA, mAdapter.getSelectedTeachers());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (mAdapter != null) {
            mAdapter.setSelectedTeachers(savedInstanceState.getStringArrayList(Constants.Activities.TEACHERS_EXTRA));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        // Calling the RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(mRecyclerView);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                LocalParseSubscriptions lps = new LocalParseSubscriptions(TeachersActivity.this);
                lps.deleteAll();
                String channels = "";
                for (String email : mAdapter.getSelectedTeachers()) {
                    lps.insert("", email);
                    channels = channels + (TextUtils.isEmpty(channels) ? "" : "-") + Utils.mangleEmail(email);
                }
                Bundle requestSyncExtras = new Bundle();
                requestSyncExtras.putInt(ParseSyncAdapter.ACTION, ParseSyncAdapter.ACTION_SUBSCRIBE);
                requestSyncExtras.putString(ParseContract.Subscriptions.EMAIL, channels);
                ContentResolver.requestSync(MainActivity.account, ParseContract.AUTHORITY, requestSyncExtras);
                onBackPressed();
            }

        });

        // The number of Columns

        mRecyclerView.setHasFixedSize(true);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.num_columns_images));

        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.image_spacing)));

        getLoaderManager().initLoader(Constants.Thoth.Cursors.TEACHERS_LOADER, null, this);
        getLoaderManager().initLoader(Constants.Parse.Cursors.SUBSCRIPTIONS_LOADER, null, this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // region Loader CallBacks

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case Constants.Thoth.Cursors.TEACHERS_LOADER:
                return new CursorLoader(this, ThothContract.Teachers.CONTENT_URI,
                        Schema.Teachers.getColumns(), ThothContract.Teachers.ACADEMIC_EMAIL + " <> ?", new String[]{MainActivity.account.name}, null);
            case Constants.Parse.Cursors.SUBSCRIPTIONS_LOADER:
                return new CursorLoader(this, ParseContract.Subscriptions.CONTENT_URI,
                        ParseContract.Subscriptions.PROJECTION_ALL, null, null, null);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case Constants.Thoth.Cursors.TEACHERS_LOADER:
                if (mAdapter == null) {
                    mAdapter = new TeachersAdapter(mRecyclerView, data);
                } else {
                    mAdapter.swapCursor(data);
                }
                mRecyclerView.setAdapter(mAdapter);
                break;
            case Constants.Parse.Cursors.SUBSCRIPTIONS_LOADER:
                Cursor cursor = (new LocalParseSubscriptions(this)).queryAll();
                while (cursor.moveToNext()) {
                    subscriptions.add(cursor.getString(cursor.getColumnIndex(ParseContract.Subscriptions.EMAIL)));
                }
                cursor.close();
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (mAdapter != null)
            mAdapter.setSelectedTeachers(subscriptions);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    // endregion

}