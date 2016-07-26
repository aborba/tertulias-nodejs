package pt.isel.s1516v.ps.apiaccess.support.remote;

import android.content.Context;

import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthlyW;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;

public class ApiCreateTertuliaMonthlyW extends ApiCreateTertulia {

    @com.google.gson.annotations.SerializedName("weekday")
    public final String weekDay;
    @com.google.gson.annotations.SerializedName("weekNr")
    public final int weekNr;
    @com.google.gson.annotations.SerializedName("fromStart")
    public final boolean fromStart;
    @com.google.gson.annotations.SerializedName("skip")
    public final int skip;

    public ApiCreateTertuliaMonthlyW(String name, String subject,
                                     String location, String address, String zip, String city, String country,
                                     String latitude, String longitude,
                                     String weekDay, int weekNr, boolean fromStart, int skip,
                                     boolean isPrivate) {
        super(name, subject,
                location, address, zip, city, country,
                latitude, longitude,
                "MonthlyW",
                isPrivate);
        this.weekDay = weekDay;
        this.weekNr = weekNr;
        this.fromStart = fromStart;
        this.skip = skip;
    }

    public ApiCreateTertuliaMonthlyW(Context ctx, CrUiTertulia crUiTertulia, CrUiMonthlyW crUiMonthlyW) {
        this(crUiTertulia.name, crUiTertulia.subject,
                crUiTertulia.crUiLocation.name, crUiTertulia.crUiLocation.address.address, crUiTertulia.crUiLocation.address.zip, crUiTertulia.crUiLocation.address.city, crUiTertulia.crUiLocation.address.country,
                String.valueOf(crUiTertulia.crUiLocation.geo.latitude), String.valueOf(crUiTertulia.crUiLocation.geo.longitude),
                crUiMonthlyW.getWeekDay(ctx), crUiMonthlyW.weekNr, crUiMonthlyW.isFromStart, crUiMonthlyW.skip,
                crUiTertulia.isPrivate);
    }

    @Override
    protected String toStringContribution() { return null; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ApiCreateTertuliaMonthlyW other = (ApiCreateTertuliaMonthlyW) obj;
        return other.name.equals(name) && other.subject.equals(this.subject);
    }

}
