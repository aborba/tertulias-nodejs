package pt.isel.s1516v.ps.apiaccess;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.io.IOException;

public class AuthenticatorActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    public static final String TAG = "Trt";

    public static final String INTENT_ACCOUNT_NAME = "ACCOUNT_NAME";
    public static final String INTENT_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public static final String INTENT_AUTHENTICATION_TYPE = "AUTHENTICATION_TYPE";
    public static final String INTENT_IS_NEW_ACCOUNT = "IS_NEW_ACCOUNT";
    public static final String INTENT_RES_AUTH_TOKEN_FAKE = "AUTH_TOKEN_FAKE";

    private static final int INTENTID_PICK_ACCOUNT = 8001;
    private static final int INTENTID_SIGN_IN = 9001;
    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 10001;

    private SignInButton sign_in_button;
    private Button sign_out_button, disconnect_button;
    private LinearLayout sign_out_and_disconnect_layout;

    private AccountManager accountManager, accountManager2;
    private GoogleApiClient googleApiClient;
    private String accountName;
    private String accountType;
    private String authType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        accountManager = AccountManager.get(getBaseContext() /* this */);
        accountName = getIntent().getStringExtra(INTENT_ACCOUNT_NAME);
        accountType = getIntent().getStringExtra(INTENT_ACCOUNT_TYPE);
        authType = getIntent().getStringExtra(INTENT_AUTHENTICATION_TYPE);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        sign_in_button = (SignInButton) findViewById(R.id.sign_in_button);
        sign_out_button = (Button) findViewById(R.id.sign_out_button);
        disconnect_button = (Button) findViewById(R.id.disconnect_button);
        sign_out_and_disconnect_layout = (LinearLayout) findViewById(R.id.sign_out_and_disconnect);

        sign_in_button.setOnClickListener(this);
        sign_out_button.setOnClickListener(this);
        disconnect_button.setOnClickListener(this);

        sign_in_button.setSize(SignInButton.SIZE_STANDARD);
        sign_in_button.setScopes(googleSignInOptions.getScopeArray());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR:
                handlePlayServicesError(resultCode);
                break;
            case INTENTID_SIGN_IN:
                boolean isNewAccount = getIntent().getBooleanExtra(INTENT_IS_NEW_ACCOUNT, false);
                handleSignInResult(data, isNewAccount);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void handlePlayServicesError(int resultCode) {
        if (resultCode == RESULT_OK) {
            getTokenAndLogin(accountName, accountType);
        } else {
            Toast.makeText(this, "Some error in the process...", Toast.LENGTH_SHORT);
        }
    }

    private void getTokenAndLogin(String accountName, String accountType) {
        if ( ! isDeviceOnline()) {
            Toast.makeText(this, "Device is not online", Toast.LENGTH_LONG).show();
            return;
        }
        if (this.accountName == null) {
            pickUserAccount();
            return;
        }
        String clientId = getString(R.string.server_client_id);
        String googleIdTokenScope = "audience:server:client_id:" + clientId;
        new GetTokenAndLoginTask(this).execute(accountName, accountType, googleIdTokenScope);
    }

        private class GetTokenAndLoginTask extends AsyncTask<String, Void, String> {

            AuthenticatorActivity authenticatorActivity;

            public GetTokenAndLoginTask(AuthenticatorActivity authenticatorActivity) {
                this.authenticatorActivity = authenticatorActivity;
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    final String token = fetchIdToken(params);
                    return token;
                } catch (IOException e) {
                    Log.e(TAG, "Exception: " + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String token) {
                if (token != null)
                    loginToMobileService(token);
            }

            protected String fetchIdToken(String... params) throws IOException {
                if (params.length != 3)
                    return null;
                String accountName = params[0];
                String accountType = getString(R.string.authenticator_account_type) /* params[1] */;
                String scope = params[2];
                try {
                    Account account = new Account(accountName, accountType);
                    String token = GoogleAuthUtil.getToken(AuthenticatorActivity.this, account, scope);
                    return token;
                } catch (UserRecoverableAuthException urae) {
                    authenticatorActivity.handleException(urae);
                } catch (GoogleAuthException gae) {
                    Log.e(TAG, "Unrecoverable exception: " + gae);
                }
                return null;
            }

            protected void loginToMobileService(final String idToken) {
                JsonObject loginBody = new JsonObject();
                loginBody.addProperty("id_token", idToken);
                MobileServiceClient cli = StaticUtil.getMobileServiceClient(AuthenticatorActivity.this);
                cli.login(MobileServiceAuthenticationProvider.Google, loginBody, new UserAuthenticationCallback() {
                    @Override
                    public void onCompleted(MobileServiceUser user, Exception error, ServiceFilterResponse response) {
                        if (error != null) {
                            Log.e(TAG, "Login error: " + error);
                            return;
                        }
                        Log.d(TAG, "Logged in to the mobile service as " + user.getUserId());
                    }
                });
            }

        }

    private boolean isDeviceOnline() {
        ConnectivityManager mgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public void handleException(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    int statusCode = ((GooglePlayServicesAvailabilityException)e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            AuthenticatorActivity.this, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    Intent intent = ((UserRecoverableAuthException)e).getIntent();
                    startActivityForResult(intent, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }

    private void handleSignInResult(Intent intent, boolean isNewAccount) {
        if (intent == null)
            return;
        final GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);

        final boolean isSuccess = signInResult.isSuccess();
        Log.d(TAG, "handleSignInResult:" + isSuccess);
        if ( ! isSuccess) {
            updateUI(false);
            return;
        }
        updateUI(true);

        final GoogleSignInAccount signInAccount = signInResult.getSignInAccount();
        final String idToken = signInAccount.getIdToken();
        final String authToken = signInAccount.getServerAuthCode();
        final AccountManager accountManager = AccountManager.get(this);
        final UserInfo userInfo = new UserInfo(signInAccount);
        final String accountName = userInfo.displayName;
        final String accountType = getString(R.string.authenticator_account_type);
        final Account account = new Account(accountName, accountType);
        if (isNewAccount) {
            AccountInfo accountInfo = new AccountInfo(this, userInfo, authToken);
            accountManager.addAccountExplicitly(account, null, accountInfo.asBundle());
        }
        loginToMobileServiceAndSave(intent, account, idToken, authToken);
    }

    private void loginToMobileServiceAndSave(final Intent intent, final Account account, final String idToken, final String authToken) {
        JsonObject loginBody = new JsonObject();
        loginBody.addProperty("id_token", idToken);
        loginBody.addProperty("authorization_code", authToken);
        MobileServiceClient cli = StaticUtil.getMobileServiceClient(AuthenticatorActivity.this);
        Futures.addCallback(
                cli.login(MobileServiceAuthenticationProvider.Google, loginBody),
                new FutureCallback<MobileServiceUser>() {

                    @Override
                    public void onSuccess(MobileServiceUser user) {
                        String authtokenType = getString(R.string.authenticator_token_type_default);
                        String userId = user.getUserId();
                        String token = user.getAuthenticationToken();
                        AuthTokenFake authTokenFake = new AuthTokenFake(idToken, authToken, userId, token);
                        String authTokenFakeJson = authTokenFake.toJson();
                        accountManager.setAuthToken(account, authtokenType, authTokenFakeJson);
                        intent.putExtra(INTENT_RES_AUTH_TOKEN_FAKE, authTokenFakeJson);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG, "Login error: " + t.getMessage());
                    }
                }
        );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }

    private void signIn() {
        if (! StaticUtil.isGooglePlayServicesAvailable(this)) {
            Toast.makeText(this, "Google Play Services not available - Aborting", Toast.LENGTH_SHORT).show(); // TODO: Strings
            return;
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, INTENTID_SIGN_IN);
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[] { getString(R.string.authenticator_account_type) };
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null, null, null, null);
        startActivityForResult(intent, INTENTID_PICK_ACCOUNT);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new UpdateUiCallback(false));
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new UpdateUiCallback(false));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void updateUI(boolean isSignedIn) {
//        if (isSignedIn) {
//            sign_in_button.setVisibility(View.GONE);
//            sign_out_and_disconnect_layout.setVisibility(View.VISIBLE);
//        } else {
//            sign_in_button.setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
//        }
    }

        private class UpdateUiCallback implements ResultCallback<Status> {
            final boolean isSignedIn;

            public UpdateUiCallback(boolean isSignedIn) {
                this.isSignedIn = isSignedIn;
            }

            @Override
            public void onResult(Status status) {
                updateUI(isSignedIn);
            }
        }

    public void updateTextView(String value) {
        Toast.makeText(this, value, Toast.LENGTH_LONG);
    }
}
