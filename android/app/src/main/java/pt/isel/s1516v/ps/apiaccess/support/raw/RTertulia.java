package pt.isel.s1516v.ps.apiaccess.support.raw;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;

public class RTertulia {

    @com.google.gson.annotations.SerializedName("tr_id")
    public String id;
    @com.google.gson.annotations.SerializedName("tr_name")
    public String name;
    @com.google.gson.annotations.SerializedName("tr_subject")
    public String subject;
    @com.google.gson.annotations.SerializedName("ev_targetdate")
    public String event;
    @com.google.gson.annotations.SerializedName("schedule")
    public String scheduleType;
    @com.google.gson.annotations.SerializedName("description")
    public String scheduleDescription;
    @com.google.gson.annotations.SerializedName("lo_id")
    public int locationId;
    @com.google.gson.annotations.SerializedName("lo_name")
    public String locationName;
    @com.google.gson.annotations.SerializedName("lo_address")
    public String address;
    @com.google.gson.annotations.SerializedName("lo_zip")
    public String zip;
    @com.google.gson.annotations.SerializedName("lo_city")
    public String city;
    @com.google.gson.annotations.SerializedName("lo_country")
    public String country;
    @com.google.gson.annotations.SerializedName("lo_latitude")
    public double latitude;
    @com.google.gson.annotations.SerializedName("lo_longitude")
    public double longitude;
    @com.google.gson.annotations.SerializedName("tr_is_private")
    public boolean isPrivate;
    @com.google.gson.annotations.SerializedName("nv_name")
    public String role;
    @com.google.gson.annotations.SerializedName("no_count")
    public int messagesTotal;
    @com.google.gson.annotations.SerializedName("_links")
    public ApiLink[] links;

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        RTertulia other = (RTertulia) obj;
        return obj instanceof RTertulia && other.id == this.id && other.name == this.name;
    }

}
