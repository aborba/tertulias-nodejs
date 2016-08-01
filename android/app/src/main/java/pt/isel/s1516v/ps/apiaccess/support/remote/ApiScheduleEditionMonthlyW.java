package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiScheduleEditionMonthlyW {

    @com.google.gson.annotations.SerializedName("schedule_id")
    public final int sc_id;
    @com.google.gson.annotations.SerializedName("schedule_weekday")
    public final int sc_weekday;
    @com.google.gson.annotations.SerializedName("schedule_weeknr")
    public final int sc_weeknr;
    @com.google.gson.annotations.SerializedName("schedule_isfromstart")
    public final boolean sc_isfromstart;
    @com.google.gson.annotations.SerializedName("schedule_skip")
    public final int sc_skip;

    public ApiScheduleEditionMonthlyW(int sc_id, int weekDay, int sc_weeknr, boolean sc_isfromstart, int sc_skip) {
        this.sc_id = sc_id;
        this.sc_weekday = weekDay;
        this.sc_weeknr = sc_weeknr;
        this.sc_isfromstart = sc_isfromstart;
        this.sc_skip = sc_skip;
    }
}
