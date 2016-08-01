package pt.isel.s1516v.ps.apiaccess.tertuliadetails.api;

import pt.isel.s1516v.ps.apiaccess.tertuliacreation.api.CrApiTertulia;

public abstract class DtApiTertulia extends CrApiTertulia {

    @com.google.gson.annotations.SerializedName("tertulia_id")
    public final int tertuliaId;
    @com.google.gson.annotations.SerializedName("role_id")
    public final int roleId;
    @com.google.gson.annotations.SerializedName("role_type")
    public final String roleType;
    @com.google.gson.annotations.SerializedName("location_id")
    public final int locationId;

    public DtApiTertulia(int tertuliaId, String tertuliaName, String subject, boolean isPrivate,
                         int roleId, String roleType,
                         int locationId, String locationName, String streetAddress, String zip, String city, String country, String latitude, String longitude,
                         String scheduleType) {
        super(tertuliaName, subject, isPrivate,
                locationName, streetAddress, zip, city, country, latitude, longitude,
                scheduleType);
        this.tertuliaId = tertuliaId;
        this.roleId = roleId;
        this.roleType = roleType;
        this.locationId = locationId;
    }

}
