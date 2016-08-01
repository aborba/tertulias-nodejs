package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiTertuliaEditionBundleWeekly extends ApiTertuliaEditionBundle {

    @com.google.gson.annotations.SerializedName("weekly")
    public final ApiScheduleEditionWeekly weekly;

    public ApiTertuliaEditionBundleWeekly(ApiTertuliaEdition tertulia, ApiScheduleEditionWeekly weekly, ApiLink[] links) {
        super(tertulia, links);
        this.weekly = weekly;
    }
}
