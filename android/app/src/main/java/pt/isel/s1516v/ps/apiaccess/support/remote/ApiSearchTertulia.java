package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiSearchTertulia {
    @com.google.gson.annotations.SerializedName("searchTerms")
    public final String searchTerms;
    @com.google.gson.annotations.SerializedName("latitude")
    public final double latitude;
    @com.google.gson.annotations.SerializedName("longitude")
    public final double longitude;

    public ApiSearchTertulia(String searchTerms, double latitude, double longitude) {
        this.searchTerms = searchTerms;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
