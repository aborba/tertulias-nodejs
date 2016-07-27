package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.raw.RLocation;
import pt.isel.s1516v.ps.apiaccess.support.raw.RTertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiReadTertuliaCore;

public class Geolocation implements Parcelable {

    public final double latitude, longitude;
    public final boolean isLatitude, isLongitude;

    public Geolocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        isLatitude = isLongitude = true;
    }

    public Geolocation(String latitude, String longitude) {
        isLatitude = !TextUtils.isEmpty(latitude);
        this.latitude = Util.string2Double(latitude);
        isLongitude = !TextUtils.isEmpty(longitude);
        this.longitude = Util.string2Double(longitude);
    }

    public Geolocation(RTertulia rtertulia) {
        this(rtertulia.latitude, rtertulia.longitude);
    }

    public Geolocation(RLocation rlocation) {
        this(rlocation.latitude, rlocation.longitude);
    }

    public Geolocation(ApiReadTertuliaCore core) {
        this(core.latitude, core.longitude);
    }

    public String getLatitude() {
        return isLatitude ? String.valueOf(latitude) : "";
    }

    public String getLongitude() {
        return isLongitude ? String.valueOf(longitude) : "";
    }

    // region Parcelable

    protected Geolocation(Parcel in) {
        latitude = in.readDouble();
        isLatitude = in.readByte() != 0;
        longitude = in.readDouble();
        isLongitude = in.readByte() != 0;
    }

    public static final Creator<Geolocation> CREATOR = new Creator<Geolocation>() {
        @Override
        public Geolocation createFromParcel(Parcel in) {
            return new Geolocation(in);
        }

        @Override
        public Geolocation[] newArray(int size) {
            return new Geolocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeByte((byte) (isLatitude ? 1 : 0));
        dest.writeDouble(longitude);
        dest.writeByte((byte) (isLongitude ? 1 : 0));
    }

    // endregion
}
