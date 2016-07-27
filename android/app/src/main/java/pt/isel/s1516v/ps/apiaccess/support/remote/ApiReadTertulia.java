package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiReadTertulia {
    @com.google.gson.annotations.SerializedName("tertulia")
    public final ApiReadTertuliaCore tertulia;
    @com.google.gson.annotations.SerializedName("links")
    public final ApiLink[] links;

    public ApiReadTertulia(ApiReadTertuliaCore tertulia, ApiLink[] links) {
        this.tertulia = tertulia;
        this.links = links;
    }
}
