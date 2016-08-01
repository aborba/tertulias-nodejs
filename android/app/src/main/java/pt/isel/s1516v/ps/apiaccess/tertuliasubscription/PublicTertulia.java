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

package pt.isel.s1516v.ps.apiaccess.tertuliasubscription;

import android.os.Parcel;
import android.os.Parcelable;

import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.tertuliasubscription.gson.ApiSearchListItem;

public class PublicTertulia implements Parcelable {

    public final String id;
    public final String name;
    public final String subject;
    public final String location;
    public final String schedule;
    public ApiLink[] links;

    public PublicTertulia(ApiSearchListItem item) {
        id = item.id;
        name = item.name;
        subject = item.subject;
        location = item.location;
        schedule = item.schedule;
        links = item.links;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        PublicTertulia other = (PublicTertulia) obj;
        return obj instanceof PublicTertulia && other.id == this.id && other.name == this.name;
    }

    // region Parcelable

    protected PublicTertulia(Parcel in) {
        id = in.readString();
        name = in.readString();
        subject = in.readString();
        location = in.readString();
        schedule = in.readString();
        Parcelable[] parcelableLinks = in.readParcelableArray(ApiLink.class.getClassLoader());
        links = new ApiLink[parcelableLinks.length];
        for (int i = 0; i < parcelableLinks.length; i++)
            links[i] = (ApiLink) parcelableLinks[i];
    }

    public static final Creator<PublicTertulia> CREATOR = new Creator<PublicTertulia>() {
        @Override
        public PublicTertulia createFromParcel(Parcel in) {
            return new PublicTertulia(in);
        }

        @Override
        public PublicTertulia[] newArray(int size) {
            return new PublicTertulia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(subject);
        dest.writeString(location);
        dest.writeString(schedule);
        dest.writeParcelableArray(links, flags);
    }

    // endregion

}
