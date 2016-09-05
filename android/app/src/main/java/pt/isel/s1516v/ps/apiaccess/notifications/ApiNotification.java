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

package pt.isel.s1516v.ps.apiaccess.notifications;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class ApiNotification implements Parcelable {

    @com.google.gson.annotations.SerializedName("myKey")
    public final String myKey;
    @com.google.gson.annotations.SerializedName("action")
    public final String action;
    @com.google.gson.annotations.SerializedName("tertulia")
    public final int tertulia;

    public ApiNotification(String myKey, String action, int tertulia) {
        this.myKey = myKey;
        this.action = action;
        this.tertulia = tertulia;
    }

    // region Parcelable

    protected ApiNotification(Parcel in) {
        myKey = in.readString();
        action = in.readString();
        tertulia = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        out.writeString(myKey);
        out.writeString(action);
        out.writeInt(tertulia);
    }

    public static final Creator<ApiNotification> CREATOR = new Creator<ApiNotification>() {
        @Override
        public ApiNotification createFromParcel(Parcel in) {
            return new ApiNotification(in);
        }

        @Override
        public ApiNotification[] newArray(int size) {
            return new ApiNotification[size];
        }
    };    // endregion
}
