package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiReadMonthlyW {

    @com.google.gson.annotations.SerializedName("id")
    public final int id;
    @com.google.gson.annotations.SerializedName("weekDay")
    public final int weekDay;
    @com.google.gson.annotations.SerializedName("weekNr")
    public final int weekNr;
    @com.google.gson.annotations.SerializedName("isFromStart")
    public final boolean isFromStart;
    @com.google.gson.annotations.SerializedName("skip")
    public final int skip;

    public ApiReadMonthlyW(int id, int weekDay, int weekNr, boolean isFromStart, int skip) {
        this.id = id;
        this.weekDay = weekDay;
        this.weekNr = weekNr;
        this.isFromStart = isFromStart;
        this.skip = skip;
    }
}
