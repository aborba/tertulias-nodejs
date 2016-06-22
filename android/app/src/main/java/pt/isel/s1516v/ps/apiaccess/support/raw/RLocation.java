package pt.isel.s1516v.ps.apiaccess.support.raw;

import android.content.Context;

public class RLocation {

    @com.google.gson.annotations.SerializedName("lo_id")
    public int id;
    @com.google.gson.annotations.SerializedName("lo_name")
    public String name;
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
    @com.google.gson.annotations.SerializedName("lo_tertulia")
    public int tertulia;

    public static Builder builder() {
        return new Builder();
    }

    public RLocation(Context ctx, Builder builder) {
        id = builder.id;
        name = builder.name;
        address = builder.address;
        zip = builder.zip;
        city = builder.city;
        country = builder.country;
        latitude = builder.latitude;
        longitude = builder.longitude;
        tertulia = builder.tertulia;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        RLocation other = (RLocation) obj;
        return obj instanceof RLocation && other.id == this.id && other.name == this.name;
    }

    public static class Builder {
        private int id;
        private String name;
        private String address;
        private String zip;
        private String city;
        private String country;
        private double latitude;
        private double longitude;
        private int tertulia;

        public Builder id(String value) { id = Integer.parseInt(value); return this; }

        public Builder name(String value) { name = value; return this; }

        public Builder address(String value) { address = value; return this; }

        public Builder zip(String value) { zip = value; return this; }

        public Builder city(String value) { city = value; return this; }

        public Builder country(String value) { country = value; return this; }

        public Builder latitude(String value) { latitude = Double.parseDouble(value); return this; }

        public Builder longitude(String value) { longitude = Double.parseDouble(value); return this; }

        public Builder tertulia(int value) { tertulia = value; return this; }

        public RLocation build(Context ctx) { return new RLocation(ctx, this); }
    }

}
