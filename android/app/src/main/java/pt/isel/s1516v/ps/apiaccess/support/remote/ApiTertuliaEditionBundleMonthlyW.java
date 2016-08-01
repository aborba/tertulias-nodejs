package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiTertuliaEditionBundleMonthlyW extends ApiTertuliaEditionBundle {

    @com.google.gson.annotations.SerializedName("monthlyw")
    public final ApiScheduleEditionMonthlyW monthlyw;

    public ApiTertuliaEditionBundleMonthlyW(ApiTertuliaEdition tertulia, ApiScheduleEditionMonthlyW monthlyw, ApiLink[] links) {
        super(tertulia, links);
        this.monthlyw = monthlyw;
    }
}
