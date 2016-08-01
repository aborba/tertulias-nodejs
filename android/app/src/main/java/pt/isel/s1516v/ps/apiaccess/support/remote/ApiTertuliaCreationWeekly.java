package pt.isel.s1516v.ps.apiaccess.support.remote;

import android.content.Context;

import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiWeekly;

public class ApiTertuliaCreationWeekly extends ApiTertuliaCreation {

    @com.google.gson.annotations.SerializedName("weekDay")
    public final String weekDay;
    @com.google.gson.annotations.SerializedName("sc_skip")
    public final int skip;

    public ApiTertuliaCreationWeekly(String name, String subject,
                                     String location, String address, String zip, String city, String country,
                                     String latitude, String longitude,
                                     String weekDay, int skip,
                                     boolean isPrivate) {
        super(name, subject, isPrivate,
                location, address, zip, city, country,
                latitude, longitude,
                "Weekly");
        this.weekDay = weekDay;
        this.skip = skip;
    }

    public ApiTertuliaCreationWeekly(Context ctx, CrUiTertulia crUiTertulia, CrUiWeekly crUiWeekly) {
        this(crUiTertulia.name, crUiTertulia.subject,
                crUiTertulia.crUiLocation.name, crUiTertulia.crUiLocation.address.address, crUiTertulia.crUiLocation.address.zip, crUiTertulia.crUiLocation.address.city, crUiTertulia.crUiLocation.address.country,
                String.valueOf(crUiTertulia.crUiLocation.geo.latitude), String.valueOf(crUiTertulia.crUiLocation.geo.longitude),
                crUiWeekly.getWeekDay(ctx), crUiWeekly.skip,
                crUiTertulia.isPrivate);
    }

    @Override
    protected String toStringContribution() { return null; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ApiTertuliaCreationWeekly other = (ApiTertuliaCreationWeekly) obj;
        return other.tr_name.equals(tr_name) && other.tr_subject.equals(this.tr_subject);
    }

}
