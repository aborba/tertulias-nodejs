/*
 * Copyright (c) 2016 António Borba da Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEdition;

public class Address implements Parcelable {

    public String address;
    public String zip;
    public String city;
    public String country;

    public Address(String address, String zip, String city, String country) {
        this.address = address;
        this.zip = zip;
        this.city = city;
        this.country = country;
    }

    public Address(ApiTertuliaEdition core) {
        address = core.lo_address;
        zip = core.lo_zip;
        city = core.lo_city;
        country = core.lo_country;
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
        if (obj == null || ! (obj instanceof Address))
            return false;
        Address other = (Address) obj;
        return other.address.equals(address) &&
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
