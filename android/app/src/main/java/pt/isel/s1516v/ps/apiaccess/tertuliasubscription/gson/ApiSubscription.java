package pt.isel.s1516v.ps.apiaccess.tertuliasubscription.gson;

public class ApiSubscription {
    @com.google.gson.annotations.SerializedName("tertulia")
    public final int tertulia;

    public ApiSubscription(int tertulia) {
        this.tertulia = tertulia;
    }
}
