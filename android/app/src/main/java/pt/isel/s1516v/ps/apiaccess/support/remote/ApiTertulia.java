package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiTertulia {
    @com.google.gson.annotations.SerializedName("tertulia")
    public final ApiTertuliaCore tertulia;
    @com.google.gson.annotations.SerializedName("links")
    public final ApiLink[] links;

    public ApiTertulia(ApiTertuliaCore tertulia, ApiLink[] links) {
        this.tertulia = tertulia;
        this.links = links;
    }
}
