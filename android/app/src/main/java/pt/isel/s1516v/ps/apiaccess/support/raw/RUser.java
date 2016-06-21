package pt.isel.s1516v.ps.apiaccess.support.raw;

public class RUser {
    @com.google.gson.annotations.SerializedName("us_id")
    public int id;
    @com.google.gson.annotations.SerializedName("us_sid")
    public String sid;
    @com.google.gson.annotations.SerializedName("us_alias")
    public String alias;
    @com.google.gson.annotations.SerializedName("us_email")
    public String email;
    @com.google.gson.annotations.SerializedName("us_firstName")
    public String firstName;
    @com.google.gson.annotations.SerializedName("us_lastName")
    public String lastName;
    @com.google.gson.annotations.SerializedName("us_picture")
    public String picture;
}
