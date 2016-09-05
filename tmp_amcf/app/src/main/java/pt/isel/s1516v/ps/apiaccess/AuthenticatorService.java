package pt.isel.s1516v.ps.apiaccess;

import android.accounts.AbstractAccountAuthenticator;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
//        if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
//            return null;
        AbstractAccountAuthenticator authenticator = new Authenticator(this);
        return authenticator.getIBinder();
    }
}
