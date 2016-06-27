package pt.isel.s1516v.ps.apiaccess.support.remote;

public class SelfLink {
    @com.google.gson.annotations.SerializedName("href")
    final String href;

    public SelfLink(String href) {
        this.href = href;
    }
}
