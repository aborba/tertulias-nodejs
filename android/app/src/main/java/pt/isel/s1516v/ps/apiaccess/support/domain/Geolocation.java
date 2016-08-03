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

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.TertuliasApplication;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;

public class Geolocation implements Parcelable {

    public double latitude, longitude;
    public boolean isLatitude, isLongitude;

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

    public boolean isDefined() {
        return isLatitude && isLongitude;
    }

    public String getLatitude() {
        return isLatitude ? String.valueOf(latitude) : "";
    }

    public String getLongitude() {
        return isLongitude ? String.valueOf(longitude) : "";
    }

    @Override
    public String toString() {
        return isDefined() ? getLatitude() + " " + getLongitude() :
                TertuliasApplication.getApplication().getResources().getString(R.string.undefined);
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
