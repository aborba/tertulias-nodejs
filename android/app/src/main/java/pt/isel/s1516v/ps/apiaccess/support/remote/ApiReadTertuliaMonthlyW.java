package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiReadTertuliaMonthlyW extends ApiReadTertulia {

    @com.google.gson.annotations.SerializedName("monthlyw")
    public final ApiReadMonthlyW monthlyw;

    public ApiReadTertuliaMonthlyW(ApiReadTertuliaCore tertulia, ApiReadMonthlyW monthlyw, ApiLink[] links) {
        super(tertulia, links);
        this.monthlyw = monthlyw;
    }
}
