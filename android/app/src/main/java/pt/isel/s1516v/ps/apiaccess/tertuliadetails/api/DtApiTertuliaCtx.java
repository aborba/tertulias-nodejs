package pt.isel.s1516v.ps.apiaccess.tertuliadetails.api;

import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;

public class DtApiTertuliaCtx {
    @com.google.gson.annotations.SerializedName("tertulia")
    public final DtApiTertulia tertulia;
    @com.google.gson.annotations.SerializedName("links")
    public final ApiLink[] links;

    public DtApiTertuliaCtx(DtApiTertulia tertulia, ApiLink[] links) {
        this.tertulia = tertulia;
        this.links = links;
    }
}
