package pt.isel.pdm.g04.pf;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import pt.isel.pdm.g04.pf.data.parse.classes.Subscription;
import pt.isel.pdm.g04.pf.data.thoth.provider.ThothContract;
import pt.isel.pdm.g04.pf.helpers.Constants;
import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.helpers.Preferences;
import pt.isel.pdm.g04.pf.helpers.Utils;
import pt.isel.pdm.g04.pf.workers.DownloadThread;
import pt.isel.pdm.g04.pf.workers.IOThread;

public class TeacherLocatorApplication extends Application {

    private static Context context;

    private Account mAccount;

    public static DownloadThread sDownloadThread;
    public static IOThread sIOThread;


    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i("Starting App...");
        context = getApplicationContext();

        if (createSyncAccount(this)) {
            Logger.i("Sync Account created.");
            requestImmediateSync();
        }

        initializeParse();
        startWorkers();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void initializeParse() {
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Subscription.class);
//        ParseObject.registerSubclass(Location.class);
        Parse.initialize(this, Constants.Parse.Keys.APPLICATION_ID, Constants.Parse.Keys.CLIENT_KEY);
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
//        parseInstallation.saveInBackground();

        ParseACL parseACL = new ParseACL();
        parseACL.setPublicReadAccess(true); // Comment this line for the objects to be private by default.
        ParseACL.setDefaultACL(parseACL, true);

//        PushService.setDefaultPushCallback(this, MainActivity.class);
        parseInstallation.saveEventually();

    }

    private void startWorkers() {
        synchronized (TeacherLocatorApplication.class) {

            if (TeacherLocatorApplication.sDownloadThread == null) {
                TeacherLocatorApplication.sDownloadThread = new DownloadThread();
                TeacherLocatorApplication.sDownloadThread.start();
                TeacherLocatorApplication.sDownloadThread.prepareHandler();
            }

            if (TeacherLocatorApplication.sIOThread == null) {
                TeacherLocatorApplication.sIOThread = new IOThread(Utils.getCacheDir(this),
                        Preferences.getDiskCacheSize(getBaseContext()),
                        Preferences.getMemoryCacheSize(getBaseContext()));
                TeacherLocatorApplication.sIOThread.start();
                TeacherLocatorApplication.sIOThread.prepareHandler();
            }
        }
    }

    /**
     * Helper method to trigger an immediate sync ("refresh").
     * <p/>
     * <p>This should only be used when we need to preempt the normal sync schedule. Typically, this
     * means the user has pressed the "refresh" button.
     * <p/>
     * Note that SYNC_EXTRAS_MANUAL will cause an immediate sync, without any optimization to
     * preserve battery life. If you know new data is available (perhaps via a GCM notification),
     * but the user is not actively waiting for that data, you should omit this flag; this will give
     * the OS additional freedom in scheduling your sync request.
     */
    private void requestImmediateSync() {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(mAccount, ThothContract.AUTHORITY, settingsBundle);
    }

    /**
     * Create an entry for this application in the system account list, if it isn't already there.
     *
     * @param context Context
     */
    private boolean createSyncAccount(Context context) {
        boolean newAccount = false;
        boolean setupComplete = Preferences.isSetupComplete(context);

        // Create account, if it's missing. (Either first run, or user has deleted account.)
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(Constants.SyncAdaptor.ACCOUNT_TYPE);

        if (accounts.length == 1) {
            mAccount = accounts[0];
            return false;
        }
        mAccount = new Account(Constants.SyncAdaptor.ACCOUNT_NAME, Constants.SyncAdaptor.ACCOUNT_TYPE);
        if (accountManager.addAccountExplicitly(mAccount, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(mAccount, ThothContract.AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(mAccount, ThothContract.AUTHORITY, true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(
                    mAccount, ThothContract.AUTHORITY, Bundle.EMPTY, Constants.SyncAdaptor.SYNC_FREQUENCY);
            newAccount = true;
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            requestImmediateSync();
            Preferences.setSetupComplete(context);
        }

        return newAccount;
    }

}
