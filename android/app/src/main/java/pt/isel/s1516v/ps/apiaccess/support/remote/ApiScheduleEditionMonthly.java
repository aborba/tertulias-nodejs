package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiScheduleEditionMonthly {

    @com.google.gson.annotations.SerializedName("schedule_id")
    public final int sc_id;
    @com.google.gson.annotations.SerializedName("schedule_daynr")
    public final int sc_daynr;
    @com.google.gson.annotations.SerializedName("schedule_isfromstart")
    public final boolean sc_isfromstart;
    @com.google.gson.annotations.SerializedName("schedule_skip")
    public final int sc_skip;

    public ApiScheduleEditionMonthly(int sc_id, int sc_daynr, boolean sc_isfromstart, int sc_skip) {
        this.sc_id = sc_id;
        this.sc_daynr = sc_daynr;
        this.sc_isfromstart = sc_isfromstart;
        this.sc_skip = sc_skip;
    }
}
