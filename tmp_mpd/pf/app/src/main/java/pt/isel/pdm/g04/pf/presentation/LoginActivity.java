package pt.isel.pdm.g04.pf.presentation;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.isel.pdm.g04.pf.R;
import pt.isel.pdm.g04.pf.data.AuthenticatedUser;
import pt.isel.pdm.g04.pf.data.Notification;
import pt.isel.pdm.g04.pf.data.parse.localhelpers.ParseEndPoint;
import pt.isel.pdm.g04.pf.data.thoth.provider.ThothContract;
import pt.isel.pdm.g04.pf.helpers.Constants;
import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.helpers.Utils;

public class LoginActivity extends AccountAuthenticatorActivity implements LoaderCallbacks<Cursor> {

    private List<String> mAutocompleteEmails;
    private UserLoginTask userLoginTask = null;
    private View loginFormView;
    private View progressView;
    private AutoCompleteTextView emailTextView;
    private EditText passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
        emailTextView = (AutoCompleteTextView) findViewById(R.id.email);
        passwordTextView = (EditText) findViewById(R.id.password);
        passwordTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL) {
                    initLogin();
                    return true;
                }
                return false;
            }
        });

        String email = getIntent().getStringExtra(Constants.Activities.USER_EXTRA);
        emailTextView.setText(email);

        loadAutoComplete();

        Button loginButton = (Button) findViewById(R.id.email_sign_in_button);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                initLogin();
            }
        });
    }

    private void loadAutoComplete() {
        mAutocompleteEmails = Collections.synchronizedList(new ArrayList<String>());
        getLoaderManager().initLoader(Constants.Thoth.Cursors.STUDENTS_LOADER, null, this);
        getLoaderManager().initLoader(Constants.Thoth.Cursors.TEACHERS_LOADER, null, this);
    }


    /**
     * Validate Login form and authenticate.
     */
    public void initLogin() {
        if (userLoginTask != null) {
            return;
        }

        emailTextView.setError(null);
        passwordTextView.setError(null);

        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        boolean cancelLogin = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordTextView.setError(getString(R.string.invalid_password));
            focusView = passwordTextView;
            cancelLogin = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailTextView.setError(getString(R.string.field_required));
            focusView = emailTextView;
            cancelLogin = true;
        } else if (!isEmailValid(email)) {
            emailTextView.setError(getString(R.string.invalid_email));
            focusView = emailTextView;
            cancelLogin = true;
        }

        if (cancelLogin) {
            // error in login
            focusView.requestFocus();
        } else {
            // show progress spinner, and start background task to login
            showProgress(true);
            userLoginTask = new UserLoginTask(email, password, Utils.coalesce(this.getIntent().getStringExtra(Constants.Activities.AUTH_TOKEN_TYPE_EXTRA), Constants.Parse.Keys.PARSE_ACCOUNT_TYPE));
            userLoginTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        boolean validDomain = false;
        for (String d : Constants.Thoth.VALID_EMAIL_SUFFIXES.split(";")) {
            if (email.endsWith(d)) {
                validDomain = true;
                break;
            }
        }
        return email.contains("@") && validDomain;
    }

    private boolean isPasswordValid(String password) {
        //add your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


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
        String[] columns = {ThothContract.Users.ACADEMIC_EMAIL};
        switch (id) {
            case Constants.Thoth.Cursors.TEACHERS_LOADER:
                return new CursorLoader(this, ThothContract.Teachers.CONTENT_URI,
                        columns, null, null, ThothContract.Users.EMAIL_SORT_ORDER);
            case Constants.Thoth.Cursors.STUDENTS_LOADER:
                return new CursorLoader(this, ThothContract.Students.CONTENT_URI, columns
                        , null, null, ThothContract.Users.EMAIL_SORT_ORDER);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        int colIndex = cursor.getColumnIndex(ThothContract.Users.ACADEMIC_EMAIL);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                mAutocompleteEmails.add(cursor.getString(colIndex));
                cursor.moveToNext();
            }

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(LoginActivity.this,
                            android.R.layout.simple_dropdown_item_1line, mAutocompleteEmails.toArray(new String[0]));

            if (mAutocompleteEmails.size() > 0)
                emailTextView.setAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    //endregion

    /**
     * Async Login Task to authenticate
     */
    private class UserLoginTask extends AsyncTask<Void, Void, AuthenticatedUser> {


        private final String emailStr;
        private final String passwordStr;
        private final String mTokenType;

        UserLoginTask(String email, String password, String tokenType) {
            emailStr = email;
            passwordStr = password;
            mTokenType = tokenType;
        }

        @Override
        protected AuthenticatedUser doInBackground(Void... params) {

            AuthenticatedUser authenticatedUser = new AuthenticatedUser(emailStr)
                    .withProfileFrom(getContentResolver());
            authenticatedUser.password = passwordStr;
            authenticatedUser.tokenType = mTokenType;

            try {
                boolean userExists = ParseEndPoint.existsUser(emailStr);
                authenticatedUser.token = userExists ? // If user exists login, otherwise sign up
                        ParseEndPoint.logIn(emailStr, passwordStr).getSessionToken() :
                        ParseEndPoint.signUp(emailStr, passwordStr, authenticatedUser.type).getSessionToken();


            } catch (ParseException e) {
                authenticatedUser.lastError = e;
            }


            return authenticatedUser;
        }


        @Override
        protected void onPostExecute(final AuthenticatedUser authenticatedUser) {
            userLoginTask = null;
            //stop the progress spinner
            showProgress(false);

            if (authenticatedUser != null) {


                final Intent intent = getIntent();
                String toastMsg;
                if (authenticatedUser.lastError != null) {
                    intent.putExtra(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_REMOTE_EXCEPTION);
                    intent.putExtra(AccountManager.KEY_ERROR_MESSAGE, getString(R.string.account_setup_error));
                    Logger.e(authenticatedUser.lastError.getMessage(), authenticatedUser.lastError);
                    toastMsg = getString(R.string.account_setup_failed);
                } else {
                    Bundle accountExtras = new Bundle();
                    Account account = new Account(authenticatedUser.getEmail(), authenticatedUser.tokenType);
                    AccountManager.get(LoginActivity.this)
                            .addAccountExplicitly(account, authenticatedUser.password, accountExtras);
                    toastMsg = getString(R.string.account_setup_complete);
                }
                Utils.longToast(LoginActivity.this, toastMsg);
                setAccountAuthenticatorResult(intent.getExtras());
                setResult(RESULT_OK, intent);
                finish();

            } else {
                // login failure
                passwordTextView.setError(getString(R.string.incorrect_password));
                passwordTextView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            userLoginTask = null;
            showProgress(false);
        }
    }

}