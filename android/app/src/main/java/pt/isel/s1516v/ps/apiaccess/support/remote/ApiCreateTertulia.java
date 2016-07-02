package pt.isel.s1516v.ps.apiaccess.support.remote;

public abstract class ApiCreateTertulia {

    @com.google.gson.annotations.SerializedName("name")
    public final String name;
    @com.google.gson.annotations.SerializedName("subject")
    public final String subject;
    @com.google.gson.annotations.SerializedName("location")
    public final String location;
    @com.google.gson.annotations.SerializedName("address")
    public final String address;
    @com.google.gson.annotations.SerializedName("zip")
    public final String zip;
    @com.google.gson.annotations.SerializedName("city")
    public final String city;
    @com.google.gson.annotations.SerializedName("country")
    public final String country;
    @com.google.gson.annotations.SerializedName("latitude")
    public final String latitude;
    @com.google.gson.annotations.SerializedName("longitude")
    public final String longitude;

    @com.google.gson.annotations.SerializedName("scheduleName")
    public final String scheduleName;

    @com.google.gson.annotations.SerializedName("isPrivate")
    public final boolean isPrivate;

    public ApiCreateTertulia(String name, String subject,
                             String location, String address, String zip, String city, String country,
                             String latitude, String longitude,
                             String scheduleName,
                             boolean isPrivate) {
        this.name = name;
        this.subject = subject;
        this.location = location;
        this.address = address;
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.scheduleName = scheduleName;
        this.isPrivate = isPrivate;
    }

    public ApiCreateTertulia(String name, String subject,
                             String location, String address, String zip, String city, String country,
                             String latitude, String longitude,
                             String weekDay, int weekNr, boolean fromStart, int skip,
                             boolean isPrivate) {
        this.scheduleName = "MonthlyW";
        this.name = name;
        this.subject = subject;
        this.location = location;
        this.address = address;
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isPrivate = isPrivate;
    }

    protected abstract String toStringContribution();

    @Override
    public String toString() {
        String contribution = toStringContribution();
        return name + (contribution == null ? "" : " " + contribution);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ApiCreateTertulia other = (ApiCreateTertulia) obj;
        return other.name.equals(name) && other.subject.equals(this.subject);
    }

}
