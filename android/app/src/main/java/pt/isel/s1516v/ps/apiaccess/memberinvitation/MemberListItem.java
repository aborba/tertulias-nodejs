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

public class MemberListItem implements Parcelable {

    @com.google.gson.annotations.SerializedName("id")
    public final int id;
    @com.google.gson.annotations.SerializedName("sid")
    public final String sid;
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

    public MemberListItem(int id, String sid, String firstName, String lastName, String email, String photo, String role) {
        this.id = id;
        this.sid = sid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.photo = photo;
        this.role = role;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + ", " + email + ", " + role;
    }

    // region Parcelable

    protected MemberListItem(Parcel in) {
        id = in.readInt();
        sid = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        photo = in.readString();
        role = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(sid);
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(email);
        out.writeString(photo);
        out.writeString(role);
    }

    public static final Creator<MemberListItem> CREATOR = new Creator<MemberListItem>() {
        @Override
        public MemberListItem createFromParcel(Parcel in) {
            return new MemberListItem(in);
        }

        @Override
        public MemberListItem[] newArray(int size) {
            return new MemberListItem[size];
        }
    };    // endregion
}
