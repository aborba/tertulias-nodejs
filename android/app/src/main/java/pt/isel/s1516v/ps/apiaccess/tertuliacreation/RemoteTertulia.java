package pt.isel.s1516v.ps.apiaccess.tertuliacreation;

import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;

public abstract class RemoteTertulia {

    @com.google.gson.annotations.SerializedName("tr_id")
    public final int tertuliaId;
    @com.google.gson.annotations.SerializedName("tr_name")
    public final String tertuliaName;
    @com.google.gson.annotations.SerializedName("tr_subject")
    public final String subject;

    @com.google.gson.annotations.SerializedName("ro_id")
    public final int roleId;
    @com.google.gson.annotations.SerializedName("ro_name")
    public final String roleName;

    @com.google.gson.annotations.SerializedName("lo_id")
    public final int locationId;
    @com.google.gson.annotations.SerializedName("lo_name")
    public final String locationName;
    @com.google.gson.annotations.SerializedName("lo_address")
    public final String streetAddress;
    @com.google.gson.annotations.SerializedName("lo_zip")
    public final String zip;
    @com.google.gson.annotations.SerializedName("lo_city")
    public final String city;
    @com.google.gson.annotations.SerializedName("lo_country")
    public final String country;
    @com.google.gson.annotations.SerializedName("lo_latitude")
    public final String latitude;
    @com.google.gson.annotations.SerializedName("lo_longitude")
    public final String longitude;

    @com.google.gson.annotations.SerializedName("sc_type")
    public final String scheduleType;
    @com.google.gson.annotations.SerializedName("sc_id")
    public final int scheduleId;

    @com.google.gson.annotations.SerializedName("isPrivate")
    public final boolean isPrivate;

    @com.google.gson.annotations.SerializedName("links")
    public ApiLink[] links;

    public RemoteTertulia(int tertuliaId, String tertuliaName, String subject,
                          int roleId, String roleName,
                          int locationId, String locationName, String streetAddress, String zip, String city, String country,
                          String latitude, String longitude,
                          String scheduleType, int scheduleId,
                          boolean isPrivate) {
        this.tertuliaId = tertuliaId;
        this.tertuliaName = tertuliaName;
        this.subject = subject;
        this.roleId = roleId;
        this.roleName = roleName;
        this.locationId = locationId;
        this.locationName = locationName;
        this.streetAddress = streetAddress;
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.scheduleType = scheduleType;
        this.scheduleId = scheduleId;
        this.isPrivate = isPrivate;
    }

    protected abstract String toStringContribution();

    @Override
    public String toString() {
        String contribution = toStringContribution();
        return tertuliaName + (contribution == null ? "" : " " + contribution);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        RemoteTertulia other = (RemoteTertulia) obj;
        return other.tertuliaName.equals(tertuliaName) && other.subject.equals(this.subject);
    }

}
