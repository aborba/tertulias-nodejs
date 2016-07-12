package pt.isel.s1516v.ps.apiaccess.support.remote;

public class JwtPayload {
    @com.google.gson.annotations.SerializedName("stable_sid")
    public final String stableSid;
    @com.google.gson.annotations.SerializedName("sub")
    public final String sub;
    @com.google.gson.annotations.SerializedName("idp")
    public final String identityProvider;
    @com.google.gson.annotations.SerializedName("ver")
    public final String version;
    @com.google.gson.annotations.SerializedName("iss")
    public final String iss;
    @com.google.gson.annotations.SerializedName("aud")
    public final String aud;
    @com.google.gson.annotations.SerializedName("exp")
    public final long exp;
    @com.google.gson.annotations.SerializedName("nbf")
    public final long nbf;

    public JwtPayload(String stableSid, String sub, String identityProvider, String version, String iss, String aud, long exp, long nbf) {
        this.stableSid = stableSid;
        this.sub = sub;
        this.identityProvider = identityProvider;
        this.version = version;
        this.iss = iss;
        this.aud = aud;
        this.exp = exp;
        this.nbf = nbf;
    }
}
