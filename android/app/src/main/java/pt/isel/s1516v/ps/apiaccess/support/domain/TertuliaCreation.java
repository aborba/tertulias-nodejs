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

public class TertuliaCreation extends TertuliaBase {

    public LocationCreation location;

    public TertuliaCreation(String name, String subject, boolean isPrivate, LocationCreation location, TertuliaSchedule schedule, SCHEDULES scheduleType, NewSchedule scheduleOLD) {
        super(name, subject, isPrivate, schedule, scheduleType);
        this.location = location;
    }

    public NewSchedule getSchedule() {
        return null;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || ! (obj instanceof TertuliaCreation))
            return false;
        TertuliaCreation other = (TertuliaCreation) obj;
        return other.name.equals(name);
    }

    // region Parcelable

    protected TertuliaCreation(Parcel in) {
        super(in);
        location = in.readParcelable(LocationCreation.class.getClassLoader());
    }

    public static final Creator<TertuliaCreation> CREATOR = new Creator<TertuliaCreation>() {
        @Override
        public TertuliaCreation createFromParcel(Parcel in) {
            return new TertuliaCreation(in);
        }

        @Override
        public TertuliaCreation[] newArray(int size) {
            return new TertuliaCreation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeParcelable(location, flags);
    }

    // endregion

}
