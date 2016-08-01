package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiScheduleEditionWeekly {

    @com.google.gson.annotations.SerializedName("schedule_id")
    public final int sc_id;
    @com.google.gson.annotations.SerializedName("schedule_weekday")
    public final int sc_weekday;
    @com.google.gson.annotations.SerializedName("schedule_skip")
    public final int sc_skip;

    public ApiScheduleEditionWeekly(int sc_id, int sc_weekday, int sc_skip) {
        this.sc_id = sc_id;
        this.sc_weekday = sc_weekday;
        this.sc_skip = sc_skip;
    }
}
