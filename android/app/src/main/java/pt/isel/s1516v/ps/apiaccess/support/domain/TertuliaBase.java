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
import android.os.Parcelable;

import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;

public class TertuliaBase implements Parcelable, TertuliasApi {

    public String name;
    public String subject;
    public boolean isPrivate;
    public TertuliaSchedule tertuliaSchedule;
    public final SCHEDULES scheduleType;

    public TertuliaBase(String name, String subject, boolean isPrivate, TertuliaSchedule schedule) {
        this.name = name;
        this.subject = subject;
        this.isPrivate = isPrivate;
        this.tertuliaSchedule = schedule;
        this.scheduleType = null;
    }

    public TertuliaBase(String name, String subject, boolean isPrivate, TertuliaSchedule schedule, SCHEDULES scheduleType) {
        this.name = name;
        this.subject = subject;
        this.isPrivate = isPrivate;
        this.tertuliaSchedule = schedule;
        this.scheduleType = scheduleType;
    }

    @Override
    public String toString() { return name; }

    // region Parcelable

    protected TertuliaBase(Parcel in) {
        name = in.readString();
        subject = in.readString();
        isPrivate = in.readByte() != 0;
        switch (in.readString()) {
            case "UNDEFINED":
                tertuliaSchedule = null;
                break;
            case "WEEKLY":
                tertuliaSchedule = new TertuliaScheduleWeekly(in);
                break;
            case "MONTHLYD":
                tertuliaSchedule = new TertuliaScheduleMonthlyD(in);
                break;
            case "MONTHLYW":
                tertuliaSchedule = new TertuliaScheduleMonthlyW(in);
                break;
            case "YEARLY":
            case "YEARLYW":
                throw new UnsupportedOperationException();
            default:
                throw new IllegalStateException();
        }
        String value = in.readString();
        scheduleType = value.equals("UNDEFINED") ? null : SCHEDULES.valueOf(value);
    }

    public static final Creator<TertuliaBase> CREATOR = new Creator<TertuliaBase>() {
        @Override
        public TertuliaBase createFromParcel(Parcel in) {
            return new TertuliaBase(in);
        }

        @Override
        public TertuliaBase[] newArray(int size) {
            return new TertuliaBase[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(subject);
        out.writeByte((byte) (isPrivate ? 1 : 0));
        out.writeString(tertuliaSchedule == null ? "UNDEFINED" : tertuliaSchedule.getType().name());
        if (tertuliaSchedule != null)
            tertuliaSchedule.writeToParcel(out, flags);
        out.writeString(scheduleType == null ? "UNDEFINED" : scheduleType.name());
    }

    // endregion

}
