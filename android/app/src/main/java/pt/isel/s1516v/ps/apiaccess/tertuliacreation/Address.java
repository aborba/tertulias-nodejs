package pt.isel.s1516v.ps.apiaccess.tertuliacreation;

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

    public Address(String complete) {
        String[] addressParts = complete.split(",\\s*");
        switch(addressParts.length) {
            case 1:
                zip = city = null;
                String[] words = addressParts[0].split("\\s");
                if (words.length == 1) {
                    address = null;
                    country = words[0];
                } else {
                    address = words[0];
                    country = null;
                }
                break;
            case 2:
                country = addressParts[1];
                if (addressParts[0].charAt(4) == '-' && addressParts[0].charAt(8) == ' ') {
                    address = null;
                    String[] zipParts = addressParts[0].split("\\s");
                    zip = zipParts[0];
                    city = zipParts[1];
                } else {
                    address = addressParts[0];
                    zip = city = null;
                }
                break;
            case 3:
                address = addressParts[0];
                String[] zipParts = addressParts[1].split("\\s");
                zip = zipParts[0];
                city = zipParts[1];
                country = addressParts[2];
                break;
            default:
                String daddress = addressParts[0];
                for (int i = 1; i < addressParts.length - 3; i++)
                    daddress += ", " + addressParts[i];
                address = daddress;
                String[] zipParts_d = addressParts[addressParts.length - 2].split("\\s");
                zip = zipParts_d[0];
                city = zipParts_d[1];
                country = addressParts[addressParts.length - 1];
        }
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
