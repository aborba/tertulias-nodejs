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

import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEdition;

public class LocationEdition extends LocationCreation {

    public int id;

    public LocationEdition(int id, String name, Address address, Geolocation geolocation) {
        super(name, address, geolocation);
        this.id = id;
    }

    public LocationEdition(ApiTertuliaEdition apiTertuliaEdition) {
        this(Integer.parseInt(apiTertuliaEdition.lo_id), apiTertuliaEdition.lo_name,
                new Address(apiTertuliaEdition.lo_address, apiTertuliaEdition.lo_zip, apiTertuliaEdition.lo_city, apiTertuliaEdition.lo_country),
                new Geolocation(apiTertuliaEdition.lo_latitude, apiTertuliaEdition.lo_longitude));
    }

    @Override
    public String toString() { return name + " (" + address + ")"; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || ! (obj instanceof LocationEdition))
            return false;
        LocationEdition other = (LocationEdition) obj;
        return other.id == this.id && other.name.equals(name);
    }

    // region Parcelable

    protected LocationEdition(Parcel in) {
        super(in);
        id = in.readInt();
    }

    public static final Creator<LocationEdition> CREATOR = new Creator<LocationEdition>() {
        @Override
        public LocationEdition createFromParcel(Parcel in) {
            return new LocationEdition(in);
        }

        @Override
        public LocationEdition[] newArray(int size) {
            return new LocationEdition[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(id);
    }

    // endregion
}
