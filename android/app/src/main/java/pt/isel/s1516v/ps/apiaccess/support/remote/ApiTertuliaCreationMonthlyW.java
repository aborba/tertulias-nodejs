package pt.isel.s1516v.ps.apiaccess.support.remote;

import android.content.Context;

import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthlyW;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;

public class ApiTertuliaCreationMonthlyW extends ApiTertuliaCreation {

    @com.google.gson.annotations.SerializedName("sc_weekday")
    public final int sc_weekday;
    @com.google.gson.annotations.SerializedName("sc_weeknr")
    public final int sc_weeknr;
    @com.google.gson.annotations.SerializedName("sc_isfromstart")
    public final boolean sc_isfromstart;
    @com.google.gson.annotations.SerializedName("sc_skip")
    public final int sc_skip;

    public ApiTertuliaCreationMonthlyW(String tr_name, String tr_subject, boolean tr_isPrivate,
                                       String lo_location, String lo_address, String lo_zip, String lo_city, String lo_country,
                                       String lo_latitude, String lo_longitude,
                                       int sc_weekday, int sc_weeknr, boolean sc_isfromstart, int sc_skip) {
        super(tr_name, tr_subject, tr_isPrivate,
                lo_location, lo_address, lo_zip, lo_city, lo_country,
                lo_latitude, lo_longitude,
                "monthlyw");
        this.sc_weekday = sc_weekday;
        this.sc_weeknr = sc_weeknr;
        this.sc_isfromstart = sc_isfromstart;
        this.sc_skip = sc_skip;
    }

    @Override
    protected String toStringContribution() { return null; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ApiTertuliaCreationMonthlyW other = (ApiTertuliaCreationMonthlyW) obj;
        return other.tr_name.equals(tr_name) && other.tr_subject.equals(this.tr_subject);
    }

}
