package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;
import android.os.Parcelable;

import pt.isel.s1516v.ps.apiaccess.support.raw.RLocation;
import pt.isel.s1516v.ps.apiaccess.support.raw.RTertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaCore;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaListItem;

public class NewLocation implements Parcelable {

    public int id;
    public String name;
    public Address address;
    public Geolocation geolocation;

    public NewLocation(RLocation rlocation) {
        id = rlocation.id;
        name = rlocation.name;
        address = new Address(rlocation);
        geolocation = new Geolocation(rlocation);
    }

    public NewLocation(ApiTertuliaListItem apiTertuliaListItem) {
        id = -1;
        name = apiTertuliaListItem.eventLocation;
    }

    public NewLocation(RTertulia rtertulia) {
        id = rtertulia.locationId;
        name = rtertulia.locationName;
        address = new Address(rtertulia);
        geolocation = new Geolocation(rtertulia);
    }

    public NewLocation(ApiTertuliaCore core) {
        id = -1;
        name = core.location;
        address = new Address(core);
        geolocation = new Geolocation(core);
    }

    @Override
    public String toString() { return name + " (" + address + ")"; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        NewLocation other = (NewLocation) obj;
        return obj instanceof NewLocation && other.id == this.id && other.name == this.name;
    }

    // region Parcelable

    protected NewLocation(Parcel in) {
        id = in.readInt();
        name = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        geolocation = in.readParcelable(Geolocation.class.getClassLoader());
    }

    public static final Creator<NewLocation> CREATOR = new Creator<NewLocation>() {
        @Override
        public NewLocation createFromParcel(Parcel in) {
            return new NewLocation(in);
        }

        @Override
        public NewLocation[] newArray(int size) {
            return new NewLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeParcelable(address, flags);
        dest.writeParcelable(geolocation, flags);
    }

    // endregion
}
