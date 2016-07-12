package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;
import android.os.Parcelable;

import pt.isel.s1516v.ps.apiaccess.support.raw.RLocation;
import pt.isel.s1516v.ps.apiaccess.support.raw.RTertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaCore;

public class Geolocation implements Parcelable {

    public final double latitude;
    public final double longitude;

    public Geolocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Geolocation(String latitude, String longitude) {
        this.latitude = Double.parseDouble(latitude == null ? "0" : latitude);
        this.longitude = Double.parseDouble(longitude == null ? "0" : longitude);
    }

    public Geolocation(RTertulia rtertulia) {
        this(rtertulia.latitude, rtertulia.longitude);
    }

    public Geolocation(RLocation rlocation) {
        this(rlocation.latitude, rlocation.longitude);
    }

    public Geolocation(ApiTertuliaCore core) {
        this(core.latitude, core.longitude);
    }

    // region Parcelable

    protected Geolocation(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
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
        dest.writeDouble(longitude);
    }

    // endregion
}
