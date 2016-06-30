package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiSelfLink {
    @com.google.gson.annotations.SerializedName("href")
    final String href;

    public ApiSelfLink(String href) {
        this.href = href;
    }
}
