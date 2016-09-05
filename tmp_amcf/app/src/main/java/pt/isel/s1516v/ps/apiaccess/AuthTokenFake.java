package pt.isel.s1516v.ps.apiaccess;

import com.google.gson.Gson;

public class AuthTokenFake {

    @com.google.gson.annotations.SerializedName("id_token")
    public final String idToken;
    @com.google.gson.annotations.SerializedName("authorization_code")
    public final String authToken;
    @com.google.gson.annotations.SerializedName("userId")
    public final String userSid;
    @com.google.gson.annotations.SerializedName("token")
    public final String token;

    public AuthTokenFake(String idToken, String authToken, String userSid, String token) {
        this.idToken = idToken;
        this.authToken = authToken;
        this.userSid = userSid;
        this.token = token;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static AuthTokenFake fromJson(String json) {
        return new Gson().fromJson(json, AuthTokenFake.class);
    }
}