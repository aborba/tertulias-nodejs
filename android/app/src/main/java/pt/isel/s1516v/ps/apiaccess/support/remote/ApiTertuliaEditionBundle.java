package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiTertuliaEditionBundle {
    @com.google.gson.annotations.SerializedName("tertulia")
    public final ApiTertuliaEdition tertulia;
    @com.google.gson.annotations.SerializedName("links")
    public final ApiLink[] links;

    public ApiTertuliaEditionBundle(ApiTertuliaEdition tertulia, ApiLink[] links) {
        this.tertulia = tertulia;
        this.links = links;
    }
}
