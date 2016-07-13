package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiMe {
    @com.google.gson.annotations.SerializedName("me")
    public final ApiMeCore me;
    @com.google.gson.annotations.SerializedName("links")
    public final ApiLink[] links;

    public ApiMe() {
        me = null;
        links = null;
    }
}
