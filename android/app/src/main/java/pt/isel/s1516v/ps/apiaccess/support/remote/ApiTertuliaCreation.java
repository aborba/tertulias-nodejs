package pt.isel.s1516v.ps.apiaccess.support.remote;

public abstract class ApiTertuliaCreation {

    @com.google.gson.annotations.SerializedName("tertulia_name")
    public final String tr_name;
    @com.google.gson.annotations.SerializedName("tertulia_subject")
    public final String tr_subject;
    @com.google.gson.annotations.SerializedName("tertulia_isprivate")
    public final boolean tr_isPrivate;
    @com.google.gson.annotations.SerializedName("location_name")
    public final String lo_name;
    @com.google.gson.annotations.SerializedName("location_address")
    public final String lo_address;
    @com.google.gson.annotations.SerializedName("location_zip")
    public final String lo_zip;
    @com.google.gson.annotations.SerializedName("location_city")
    public final String lo_city;
    @com.google.gson.annotations.SerializedName("location_country")
    public final String lo_country;
    @com.google.gson.annotations.SerializedName("location_latitude")
    public final String lo_latitude;
    @com.google.gson.annotations.SerializedName("location_longitude")
    public final String lo_longitude;
    @com.google.gson.annotations.SerializedName("schedule_name")
    public final String sc_name;

    public ApiTertuliaCreation(String tr_name, String tr_subject, boolean tr_isPrivate,
                               String lo_name, String lo_address, String lo_zip, String lo_city, String lo_country,
                               String lo_latitude, String lo_longitude,
                               String sc_name) {
        this.tr_name = tr_name;
        this.tr_subject = tr_subject;
        this.tr_isPrivate = tr_isPrivate;
        this.lo_name = lo_name;
        this.lo_address = lo_address;
        this.lo_zip = lo_zip;
        this.lo_city = lo_city;
        this.lo_country = lo_country;
        this.lo_latitude = lo_latitude;
        this.lo_longitude = lo_longitude;
        this.sc_name = sc_name;
    }

    protected abstract String toStringContribution();

    @Override
    public String toString() {
        String contribution = toStringContribution();
        return tr_name + (contribution == null ? "" : " " + contribution);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ApiTertuliaCreation other = (ApiTertuliaCreation) obj;
        return other.tr_name.equals(tr_name) && other.tr_subject.equals(this.tr_subject);
    }

}
