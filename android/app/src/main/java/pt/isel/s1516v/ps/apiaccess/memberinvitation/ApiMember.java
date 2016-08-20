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

package pt.isel.s1516v.ps.apiaccess.memberinvitation;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;

public class ApiMember implements Parcelable {

    @com.google.gson.annotations.SerializedName("id")
    public final int id;
    @com.google.gson.annotations.SerializedName("sid")
    public final String sid;
    @com.google.gson.annotations.SerializedName("alias")
    public final String alias;
    @com.google.gson.annotations.SerializedName("firstName")
    public final String firstName;
    @com.google.gson.annotations.SerializedName("lastName")
    public final String lastName;
    @com.google.gson.annotations.SerializedName("email")
    public final String email;
    @com.google.gson.annotations.SerializedName("picture")
    public final String photo;
    @com.google.gson.annotations.SerializedName("role")
    public final String role;
    @com.google.gson.annotations.SerializedName("links")
    public final ApiLink[] links;

    public ApiMember(int id, String sid, String alias, String firstName, String lastName, String email, String photo, String role, ApiLink[] links) {
        this.id = id;
        this.sid = sid;
        this.alias = alias;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.photo = photo;
        this.role = role;
        this.links = links;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + ", " + email + ", " + role;
    }

    public String getName() {
        if (alias != null)
            return alias;
        String name = firstName;
        if (name == null)
            name = lastName;
        else if (lastName != null)
            name += " " + lastName;
        if (name == null)
            name = email;
        return name;
    }

    // region Parcelable

    protected ApiMember(Parcel in) {
        id = in.readInt();
        sid = in.readString();
        alias = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        photo = in.readString();
        role = in.readString();
        links = in.createTypedArray(ApiLink.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(sid);
        out.writeString(alias);
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(email);
        out.writeString(photo);
        out.writeString(role);
        out.writeTypedArray(links, flags);
    }

    public static final Creator<ApiMember> CREATOR = new Creator<ApiMember>() {
        @Override
        public ApiMember createFromParcel(Parcel in) {
            return new ApiMember(in);
        }

        @Override
        public ApiMember[] newArray(int size) {
            return new ApiMember[size];
        }
    };    // endregion
}
