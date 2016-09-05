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

package pt.isel.s1516v.ps.apiaccess.viewmessages;

import android.os.Parcel;
import android.os.Parcelable;

public class TertuliaMessage implements Parcelable {

    @com.google.gson.annotations.SerializedName("message")
    public final String message;

    public TertuliaMessage(String message) {
        this.message = message;
    }

    // region Parcelable

    protected TertuliaMessage(Parcel in) {
        message = in.readString();
    }

    public static final Creator<TertuliaMessage> CREATOR = new Creator<TertuliaMessage>() {
        @Override
        public TertuliaMessage createFromParcel(Parcel in) {
            return new TertuliaMessage(in);
        }

        @Override
        public TertuliaMessage[] newArray(int size) {
            return new TertuliaMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(message);
    }

    // endregion

}
