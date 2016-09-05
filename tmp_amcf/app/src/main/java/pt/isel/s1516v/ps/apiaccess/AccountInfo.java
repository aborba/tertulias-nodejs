package pt.isel.s1516v.ps.apiaccess;

import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;

public class AccountInfo {

    public final UserInfo userInfo;
    public final String authTokenType, authToken;

    public AccountInfo(UserInfo userInfo, String authTokenType, String authToken) {
        this.userInfo = userInfo;
        this.authTokenType = authTokenType;
        this.authToken = authToken;
    }

    public AccountInfo(Context ctx, UserInfo userInfo, String authToken) {
        this.userInfo = userInfo;
        this.authTokenType = ctx.getString(R.string.authenticator_token_type_default);
        this.authToken = authToken;
    }

    public Bundle asBundle() {
//        Bundle bundle = userInfo.asBundle();
        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, userInfo.displayName);
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, authTokenType);
        bundle.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        return bundle;
    }
}
