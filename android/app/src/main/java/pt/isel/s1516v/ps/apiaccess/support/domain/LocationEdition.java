package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;
import android.os.Parcelable;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.raw.RLocation;
import pt.isel.s1516v.ps.apiaccess.support.raw.RTertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaListItem;

public class LocationEdition extends LocationCreation {

    public int id;

    public LocationEdition(int id, String name, Address address, Geolocation geolocation) {
        super(name, address, geolocation);
        this.id = id;
    }

    public LocationEdition(ApiTertuliaEdition apiTertuliaEdition) {
        this(Integer.parseInt(apiTertuliaEdition.lo_id), apiTertuliaEdition.lo_name,
                new Address(apiTertuliaEdition.lo_address, apiTertuliaEdition.lo_zip, apiTertuliaEdition.lo_city, apiTertuliaEdition.lo_country),
                new Geolocation(apiTertuliaEdition.lo_latitude, apiTertuliaEdition.lo_longitude));
    }

    @Override
    public String toString() { return name + " (" + address + ")"; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        LocationEdition other = (LocationEdition) obj;
        return obj instanceof LocationEdition && other.id == this.id && other.name == this.name;
    }

    // region Parcelable

    protected LocationEdition(Parcel in) {
        super(in);
        id = in.readInt();
    }

    public static final Creator<LocationEdition> CREATOR = new Creator<LocationEdition>() {
        @Override
        public LocationEdition createFromParcel(Parcel in) {
            return new LocationEdition(in);
        }

        @Override
        public LocationEdition[] newArray(int size) {
            return new LocationEdition[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(id);
    }

    // endregion
}
