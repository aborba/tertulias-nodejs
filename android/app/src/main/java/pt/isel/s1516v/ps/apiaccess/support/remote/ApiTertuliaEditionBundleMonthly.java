package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiTertuliaEditionBundleMonthly extends ApiTertuliaEditionBundle {

    @com.google.gson.annotations.SerializedName("monthly")
    public final ApiScheduleEditionMonthly monthly;

    public ApiTertuliaEditionBundleMonthly(ApiTertuliaEdition tertulia, ApiScheduleEditionMonthly monthly, ApiLink[] links) {
        super(tertulia, links);
        this.monthly = monthly;
    }
}
