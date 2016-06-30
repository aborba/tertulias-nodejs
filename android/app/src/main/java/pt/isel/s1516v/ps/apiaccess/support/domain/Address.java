package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import pt.isel.s1516v.ps.apiaccess.support.raw.RLocation;
import pt.isel.s1516v.ps.apiaccess.support.raw.RTertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaCore;

public class Address implements Parcelable {

    public final String address;
    public final String zip;
    public final String city;
    public final String country;

    public Address(String address, String zip, String city, String country) {
        this.address = address;
        this.zip = zip;
        this.city = city;
        this.country = country;
    }

    public Address(RTertulia rtertulia) {
        address = rtertulia.address;
        zip = rtertulia.zip;
        city = rtertulia.city;
        country = rtertulia.country;
    }

    public Address(ApiTertuliaCore core) {
        address = core.address;
        zip = core.zip;
        city = core.city;
        country = core.country;
    }

    public Address(RLocation rlocation) {
        address = rlocation.address;
        zip = rlocation.zip;
        city = rlocation.city;
        country = rlocation.country;
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    private String compose(String separator, String begin, String end) {
        return TextUtils.isEmpty(begin) ? end : begin + separator + end;
    }

    @Override
    public String toString() {
        return compose(", ", compose(", ", address, compose(", ", zip, city)), country);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        Address other = (Address) obj;
        return obj instanceof Address &&
                other.address.equals(address) &&
                other.zip.equals(zip) &&
                other.city.equals(city) &&
                other.country.equals(country);
    }

    // region Parcelable

    protected Address(Parcel in) {
        address = in.readString();
        zip = in.readString();
        city = in.readString();
        country = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(zip);
        dest.writeString(city);
        dest.writeString(country);
    }

    // endregion
}
