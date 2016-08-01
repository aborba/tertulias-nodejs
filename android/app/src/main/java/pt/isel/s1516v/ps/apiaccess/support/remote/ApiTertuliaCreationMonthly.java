package pt.isel.s1516v.ps.apiaccess.support.remote;

import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthly;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;

public class ApiTertuliaCreationMonthly extends ApiTertuliaCreation {

    @com.google.gson.annotations.SerializedName("sc_daynr")
    public final int sc_daynr;
    @com.google.gson.annotations.SerializedName("sc_isfromstart")
    public final boolean sc_isfromstart;
    @com.google.gson.annotations.SerializedName("sc_skip")
    public final int sc_skip;

    public ApiTertuliaCreationMonthly(String tr_name, String tr_subject, boolean tr_isPrivate,
                                      String lo_location, String lo_address, String lo_zip, String lo_city, String lo_country,
                                      String lo_latitude, String lo_longitude,
                                      int sc_daynr, boolean sc_isfromstart, int sc_skip) {
        super(tr_name, tr_subject, tr_isPrivate,
                lo_location, lo_address, lo_zip, lo_city, lo_country,
                lo_latitude, lo_longitude,
                "monthly");
        this.sc_daynr = sc_daynr;
        this.sc_isfromstart = sc_isfromstart;
        this.sc_skip = sc_skip;
    }

    @Override
    protected String toStringContribution() { return null; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ApiTertuliaCreationMonthly other = (ApiTertuliaCreationMonthly) obj;
        return other.tr_name.equals(tr_name) && other.tr_subject.equals(this.tr_subject);
    }

}
