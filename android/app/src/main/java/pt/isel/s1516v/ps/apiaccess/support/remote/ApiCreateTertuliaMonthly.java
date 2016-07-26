package pt.isel.s1516v.ps.apiaccess.support.remote;

import android.content.Context;

import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthly;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;

public class ApiCreateTertuliaMonthly extends ApiCreateTertulia {

    @com.google.gson.annotations.SerializedName("dayNr")
    public final int dayNr;
    @com.google.gson.annotations.SerializedName("fromStart")
    public final boolean fromStart;
    @com.google.gson.annotations.SerializedName("skip")
    public final int skip;

    public ApiCreateTertuliaMonthly(String name, String subject,
                                    String location, String address, String zip, String city, String country,
                                    String latitude, String longitude,
                                    int dayNr, boolean fromStart, int skip,
                                    boolean isPrivate) {
        super(name, subject,
                location, address, zip, city, country,
                latitude, longitude,
                "Monthly",
                isPrivate);
        this.dayNr = dayNr;
        this.fromStart = fromStart;
        this.skip = skip;
    }

    public ApiCreateTertuliaMonthly(CrUiTertulia crUiTertulia, CrUiMonthly crUiMonthly) {
        this(crUiTertulia.name, crUiTertulia.subject,
                crUiTertulia.crUiLocation.name, crUiTertulia.crUiLocation.address.address, crUiTertulia.crUiLocation.address.zip, crUiTertulia.crUiLocation.address.city, crUiTertulia.crUiLocation.address.country,
                String.valueOf(crUiTertulia.crUiLocation.geo.latitude), String.valueOf(crUiTertulia.crUiLocation.geo.longitude),
                crUiMonthly.dayNr, crUiMonthly.isFromStart, crUiMonthly.skip,
                crUiTertulia.isPrivate);
    }

    @Override
    protected String toStringContribution() { return null; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ApiCreateTertuliaMonthly other = (ApiCreateTertuliaMonthly) obj;
        return other.name.equals(name) && other.subject.equals(this.subject);
    }

}
