package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiTertuliaCore {

    @com.google.gson.annotations.SerializedName("id")
    public final String id;
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
    @com.google.gson.annotations.SerializedName("scheduleId")
    public final int scheduleId;
    @com.google.gson.annotations.SerializedName("scheduleName")
    public final String scheduleName;
    @com.google.gson.annotations.SerializedName("scheduleDescription")
    public final String scheduleDescription;
    @com.google.gson.annotations.SerializedName("private")
    public final boolean isPrivate;
    @com.google.gson.annotations.SerializedName("role")
    public final String role;
    @com.google.gson.annotations.SerializedName("messages")
    public final int messagesCount;

    public ApiTertuliaCore(String id, String name, String subject,
                           String location, String address, String zip, String city, String country,
                           String latitude, String longitude,
                           int scheduleId, String scheduleName, String scheduleDescription,
                           boolean isPrivate, String role, int messagesCount) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.location = location;
        this.address = address;
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.scheduleDescription = scheduleDescription;
        this.isPrivate = isPrivate;
        this.role = role;
        this.messagesCount = messagesCount;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ApiTertuliaCore other = (ApiTertuliaCore) obj;
        return obj instanceof ApiTertuliaCore && other.id == this.id && other.name == this.name;
    }

}
