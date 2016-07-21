package pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.EditText;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;

public class CrUiGeolocation implements Parcelable {

    public final double latitude, longitude;
    public final boolean isLatitude, isLongitude;

    public CrUiGeolocation(EditText latitudeView, EditText longitudeView) {
        String value = latitudeView.getText().toString();
        isLatitude = !TextUtils.isEmpty(value);
        latitude = Util.string2Double(value);

        value = longitudeView.getText().toString();
        isLongitude = !TextUtils.isEmpty(value);
        longitude = Util.string2Double(value);
    }

    public void updateViews(EditText latitudeView, EditText longitudeView) {
        if (isLatitude)
            latitudeView.setText(String.valueOf(latitude));
        if (isLongitude)
            longitudeView.setText(String.valueOf(longitude));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        CrUiGeolocation other = (CrUiGeolocation) obj;
        return obj instanceof CrUiGeolocation &&
                other.isLatitude == isLatitude &&
                (isLatitude ? other.latitude == latitude : true) &&
                other.isLongitude == isLongitude &&
                isLongitude ? other.longitude == longitude : true;
    }

    // region Parcelable

    protected CrUiGeolocation(Parcel in) {
        latitude = in.readDouble();
        isLatitude = in.readByte() != 0;
        longitude = in.readDouble();
        isLongitude = in.readByte() != 0;
    }

    public static final Creator<CrUiGeolocation> CREATOR = new Creator<CrUiGeolocation>() {
        @Override
        public CrUiGeolocation createFromParcel(Parcel in) {
            return new CrUiGeolocation(in);
        }

        @Override
        public CrUiGeolocation[] newArray(int size) {
            return new CrUiGeolocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(latitude);
        out.writeByte((byte) (isLatitude ? 1 : 0));
        out.writeDouble(longitude);
        out.writeByte((byte) (isLongitude ? 1 : 0));
    }

    // endregion
}
