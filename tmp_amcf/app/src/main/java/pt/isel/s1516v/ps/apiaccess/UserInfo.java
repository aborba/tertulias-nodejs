package pt.isel.s1516v.ps.apiaccess;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class UserInfo {
    public static final String DISPLAY_NAME = "displayName";
    public static final String FAMILY_NAME = "familyName";
    public static final String GIVEN_NAME = "givenName";
    public static final String EMAIL = "email";
    public static final String PHOTO_URI = "photoUri";

    public final String displayName;
    public final String familyName;
    public final String givenName;
    public final String email;
    public final Uri photoUri;

    public UserInfo(GoogleSignInAccount signInAccount) {
        displayName = signInAccount.getDisplayName();
        familyName = signInAccount.getFamilyName();
        givenName = signInAccount.getGivenName();
        email = signInAccount.getEmail();
        photoUri = signInAccount.getPhotoUrl();
    }

    public Bundle asBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(DISPLAY_NAME, displayName);
        bundle.putString(FAMILY_NAME, familyName);
        bundle.putString(GIVEN_NAME, givenName);
        bundle.putString(EMAIL, email);
        bundle.putParcelable(PHOTO_URI, photoUri);
        return bundle;
    }
}
