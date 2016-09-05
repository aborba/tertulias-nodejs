package pt.isel.pdm.g04.pf.syncadapter.thoth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * A bound Service that instantiates the authenticator
 * when started.
 */
public class ThothAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private ThothAuthenticator mThothAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mThothAuthenticator = new ThothAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mThothAuthenticator.getIBinder();
    }
}