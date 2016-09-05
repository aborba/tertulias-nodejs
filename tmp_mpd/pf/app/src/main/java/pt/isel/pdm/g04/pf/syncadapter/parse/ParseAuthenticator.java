package pt.isel.pdm.g04.pf.syncadapter.parse;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.parse.ParseException;
import com.parse.ParseUser;

import pt.isel.pdm.g04.pf.helpers.Constants;
import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.helpers.Utils;
import pt.isel.pdm.g04.pf.presentation.LoginActivity;

public class ParseAuthenticator extends AbstractAccountAuthenticator {
    private static final String CLASS_NAME = "ParseAuthenticator";
    private Context ctx;
    private ParseUser parseUser;

    public ParseAuthenticator(Context ctx) {
        super(ctx);
        this.ctx = ctx;
    }

    // region important methods

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
                             String accountType, String authTokenType,
                             String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        lc("addAccount");
        Logger.i("authTokenType: " + authTokenType);

        parseUser = ParseUser.getCurrentUser();

        final Intent intent = new Intent(ctx, LoginActivity.class);
        intent.putExtra(Constants.Activities.IS_NEW_ACCOUNT_EXTRA, true);
        intent.putExtra(Constants.Activities.ACCOUNT_TYPE_EXTRA, accountType);
        intent.putExtra(Constants.Activities.AUTH_TOKEN_TYPE_EXTRA, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    private static boolean matchAccountParseUser(Context ctx, Account account) {
        if (account == null) throw new RuntimeException();
        ParseUser parseUser = ParseUser.getCurrentUser();
        if (parseUser == null) return false;
        if (!account.name.equals(parseUser.getUsername())) {
            ParseUser.logOut();
            try {
                ParseUser.logIn(account.name, AccountManager.get(ctx).getPassword(account));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                               String authTokenType, Bundle options) throws NetworkErrorException {
        lc("getAuthToken");

        AccountManager am = AccountManager.get(ctx);
        String token = am.peekAuthToken(account, authTokenType);

        if (TextUtils.isEmpty(token)) { // No token? Try parseUser
            matchAccountParseUser(ctx, account);
            if (parseUser.isAuthenticated()) token = parseUser.getSessionToken();
        }

        if (TextUtils.isEmpty(token)) { // Still no token? Try logging in
            Logger.i("No token in account manager; Trying login...");
            String password = am.getPassword(account);
            if (!TextUtils.isEmpty(password)) {
                try {
                    Utils.assertNotOnUIThread();
                    parseUser = ParseUser.logIn(account.name, password);
                    token = parseUser.getSessionToken();
                } catch (ParseException e) {
                    Logger.e("ParseException occurred while trying to login", e);
                    switch (e.getCode()) {
                        case ParseException.OPERATION_FORBIDDEN:
                            break;
                        case ParseException.TIMEOUT:
                        case ParseException.CONNECTION_FAILED:
                            break;
                        case ParseException.MUST_CREATE_USER_THROUGH_SIGNUP:
                            break;
                    }
                }
            }
        }

        if (!TextUtils.isEmpty(token)) { // Got a token? Return the bundle
            Logger.i(String.format("Got a token for %s; Returning!", account.name));
            final Bundle bundle = new Bundle();
            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            bundle.putString(AccountManager.KEY_AUTHTOKEN, token);
            return bundle;
        }

        // Still no token? Return an intent for user registration activity
        Logger.i("Still no token; Trying new user registration activity.");
        final Intent intent = new Intent(ctx, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(Constants.Activities.ACCOUNT_TYPE_EXTRA, account.type);
        intent.putExtra(Constants.Activities.AUTH_TOKEN_TYPE_EXTRA, authTokenType);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    // endregion

    // region other methods

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        lc("editProperties");
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        lc("confirmCredentials");
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        lc("getAuthTokenLabel");
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        lc("updateCredentials");
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        lc("hasFeatures");
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    // endregion

    // region internal

    private static void lc(String methodname) {
        Logger.c(CLASS_NAME, methodname);
    }

    // endregion

}
