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

public class ContactListItem implements Parcelable {

    public final int id;
    public final String name;
    public final String email;
    public final String photo;

    public ContactListItem(int id, String name, String email, String photo) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.photo = photo;
    }

    @Override
    public String toString() {
        return name + " " + email + " " + photo;
    }

    // region Parcelable

    protected ContactListItem(Parcel in) {
        id = in.readInt();
        name = in.readString();
        email = in.readString();
        photo = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeString(email);
        out.writeString(photo);
    }

    public static final Creator<ContactListItem> CREATOR = new Creator<ContactListItem>() {
        @Override
        public ContactListItem createFromParcel(Parcel in) {
            return new ContactListItem(in);
        }

        @Override
        public ContactListItem[] newArray(int size) {
            return new ContactListItem[size];
        }
    };    // endregion
}
