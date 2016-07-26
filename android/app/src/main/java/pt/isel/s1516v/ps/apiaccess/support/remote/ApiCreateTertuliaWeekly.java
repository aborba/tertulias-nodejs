package pt.isel.s1516v.ps.apiaccess.support.remote;

import android.content.Context;

import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiWeekly;

public class ApiCreateTertuliaWeekly extends ApiCreateTertulia {

    @com.google.gson.annotations.SerializedName("weekDay")
    public final String weekDay;
    @com.google.gson.annotations.SerializedName("skip")
    public final int skip;

    public ApiCreateTertuliaWeekly(String name, String subject,
                                   String location, String address, String zip, String city, String country,
                                   String latitude, String longitude,
                                   String weekDay, int skip,
                                   boolean isPrivate) {
        super(name, subject,
                location, address, zip, city, country,
                latitude, longitude,
                "Weekly",
                isPrivate);
        this.weekDay = weekDay;
        this.skip = skip;
    }

    public ApiCreateTertuliaWeekly(Context ctx, CrUiTertulia crUiTertulia, CrUiWeekly crUiWeekly) {
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
        ApiCreateTertuliaWeekly other = (ApiCreateTertuliaWeekly) obj;
        return other.name.equals(name) && other.subject.equals(this.subject);
    }

}
