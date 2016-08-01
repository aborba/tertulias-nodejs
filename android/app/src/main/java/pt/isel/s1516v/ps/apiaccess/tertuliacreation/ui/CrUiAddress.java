package pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

public class CrUiAddress implements Parcelable {

    public final String address;
    public final String zip;
    public final String city;
    public final String country;

    public CrUiAddress(String address, String zip, String city, String country) {
        this.address = address;
        this.zip = zip;
        this.city = city;
        this.country = country;
    }

    public CrUiAddress(TextView addressView, TextView zipView, TextView cityView, TextView countryView) {
        this(addressView.getText().toString(),
                zipView.getText().toString(),
                cityView.getText().toString(),
                countryView.getText().toString());
    }

    public CrUiAddress(String fullAddress) {
        String xAddress = null;
        String xZip = null;
        String xCity = null;
        String xCountry = null;
        String[] parts = fullAddress.split(",\\s*");
        switch (parts.length) {
            case 1: // ", ": 0
                String[] subparts = parts[0].split("\\s+");
                switch (subparts.length) {
                    case 1: // " ": 0
                        if (subparts[0].contains("-"))
                            xZip = subparts[0];
                        else
                            xCity = subparts[0];
                        break;
                    case 2: // " ": 1
                        if (subparts[0].contains("-")) {
                            xZip = subparts[0];
                            xCity = subparts[1];
                        } else {
                            xCity = subparts[0];
                            xCountry = subparts[1];
                        }
                        break;
                    default:
                        xAddress = parts[0];
                }
                break;
            case 2: // ", ": 1
                String[] subparts1 = parts[1].split("\\s+");
                if (subparts1[0].contains("-")) {
                    xZip = subparts1[0];
                    xCity = subparts1[1];
                    xAddress = parts[0];
                } else {
                    xCountry = parts[1];
                    subparts1 = parts[0].split("\\s+");
                    if (subparts1[0].contains("-")) {
                        xZip = subparts1[0];
                        xCity = subparts1[1];
                    } else
                        xAddress = parts[0];
                }
                break;
            default:
                xCountry = parts[parts.length - 1];
                String[] subparts2 = parts[parts.length - 2].split("\\s+");
                if (subparts2.length == 1) {
                    if (subparts2[0].contains("-"))
                        xCity = subparts2[0];
                    else
                        xZip = subparts2[0];
                } else {
                    xZip = subparts2[0];
                    xCity = subparts2[1];
                }
                String tmpAddress = parts[0];
                for (int i = 1; i < parts.length - 2; i++)
                    tmpAddress += ", " + parts[i];
                xAddress = tmpAddress;
        }
        address = xAddress;
        zip = xZip;
        city = xCity;
        country = xCountry;
    }

    public void updateViews(EditText addressView, EditText zipView, EditText cityView, EditText countryView) {
        addressView.setText(address);
        zipView.setText(zip);
        cityView.setText(city);
        countryView.setText(country);
    }

    private static String compose(String separator, String begin, String end) {
        return TextUtils.isEmpty(begin) ? end : begin + separator + end;
    }

    @Override
    public String toString() {
        return compose(", ", compose(", ", address, compose(", ", zip, city)), country);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        CrUiAddress other = (CrUiAddress) obj;
        return obj instanceof CrUiAddress &&
                other.address.equals(address) &&
                other.zip.equals(zip) &&
                other.city.equals(city) &&
                other.country.equals(country);
    }

    // region Parcelable

    protected CrUiAddress(Parcel in) {
        address = in.readString();
        zip = in.readString();
        city = in.readString();
        country = in.readString();
    }

    public static final Creator<CrUiAddress> CREATOR = new Creator<CrUiAddress>() {
        @Override
        public CrUiAddress createFromParcel(Parcel in) {
            return new CrUiAddress(in);
        }

        @Override
        public CrUiAddress[] newArray(int size) {
            return new CrUiAddress[size];
        }
    };

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
