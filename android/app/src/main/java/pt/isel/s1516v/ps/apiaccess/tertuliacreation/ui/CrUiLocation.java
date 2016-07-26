package pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.EditText;
import android.widget.TextView;

public class CrUiLocation implements Parcelable {

    public String name;
    public CrUiAddress address;
    public CrUiGeolocation geo;

    public CrUiLocation(String locationView, String addressView, String zipView, String cityView, String countryView,
                        String latitudeView, String longitudeView) {
        name = locationView;
        address = new CrUiAddress(addressView, zipView, cityView, countryView);
        geo = new CrUiGeolocation(latitudeView, longitudeView);
    }

    public CrUiLocation(TextView locationView, TextView addressView, TextView zipView, TextView cityView, TextView countryView,
                        TextView latitudeView, TextView longitudeView) {
        name = locationView.getText().toString();
        address = new CrUiAddress(addressView, zipView, cityView, countryView);
        geo = new CrUiGeolocation(latitudeView, longitudeView);
    }

    public void updateViews(EditText locationView, EditText addressView, EditText zipView, EditText cityView, EditText countryView,
                            EditText latitudeView, EditText longitudeView) {
        locationView.setText(name);
        address.updateViews(addressView, zipView, cityView, countryView);
        geo.updateViews(latitudeView, longitudeView);
    }

    @Override
    public String toString() { return name + " (" + address + ")"; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        CrUiLocation other = (CrUiLocation) obj;
        return obj instanceof CrUiLocation && other.name == this.name && other.address.equals(address) && other.geo.equals(geo);
    }

    // region Parcelable

    protected CrUiLocation(Parcel in) {
        name = in.readString();
        address = in.readParcelable(CrUiAddress.class.getClassLoader());
        geo = in.readParcelable(CrUiGeolocation.class.getClassLoader());
    }

    public static final Creator<CrUiLocation> CREATOR = new Creator<CrUiLocation>() {
        @Override
        public CrUiLocation createFromParcel(Parcel in) {
            return new CrUiLocation(in);
        }

        @Override
        public CrUiLocation[] newArray(int size) {
            return new CrUiLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeParcelable(address, flags);
        out.writeParcelable(geo, flags);
    }

    // endregion
}
