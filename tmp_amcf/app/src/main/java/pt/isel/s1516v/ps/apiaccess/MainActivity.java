package pt.isel.s1516v.ps.apiaccess;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final static String SERVER_URL = "https://tertulias.azurewebsites.net";

    private static final String STATE_ACCOUNT_BUNDLE = "account";
    private static final int INTENTID_SETACCOUNT = 1001;
    private static final String ACCOUNT_NAME_STRING = "accountName";
    private static final String ACCOUNT_TYPE_STRING = "accountType";

    private Account account;

    AccountManager accountManager;
    String accountType, accountTokenType;
    Account[] accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileServiceUser user = loadMobileServiceUser(this);
        if (user != null) {
            MobileServiceClient cli = StaticUtil.getMobileServiceClient(this);
            cli.setCurrentUser(user);
        } else {
            account = tryGetAccountBundle(savedInstanceState, STATE_ACCOUNT_BUNDLE);
            if (account == null)
                account = tryGetAccountAccountManager();
            if (account != null)
                retrieveCredentialsAndSetMobileServiceClient();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case INTENTID_SETACCOUNT:
                if (requestCode != RESULT_OK)
                    return;
                String authTokenFakeJson = data.getStringExtra(AuthenticatorActivity.INTENT_RES_AUTH_TOKEN_FAKE);
                AuthTokenFake authTokenFake = new Gson().fromJson(authTokenFakeJson, AuthTokenFake.class);
                MobileServiceClient cli = StaticUtil.getMobileServiceClient(this);
                MobileServiceUser user = new MobileServiceUser(authTokenFake.userSid);
                user.setAuthenticationToken(authTokenFake.token);
                cli.setCurrentUser(user);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (account != null) {
            Bundle bundle = new Bundle();
            bundle.putString(ACCOUNT_NAME_STRING, account.name);
            bundle.putString(ACCOUNT_TYPE_STRING, accountType);
            outState.putBundle(STATE_ACCOUNT_BUNDLE, bundle);
        }
        super.onSaveInstanceState(outState);
    }

    private static Account tryGetAccountBundle(Bundle inState, String accountKey) {
        if ( inState == null || ! inState.containsKey(accountKey))
            return null;
        Bundle accountBundle = inState.getBundle(accountKey);
        String accountName = accountBundle.getString(ACCOUNT_NAME_STRING);
        String accountType = accountBundle.getString(ACCOUNT_TYPE_STRING);
        Account account = new Account(accountName, accountType);
        return account;
    }

    private Account tryGetAccountAccountManager() {
        AccountManager accountManager = AccountManager.get(this);
        String accountType = getAccountType();
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length > 0)
            return accounts[0];
        Intent intent = accountManager.newChooseAccountIntent(null, null, new String[]{ accountType }, false, null, null, null, null);
        startActivityForResult(intent, INTENTID_SETACCOUNT);
        return null;
    }

    private void retrieveCredentialsAndSetMobileServiceClient() {
        String authTokenType = getString(R.string.authenticator_token_type_default);
        AccountManager.get(this).getAuthToken(account, authTokenType, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                try {
                    Bundle result = accountManagerFuture.getResult();
                    String accountName = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                    String accountType = result.getString(AccountManager.KEY_ACCOUNT_TYPE);
                    String authTokenFakeJson = result.getString(AccountManager.KEY_AUTHTOKEN);
                    AuthTokenFake authTokenFake = new Gson().fromJson(authTokenFakeJson, AuthTokenFake.class);
                    setMobileServiceClient(MainActivity.this, authTokenFake.userSid, authTokenFake.token);
                } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }

    private void setMobileServiceClient(final Context ctx, String userId, String token) {
        MobileServiceClient cli = StaticUtil.getMobileServiceClient(this);
        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        cli.setCurrentUser(user);
//        JsonObject loginBody = new JsonObject();
//        loginBody.addProperty("id_token", idToken);
//        loginBody.addProperty("authorization_code", authToken);
//        MobileServiceClient cli = StaticUtil.getMobileServiceClient(ctx);
//        cli.login(MobileServiceAuthenticationProvider.Google, loginBody, new UserAuthenticationCallback() {
//            @Override
//            public void onCompleted(MobileServiceUser user, Exception error, ServiceFilterResponse response) {
//                if (error != null) {
//                    Log.e("error", "Login error: " + error);
//                    return;
//                }
//                saveMobileServiceUser(ctx, user);
//                Log.d("msg", "Logged in to the mobile service as " + user.getUserId());
//            }
//        });
    }

    private void saveMobileServiceUser(Context ctx, MobileServiceUser user) {
        SharedPreferences sharedPref = ctx.getSharedPreferences("AZURE", Context.MODE_PRIVATE);
        sharedPref.edit()
                .putString("userSid", user.getUserId())
                .putString("token", user.getAuthenticationToken())
                .commit();
    }

    private MobileServiceUser loadMobileServiceUser(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences("AZURE", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userSid", null);
        if (TextUtils.isEmpty(userId))
            return null;
        String token = sharedPref.getString("token", null);
        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        return user;
    }

    private String getAccountType() {
        if (accountType == null)
            accountType = getString(R.string.authenticator_account_type);
        return accountType;
    }
}
