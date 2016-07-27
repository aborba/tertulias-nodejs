package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiReadWeekly {

    @com.google.gson.annotations.SerializedName("id")
    public final int id;
    @com.google.gson.annotations.SerializedName("weekDay")
    public final int weekDay;
    @com.google.gson.annotations.SerializedName("skip")
    public final int skip;

    public ApiReadWeekly(int id, int weekDay, int skip) {
        this.id = id;
        this.weekDay = weekDay;
        this.skip = skip;
    }
}
