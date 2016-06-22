package pt.isel.s1516v.ps.apiaccess;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.common.base.Predicate;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.util.concurrent.Callable;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.raw.RUser;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;

public class EditUserProfileActivity extends Activity {

    private RUser RUser;
    private EditText aliasVw, emailVw, firstNameVw, lastNameVw, pictureVw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        aliasVw = (EditText) findViewById(R.id.editProfileAliasPlaceholderVw);
        emailVw = (EditText) findViewById(R.id.editProfileEmailPlaceholderVw);
        firstNameVw = (EditText) findViewById(R.id.editProfileFirstNamePlaceholderVw);
        lastNameVw = (EditText) findViewById(R.id.editProfileLastNamePlaceholderVw);
        pictureVw = (EditText) findViewById(R.id.editProfilePicturePlaceholderVw);
        readUserProfile(this, new FillUi());
    }

    public void onClickSubmitEditProfile(View view) {
        Log.d("trt", "RUser Profile Edit in process");
        ScratchUi sui = new ScratchUi();
        writeUserProfile(this, sui);
        finish();
    }

    public void onClickCancelEditProfile(View view) {
        Log.d("trt", "RUser Profile Edit cancelled");
        Util.longToast(this, R.string.edit_user_profile_canceled_message);
        finish();
    }

    private class FillUi implements Predicate<RUser> {
        @Override
        public boolean apply(RUser RUser) {
            if (RUser == null) return false;
            if (aliasVw != null) aliasVw.setText(RUser.alias);
            if (emailVw != null) emailVw.setText(RUser.email);
            if (firstNameVw != null) firstNameVw.setText(RUser.firstName);
            if (lastNameVw != null) lastNameVw.setText(RUser.lastName);
            if (pictureVw != null) pictureVw.setText(RUser.picture);
            return true;
        }
    }

    private class ScratchUi implements Callable<RUser> {
        public ScratchUi() {
            super();
        }

        @Override
        public RUser call() throws Exception {
            RUser RUser = new RUser();
            if (aliasVw != null) RUser.alias = aliasVw.getText().toString();
            if (emailVw != null) RUser.email = emailVw.getText().toString();
            if (firstNameVw != null) RUser.firstName = firstNameVw.getText().toString();
            if (lastNameVw != null) RUser.lastName = lastNameVw.getText().toString();
            if (pictureVw != null) RUser.picture = pictureVw.getText().toString();
            return RUser;
        }
    }

    private void readUserProfile(final Context ctx, final Predicate<RUser> fillUi) {
        MobileServiceClient cli = Util.getMobileServiceClient(this);
        ListenableFuture<JsonElement> result = cli.invokeApi(TertuliasApi.USER_EDIT_API_END_POINT, null, "GET", null);

        Futures.addCallback(result, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable e) {
                Log.d("trt", "Edit RUser Profile - GET failed");
                Log.d("trt", e.getMessage());
                Util.longToast(ctx, R.string.edit_user_profile_remote_access_error_message);
            }

            @Override
            public void onSuccess(JsonElement result) {
                Log.d("trt", "Edit RUser Profile - GET succeeded");
                RUser[] RUsers = new Gson().fromJson(result, RUser[].class);
                if (RUsers.length > 0) fillUi.apply(RUsers[0]);
            }
        });
    }

    private void writeUserProfile(final Context ctx, Callable<RUser> scratchUi) {
        MobileServiceClient cli = Util.getMobileServiceClient(this);
        try {
            RUser RUser = scratchUi.call();
            JsonElement params = new Gson().toJsonTree(RUser);
            ListenableFuture<JsonElement> result = cli.invokeApi(TertuliasApi.USER_EDIT_API_END_POINT, params);

            Futures.addCallback(result, new FutureCallback<JsonElement>() {
                @Override
                public void onFailure(Throwable e) {
                    Log.d("trt", "Edit RUser Profile - POST failed");
                    Log.d("trt", e.getMessage());
                    Util.longToast(ctx, R.string.edit_user_profile_remote_access_error_message);
                }

                @Override
                public void onSuccess(JsonElement result) {
                    Log.d("trt", "Edit RUser Profile - POST succeeded");
                    Util.longToast(ctx, R.string.edit_user_profile_updated_message);
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
