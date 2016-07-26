package pt.isel.s1516v.ps.apiaccess.tertuliadetails.api;

import pt.isel.s1516v.ps.apiaccess.tertuliacreation.api.CrApiTertulia;

public abstract class DtApiTertulia extends CrApiTertulia {

    @com.google.gson.annotations.SerializedName("tr_id")
    public final int tertuliaId;
    @com.google.gson.annotations.SerializedName("ro_id")
    public final int roleId;
    @com.google.gson.annotations.SerializedName("ro_name")
    public final String roleName;
    @com.google.gson.annotations.SerializedName("lo_id")
    public final int locationId;

    public DtApiTertulia(int tertuliaId, String tertuliaName, String subject,
                         int roleId, String roleName,
                         int locationId, String locationName, String streetAddress, String zip, String city, String country, String latitude, String longitude,
//                         String scheduleType, int scheduleId,
                         boolean isPrivate) {
        super(tertuliaName, subject,
                locationName, streetAddress, zip, city, country, latitude, longitude,
//                scheduleType, scheduleId,
                isPrivate);
        this.tertuliaId = tertuliaId;
        this.roleId = roleId;
        this.roleName = roleName;
        this.locationId = locationId;
    }

}
