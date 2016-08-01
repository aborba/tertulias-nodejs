package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;
import android.os.Parcelable;

import pt.isel.s1516v.ps.apiaccess.support.raw.RLocation;
import pt.isel.s1516v.ps.apiaccess.support.raw.RTertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaListItem;

public class LocationCreation implements Parcelable {

    public String name;
    public Address address;
    public Geolocation geolocation;

    public LocationCreation(String name, Address address, Geolocation geolocation) {
        this.name = name;
        this.address = address;
        this.geolocation = geolocation;
    }

    public LocationCreation(RLocation rlocation) {
        name = rlocation.name;
        address = new Address(rlocation);
        geolocation = new Geolocation(rlocation);
    }

    public LocationCreation(ApiTertuliaListItem apiTertuliaListItem) {
        name = apiTertuliaListItem.eventLocation;
    }

    public LocationCreation(RTertulia rtertulia) {
        name = rtertulia.locationName;
        address = new Address(rtertulia);
        geolocation = new Geolocation(rtertulia);
    }

    public LocationCreation(ApiTertuliaEdition core) {
        name = core.lo_name;
        address = new Address(core);
        geolocation = new Geolocation(core);
    }

    @Override
    public String toString() { return name + " (" + address + ")"; }

    // region Parcelable

    protected LocationCreation(Parcel in) {
        name = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        geolocation = in.readParcelable(Geolocation.class.getClassLoader());
    }

    public static final Creator<LocationCreation> CREATOR = new Creator<LocationCreation>() {
        @Override
        public LocationCreation createFromParcel(Parcel in) {
            return new LocationCreation(in);
        }

        @Override
        public LocationCreation[] newArray(int size) {
            return new LocationCreation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(address, flags);
        dest.writeParcelable(geolocation, flags);
    }

    // endregion
}
