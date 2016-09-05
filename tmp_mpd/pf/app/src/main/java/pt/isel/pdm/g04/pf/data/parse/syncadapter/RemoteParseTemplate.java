package pt.isel.pdm.g04.pf.data.parse.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.parse.ParseException;
import com.parse.ParseUser;

import pt.isel.pdm.g04.pf.helpers.Constants;
import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.helpers.Utils;

/**
 * Project pf, created on 8/23/2015.
 */
public class RemoteParseTemplate {
//    protected abstract String getParseClass();

    // region public

    // endregion

    // region protected

    protected ParseUser me() {
        return ParseUser.getCurrentUser();
    }

    protected void login(Context ctx) {
        ParseUser me = ParseUser.getCurrentUser();
        if (me == null || !me.isAuthenticated()) {
            AccountManager am = AccountManager.get(ctx);
            Account[] accounts = am.getAccountsByType(Constants.Parse.Keys.PARSE_ACCOUNT_TYPE);
            try {
                if (accounts.length > 0) ParseUser.logIn(accounts[0].name, am.getPassword(accounts[0]));
            } catch (ParseException e) {
                Logger.e("Could not login in parse", e);
            }
        }
    }

    protected boolean isStudentOk(Context ctx) throws ParseException {
        ParseUser me = ParseUser.getCurrentUser();
        login(ctx);
        return me != null
                && me.isAuthenticated()
                && !isTeacher()
                && isEmailVerified();
    }

    protected boolean isTeacherOk(Context ctx) throws ParseException {
        ParseUser me = ParseUser.getCurrentUser();
        login(ctx);
        return me != null
                && me.isAuthenticated()
                && isTeacher()
                && isEmailVerified();
    }

    protected boolean isTeacher() throws ParseException {
        return is("isTeacher");
    }

    protected boolean isEmailVerified() throws ParseException {
        return is("emailVerified");
    }

    // endregion

    // region Private

    private boolean is(String what) throws ParseException {
        Utils.assertNotOnUIThread();
        ParseUser user = ParseUser.getQuery()
                .whereEqualTo("objectId",
                        ParseUser.getCurrentUser().getObjectId())
                .getFirst();
        return user.fetchIfNeeded().getBoolean(what);
    }

    // endregion
}
