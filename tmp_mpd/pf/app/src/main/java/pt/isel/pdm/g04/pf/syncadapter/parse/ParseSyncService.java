package pt.isel.pdm.g04.pf.syncadapter.parse;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import pt.isel.pdm.g04.pf.helpers.Logger;

public class ParseSyncService extends Service {
    private static final String CLASS_NAME = "ParseSyncService";

    private static ParseSyncAdapter sSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    public ParseSyncService() {
        super();
    }

    // region Life Cycle

    @Override
    public void onCreate() {
        super.onCreate();
        lc("onCreate");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) sSyncAdapter = new ParseSyncAdapter(getApplicationContext(), true);
        }
    }

    // endregion

    // region Behaviour

    @Override
    public IBinder onBind(Intent intent) {
        lc("onBind");
        return sSyncAdapter.getSyncAdapterBinder();
    }

    // endregion

    // region Private

    private void lc(String methodName) {
        Logger.c(CLASS_NAME, methodName);
    }

    // endregion
}
