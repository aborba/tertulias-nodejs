package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiReadTertuliaWeekly extends ApiReadTertulia {

    @com.google.gson.annotations.SerializedName("weekly")
    public final ApiReadWeekly weekly;

    public ApiReadTertuliaWeekly(ApiReadTertuliaCore tertulia, ApiReadWeekly weekly, ApiLink[] links) {
        super(tertulia, links);
        this.weekly = weekly;
    }
}
