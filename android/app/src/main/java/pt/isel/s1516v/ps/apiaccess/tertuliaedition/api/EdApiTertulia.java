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

package pt.isel.s1516v.ps.apiaccess.tertuliaedition.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import pt.isel.s1516v.ps.apiaccess.tertuliacreation.api.CrApiTertulia;

public class EdApiTertulia extends CrApiTertulia implements Parcelable {

    @com.google.gson.annotations.SerializedName("tertulia_id")
    public final int tertuliaId;
    @com.google.gson.annotations.SerializedName("role_type")
    public final String roleType;
    @com.google.gson.annotations.SerializedName("location_id")
    public final int locationId;
    @com.google.gson.annotations.SerializedName("myKey")
    public final String mykey;

    public EdApiTertulia(int tertuliaId, String tertuliaName, String subject, boolean isPrivate,
                         String roleType,
                         int locationId, String locationName, String streetAddress, String zip, String city, String country,
                         String latitude, String longitude,
                         String scheduleType, String mykey) {
        super(tertuliaName, subject, isPrivate,
                locationName, streetAddress, zip, city, country,
                latitude, longitude,
                scheduleType);
        this.tertuliaId = tertuliaId;
        this.locationId = locationId;
        this.roleType = roleType;
        this.mykey = mykey;
    }

    @Override
    public String toString() {
        return tertuliaName;
    }

    // region Parcelable

    protected EdApiTertulia(Parcel in) {
        super(in);
        tertuliaId = in.readInt();
        roleType = in.readString();
        locationId = in.readInt();
        mykey = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(tertuliaId);
        out.writeString(roleType);
        out.writeInt(locationId);
        out.writeString(mykey);
    }

    public static final Creator<EdApiTertulia> CREATOR = new Creator<EdApiTertulia>() {
        @Override
        public EdApiTertulia createFromParcel(Parcel in) {
            return new EdApiTertulia(in);
        }

        @Override
        public EdApiTertulia[] newArray(int size) {
            return new EdApiTertulia[size];
        }
    };

    // endregion

}
