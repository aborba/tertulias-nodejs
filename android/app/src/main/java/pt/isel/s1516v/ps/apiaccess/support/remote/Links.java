package pt.isel.s1516v.ps.apiaccess.support.remote;

public class Links {
    @com.google.gson.annotations.SerializedName("tag")
    final String tag;
    @com.google.gson.annotations.SerializedName("method")
    final String method;
    @com.google.gson.annotations.SerializedName("href")
    final String href;

    public Links(String tag, String method, String href) {
        this.tag = tag;
        this.method = method;
        this.href = href;
    }
}
