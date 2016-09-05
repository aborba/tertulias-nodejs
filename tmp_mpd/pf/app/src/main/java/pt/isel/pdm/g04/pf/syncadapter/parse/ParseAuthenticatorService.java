package pt.isel.pdm.g04.pf.syncadapter.parse;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import pt.isel.pdm.g04.pf.helpers.Logger;

public class ParseAuthenticatorService extends Service {
    private static final String CLASS_NAME = "ParseAuthenticatorService";

    private ParseAuthenticator mParseAuthenticator;

    public ParseAuthenticatorService() {
    }

    @Override
    public void onCreate() {
        mParseAuthenticator = new ParseAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        lc("onBind");
        return mParseAuthenticator.getIBinder();
    }

    // region Private

    private static void lc(String methodname) {
        Logger.c(CLASS_NAME, methodname);
    }

    // endregion

}
