package pt.isel.pdm.g04.pf.presentation;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeRecyclerListener;
import com.fortysevendeg.swipelistview.SwipeRecyclerView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.OnCheckedChangeListener;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import pt.isel.pdm.g04.pf.R;
import pt.isel.pdm.g04.pf.TeacherLocatorApplication;
import pt.isel.pdm.g04.pf.data.AuthenticatedUser;
import pt.isel.pdm.g04.pf.data.Notification;
import pt.isel.pdm.g04.pf.data.parse.localhelpers.ParseEndPoint;
import pt.isel.pdm.g04.pf.data.parse.provider.ParseContract;
import pt.isel.pdm.g04.pf.geofences.GeofenceStore;
import pt.isel.pdm.g04.pf.geofences.IselGeofences;
import pt.isel.pdm.g04.pf.geofences.SimpleGeofence;
import pt.isel.pdm.g04.pf.helpers.Constants;
import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.helpers.Preferences;
import pt.isel.pdm.g04.pf.helpers.Utils;
import pt.isel.pdm.g04.pf.presentation.widget.recyclerview.adapters.NotificationsAdapter;
import pt.isel.pdm.g04.pf.workers.Task;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, GoogleMap.OnCameraChangeListener {

    public static Account account;

    private static ArrayList<Notification> mNotifications = new ArrayList<>();
    private static GeofenceStore mGeofenceStore;
    private static Account[] accounts;
    private static boolean TWO_PANE = false;
    private static final int SETACCOUNT_REQUEST_CODE = 0;
    private static final int PREFERENCES_REQUEST_CODE = 1;
    private static final Random generator = new Random();
    private AccountHeader headerResult;
    private Drawer result;
    private SwipeRecyclerView swipeRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar mToolbar;
    private NotificationsAdapter mAdapter;

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            SwitchDrawerItem item = (SwitchDrawerItem) drawerItem;
            if (item != null && item.getIdentifier() == Constants.Activities.Main.DRAWER_SHARE) {
                if (isChecked) {
                    mGeofenceStore.connect();
                } else {
                    mGeofenceStore.disconnect();
                }
            }
        }
    };
    private GoogleMap mMap;

    // region Life Cycle

    @Override
    public void onAttachFragment(Fragment fragment) {
        Logger.i("[MainActivity] Attached Fragment: " + fragment.getClass().getSimpleName());
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.setActivityVisible(true);


        if (findViewById(R.id.map_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            TWO_PANE = true;

            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(Constants.Isel.LOCATION));
                    if (Preferences.showGeofences(this)) {
                        mMap.setOnCameraChangeListener(MainActivity.this);
                    }
                }
            }
        } else {
            TWO_PANE = false;
        }

        swipeRecyclerView = (SwipeRecyclerView) findViewById(R.id.example_lv_list);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (account == null)
            setAccount();
        if (account != null) {
            loadAccount(savedInstanceState, account.name);
        }


        loadEmptyDrawerMenu();
        getLoaderManager().initLoader(Constants.Parse.Cursors.NOTIFICATIONS_LOADER, null, this);

        if (mGeofenceStore == null) {
            mGeofenceStore = new GeofenceStore(this);
        }

    }

    private void loadEmptyDrawerMenu() {
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(getHeader())
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.setActivityVisible(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.setActivityVisible(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        if (result == null)
            return;

        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    // endregion

    // region Behaviour

    private void loadAccount(final Bundle savedInstanceState, String email) {
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser(email)
                .withProfileFrom(getContentResolver());

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(final Void... params) {
                return ParseEndPoint.isEmailVerified(authenticatedUser.getEmail());
            }

            @Override
            protected void onPostExecute(final Boolean result) {
//                authenticatedUser.emailVerified = result;

                if (!authenticatedUser.isEmailVerified()) {

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage(getResources().getString(R.string.parser_email_not_verified));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();

                } else {
                    loadUser(savedInstanceState, authenticatedUser);
                    loadNotifications();
                }
            }
        }.execute();
    }

    private void loadUser(final Bundle savedInstanceState, final AuthenticatedUser user) {
        if (user == null)
            return;


        Task task = new Task<Bitmap>() {
            @Override
            public void run() {
                IProfile[] profiles = getProfiles(accounts);
                IProfile profile = null;
                for (IProfile p : profiles) {
                    if (p.getName().equals(user.name)) {
                        p.withIcon(this.res);
                        profile = p;
                        break;
                    }
                }
                loadDrawerMenu(savedInstanceState, user);
                headerResult.addProfile(profile, 0);
                headerResult.addProfile(new ProfileSettingDrawerItem().withName(getResources().getString(R.string.drawer_addaccount)).withIcon(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_add)
                        .actionBarSize()
                        .paddingDp(5)
                        .colorRes(R.color.material_drawer_primary_text))
                        .withIdentifier(Constants.Activities.Main.DRAWER_ADD_ACCOUNT), 1);
                headerResult.setActiveProfile(profile);
            }
        };
        if (TextUtils.isEmpty(user.avatarUrl)) {
            IProfile[] profiles = getProfiles(accounts);
            IProfile profile = null;
            for (IProfile p : profiles) {
                if (p.getName().equals(user.name)) {
                    profile = p;
                    break;
                }
            }
            loadDrawerMenu(savedInstanceState, user);
            headerResult.addProfile(profile, 0);
            headerResult.setActiveProfile(profile);
        } else {
            task.url = user.avatarUrl;
            TeacherLocatorApplication.sIOThread.queueImageRead(task);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PREFERENCES_REQUEST_CODE:
                TeacherLocatorApplication.sIOThread.resizeCache(Preferences.getDiskCacheSize(getBaseContext()),
                        Preferences.getMemoryCacheSize(getBaseContext()));
                updateMap();
                mGeofenceStore.updatePriority();
                break;
            case SETACCOUNT_REQUEST_CODE:
                if (resultCode != RESULT_OK) {
                    finish();
                    return;
                }
                String name = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                String accountType = data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
                AccountManager am = AccountManager.get(this);
                accounts = am.getAccountsByType(accountType);
                switch (accounts.length) {
                    case 0:
                        Intent intent = am.newChooseAccountIntent(null, null, new String[]{Constants.Parse.Keys.PARSE_ACCOUNT_TYPE}, false, null, null, null, null);
                        startActivityForResult(intent, 0);
                        break;
                    case 1:
                        account = accounts[0];
                        break;
                    default:
                        for (Account acct : accounts)
                            if (acct.name.equals(name)) {
                                account = acct;
                                break;
                            }
                }
                if (account != null) {
                    Account lastAccount = Preferences.getLastAccount(this);
                    if (!lastAccount.name.equals(account.name) || !lastAccount.type.equals(account.type)) {
                        Preferences.setLastAccount(this, account);
                        clearParseProviderData();
                    }
                    ContentResolver.setIsSyncable(account, ParseContract.AUTHORITY, 1);
                    ContentResolver.setSyncAutomatically(account, ParseContract.AUTHORITY, true);
                    ParseUser parseUser = ParseUser.getCurrentUser();
                    try {
                        if (parseUser == null) {
                            ParseUser.logIn(account.name, AccountManager.get(this).getPassword(account));
                            parseUser = ParseUser.getCurrentUser();
                        }
                        if (!parseUser.getUsername().equals(account.name)) {
                            ParseUser.logOut();
                            invalidateAuthToken(account, Constants.Parse.Keys.PARSE_ACCOUNT_TYPE);
                            ParseUser.logIn(account.name, AccountManager.get(this).getPassword(account));
                        }
                    } catch (ParseException e) {
                        Logger.e(e);
                    }
                    loadAccount(Bundle.EMPTY, account.name);
                }

                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    private void invalidateAuthToken(final Account account, String authTokenType) {
        final AccountManager am = AccountManager.get(this);
        final AccountManagerFuture<Bundle> future = am.get(this)
                .getAuthToken(account, authTokenType, null, this, null, null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bundle = future.getResult();
                    final String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    am.invalidateAuthToken(account.type, token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void clearParseProviderData() {
        ContentResolver cr = getContentResolver();
        cr.delete(ParseContract.Locations.CONTENT_URI, null, null);
        cr.delete(ParseContract.Notifications.CONTENT_URI, null, null);
        cr.delete(ParseContract.Subscriptions.CONTENT_URI, null, null);
    }

    private void loadNotifications() {
        ViewGroup parent = (ViewGroup) swipeRecyclerView.getParent();
        View emptyView = getLayoutInflater().inflate(R.layout.notifications_not_found, null);
        parent.addView(emptyView);
        swipeRecyclerView.setEmptyView(emptyView);
        mLayoutManager = new LinearLayoutManager(this);
        swipeRecyclerView.setLayoutManager(mLayoutManager);
        swipeRecyclerView.setSwipeMode(SwipeRecyclerView.SWIPE_MODE_BOTH);

        swipeRecyclerView.setSwipeRecyclerListener(new BaseSwipeRecyclerListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));
                Notification notification = mNotifications.get(position);
                if (TWO_PANE) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(notification.getCoordinates()));
                } else {
                    Utils.openMap(MainActivity.this, notification);
                }
            }

            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                /*for (int position : reverseSortedPositions) {
                    mNotifications.remove(position);
                }
                adapter.notifyDataSetChanged();*/
            }

        });

    }

    private IProfile[] getProfiles(Account[] accounts) {
        Set<IProfile> profiles = new HashSet<>();
        if (accounts != null) {
            for (Account a : accounts) {
                AuthenticatedUser user = new AuthenticatedUser(a.name)
                        .withProfileFrom(getContentResolver());
                profiles.add(new ProfileDrawerItem().withName(user.name).withEmail(user.getEmail()).withIcon(getResources().getDrawable(R.drawable.profile)));
            }
        }
        return profiles.toArray(new IProfile[0]);
    }

    private void loadDrawerMenu(Bundle savedInstanceState, final AuthenticatedUser user) {

        // Create the AccountHeader
        if (headerResult != null) {
            headerResult.clear();
        }
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(getHeader())
                .withSavedInstance(savedInstanceState)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == Constants.Activities.Main.DRAWER_ADD_ACCOUNT) {
                             AccountManager am = AccountManager.get(MainActivity.this);
                            Intent intent = am.newChooseAccountIntent(account, null,
                                    new String[]{Constants.Parse.Keys.PARSE_ACCOUNT_TYPE},
                                    true, null, null, null, null);
                            startActivityForResult(intent, SETACCOUNT_REQUEST_CODE);
                        }
                        return false;
                    }
                })
                .build();
        if (result != null) {
            result.removeAllItems();
        }
        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .withDrawerItems(getDrawerItems(user))
                        // add the items we want to use with our Drawer*/
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem

                        if (drawerItem != null) {
                            Intent intent = null;
                            switch (drawerItem.getIdentifier()) {
                                case Constants.Activities.Main.DRAWER_MAPS:
                                    Utils.openMap(MainActivity.this, mNotifications);
                                    break;
                                case Constants.Activities.Main.DRAWER_TEACHERS:
                                    intent = new Intent(MainActivity.this, TeachersActivity.class);
                                    break;
                                case Constants.Activities.Main.DRAWER_SETTINGS:
                                    startActivityForResult(new Intent(MainActivity.this, PreferencesActivity.class), PREFERENCES_REQUEST_CODE);
                                    break;
                                case Constants.Activities.Main.DRAWER_ABOUT:
                                    intent = new LibsBuilder()
                                            .withLicenseShown(true)
                                            .withVersionShown(true)
                                            .withActivityTitle(getResources().getString(R.string.app_name))
                                            .withAboutIconShown(true)
                                            .withAboutVersionShown(true)
                                            .withFields(R.string.class.getFields())
                                            .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                            .intent(MainActivity.this);
                                    break;

                                default:
                                    break;
                            }

                            if (intent != null) {
                                startActivity(intent);
                            }
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();
        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelectionByIdentifier(1, false);
        }
    }

    // endregion

    //region Private Methods

    @NonNull
    private ArrayList<IDrawerItem> getDrawerItems(AuthenticatedUser user) {
        ArrayList<IDrawerItem> drawerItems = new ArrayList();
        drawerItems.add(new PrimaryDrawerItem().withName(R.string.drawer_item_notifications).withIcon(R.drawable.ic_action_person).withIdentifier(Constants.Activities.Main.DRAWER_NOTIFICATIONS).withCheckable(false));
        if (!TWO_PANE) {
            drawerItems.add(new PrimaryDrawerItem().withName(R.string.drawer_item_map).withIcon(FontAwesome.Icon.faw_map_marker).withIdentifier(Constants.Activities.Main.DRAWER_MAPS).withCheckable(false));
        }
        drawerItems.add(new DividerDrawerItem());
        drawerItems.add(new PrimaryDrawerItem().withName(R.string.drawer_item_teachers).withIcon(FontAwesome.Icon.faw_institution).withIdentifier(Constants.Activities.Main.DRAWER_TEACHERS).withCheckable(false));
        drawerItems.add(new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(R.drawable.ic_action_settings).withIdentifier(Constants.Activities.Main.DRAWER_SETTINGS).withCheckable(false));
        if (user.type == Constants.Thoth.UserTypes.TEACHER) {
            drawerItems.add(new SwitchDrawerItem().withName(R.string.drawer_item_sharelocation).withIcon(R.drawable.ic_action_location_found).withIdentifier(Constants.Activities.Main.DRAWER_SHARE).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener));
            mGeofenceStore.connect();
        }
        drawerItems.add(new DividerDrawerItem());
        drawerItems.add(new PrimaryDrawerItem().withName(R.string.drawer_item_about).withIcon(R.drawable.ic_action_about).withIdentifier(Constants.Activities.Main.DRAWER_ABOUT).withCheckable(false));
        return drawerItems;
    }

    private int getHeader() {
        int headerBackground;
        switch (generator.nextInt(6)) {
            case 0:
                headerBackground = R.drawable.header1;
                break;
            case 1:
                headerBackground = R.drawable.header2;
                break;
            case 2:
                headerBackground = R.drawable.header3;
                break;
            case 3:
                headerBackground = R.drawable.header4;
                break;
            case 4:
                headerBackground = R.drawable.header5;
                break;
            default:
                headerBackground = R.drawable.header6;
                break;
        }
        return headerBackground;
    }

    private boolean setAccount() {
        AccountManager am = AccountManager.get(this);
        accounts = am.getAccountsByType(Constants.Parse.Keys.PARSE_ACCOUNT_TYPE);
        if (accounts.length == 1) {
            account = accounts[0];
            ContentResolver.setIsSyncable(account, ParseContract.AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, ParseContract.AUTHORITY, true);
            ParseUser parseUser = ParseUser.getCurrentUser();
            try {
                if (parseUser == null) {
                    ParseUser.logIn(account.name, AccountManager.get(this).getPassword(account));
                    parseUser = ParseUser.getCurrentUser();
                }
                if (!parseUser.getUsername().equals(account.name)) {
                    ParseUser.logOut();
                    invalidateAuthToken(account, Constants.Parse.Keys.PARSE_ACCOUNT_TYPE);
                    ParseUser.logIn(account.name, AccountManager.get(this).getPassword(account));
                }
            } catch (ParseException e) {
                Logger.e(e);
            }

            return true;
        } else {
            Intent intent = am.newChooseAccountIntent(null, null,
                    new String[]{Constants.Parse.Keys.PARSE_ACCOUNT_TYPE},
                    false, null, null, null, null);
            startActivityForResult(intent, SETACCOUNT_REQUEST_CODE);
        }
        return false;
    }

    // endregion

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
            case Constants.Parse.Cursors.NOTIFICATIONS_LOADER:
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date()); // sets calendar time/date
                cal.add(Calendar.HOUR_OF_DAY, -12);
                long time = cal.getTimeInMillis();
                return new CursorLoader(this, ParseContract.Notifications.CONTENT_URI,
                        ParseContract.Notifications.PROJECTION_ALL, ParseContract.Notifications.TIMESTAMP + "> ?", new String[]{String.valueOf(time)}, null);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAdapter == null) {
            mAdapter = new NotificationsAdapter(swipeRecyclerView, data);
        } else {
            mAdapter.swapCursor(data);
            mNotifications.clear();
        }
        swipeRecyclerView.setAdapter(mAdapter);
        if (data.moveToFirst()) {
            mNotifications.clear();
            do {
                mNotifications.add(new Notification(data));
            } while (data.moveToNext());
        }
        if (TWO_PANE) {
            updateMap();
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        mNotifications.clear();
    }

    // endregion

    // region Camera CallBacks

    @Override
    public void onCameraChange(CameraPosition position) {
        // Makes sure the visuals remain when zoom changes.
        showGeofences();
    }


    // endregion

    // region Private Methods


    private void showGeofences() {
        if (!TWO_PANE || !Preferences.showGeofences(this))
            return;

        for (SimpleGeofence g : IselGeofences.getSimpleGeofences()) {
            mMap.addCircle(new CircleOptions().center(g.getLatLng())
                    .radius(g.getRadius())
                    .fillColor(Color.parseColor(g.getId()))
                    .strokeColor(Color.TRANSPARENT)
                    .strokeWidth(2));
        }

    }


    private void updateMap() {
        if (!TWO_PANE)
            return;

        mMap.clear();
        showGeofences();
        Utils.showMarkers(this, mMap, mNotifications);
    }

    //endregion

}

