package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiReadMonthly {

    @com.google.gson.annotations.SerializedName("id")
    public final int id;
    @com.google.gson.annotations.SerializedName("daynr")
    public final int dayNr;
    @com.google.gson.annotations.SerializedName("isFromStart")
    public final boolean isFromStart;
    @com.google.gson.annotations.SerializedName("skip")
    public final int skip;

    public ApiReadMonthly(int id, int dayNr, boolean isFromStart, int skip) {
        this.id = id;
        this.dayNr = dayNr;
        this.isFromStart = isFromStart;
        this.skip = skip;
    }
}
