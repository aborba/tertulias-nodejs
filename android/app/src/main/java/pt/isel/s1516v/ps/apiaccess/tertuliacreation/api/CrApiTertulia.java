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

package pt.isel.s1516v.ps.apiaccess.tertuliacreation.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;

public class CrApiTertulia implements Parcelable {

    @com.google.gson.annotations.SerializedName("tertulia_name")
    public final String tertuliaName;
    @com.google.gson.annotations.SerializedName("tertulia_subject")
    public final String subject;
    @com.google.gson.annotations.SerializedName("tertulia_isprivate")
    public final boolean isPrivate;


    @com.google.gson.annotations.SerializedName("location_name")
    public final String locationName;
    @com.google.gson.annotations.SerializedName("location_address")
    public final String streetAddress;
    @com.google.gson.annotations.SerializedName("location_zip")
    public final String zip;
    @com.google.gson.annotations.SerializedName("location_city")
    public final String city;
    @com.google.gson.annotations.SerializedName("location_country")
    public final String country;
    @com.google.gson.annotations.SerializedName("location_latitude")
    public final String latitude;
    @com.google.gson.annotations.SerializedName("location_longitude")
    public final String longitude;

    @com.google.gson.annotations.SerializedName("schedule_name")
    public final String scheduleType;

    @com.google.gson.annotations.SerializedName("links")
    public ApiLink[] links;

    public CrApiTertulia(String tertuliaName, String subject, boolean isPrivate,
                         String locationName, String streetAddress, String zip, String city, String country,
                         String latitude, String longitude,
                         String scheduleType
        ) {
        this.tertuliaName = tertuliaName;
        this.subject = subject;
        this.isPrivate = isPrivate;
        this.locationName = locationName != null ? locationName: streetAddress;
        this.streetAddress = streetAddress;
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.scheduleType = scheduleType;
    }

    // region Parcelable

    protected CrApiTertulia(Parcel in) {
        tertuliaName = in.readString();
        subject = in.readString();
        isPrivate = in.readByte() != 0;
        locationName = in.readString();
        streetAddress = in.readString();
        zip = in.readString();
        city = in.readString();
        country = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        scheduleType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        out.writeString(tertuliaName);
        out.writeString(subject);
        out.writeByte((byte) (isPrivate ? 1 : 0));
        out.writeString(locationName);
        out.writeString(streetAddress);
        out.writeString(zip);
        out.writeString(city);
        out.writeString(country);
        out.writeString(latitude);
        out.writeString(longitude);
        out.writeString(scheduleType);
    }

    public static final Creator<CrApiTertulia> CREATOR = new Creator<CrApiTertulia>() {
        @Override
        public CrApiTertulia createFromParcel(Parcel in) {
            return new CrApiTertulia(in);
        }

        @Override
        public CrApiTertulia[] newArray(int size) {
            return new CrApiTertulia[size];
        }
    };

    // endregion

}
