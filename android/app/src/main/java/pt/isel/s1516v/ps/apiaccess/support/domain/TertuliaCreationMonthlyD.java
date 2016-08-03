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
import android.support.annotation.NonNull;

public class TertuliaCreationMonthlyD extends TertuliaCreation {
    public final int dayNr;
    public final boolean isFromStart;
    public final int skip;

    // region Parcelable

    protected TertuliaCreationMonthlyD(Parcel in) {
        super(in);
        dayNr = in.readInt();
        isFromStart = in.readByte() != 0;
        skip = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(dayNr);
        out.writeByte((byte) (isFromStart ? 1 : 0));
        out.writeInt(skip);
    }

    public static final Creator<TertuliaCreationMonthlyD> CREATOR = new Creator<TertuliaCreationMonthlyD>() {
        @Override
        public TertuliaCreationMonthlyD createFromParcel(Parcel in) {
            return new TertuliaCreationMonthlyD(in);
        }

        @Override
        public TertuliaCreationMonthlyD[] newArray(int size) {
            return new TertuliaCreationMonthlyD[size];
        }
    };

    // endregion
}
