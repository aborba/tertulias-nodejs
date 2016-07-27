package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiReadTertuliaMonthly extends ApiReadTertulia {

    @com.google.gson.annotations.SerializedName("monthly")
    public final ApiReadMonthly monthly;

    public ApiReadTertuliaMonthly(ApiReadTertuliaCore tertulia, ApiReadMonthly monthly, ApiLink[] links) {
        super(tertulia, links);
        this.monthly = monthly;
    }
}
