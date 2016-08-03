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

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.TertuliasApplication;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiScheduleEditionWeekly;

public class TertuliaScheduleWeekly implements TertuliaSchedule {
    public final int weekday;
    public final int skip;

    public TertuliaScheduleWeekly(int weekday, int skip) {
        this.weekday = weekday;
        this.skip = skip;
    }

    public TertuliaScheduleWeekly(ApiScheduleEditionWeekly weekly) {
        weekday = weekly.sc_weekday - 1;
        skip = weekly.sc_skip;
    }

    // region TertuliaSchedule

    @Override
    public String toString() {
        Context ctx = TertuliasApplication.getApplication().getApplicationContext();
        Resources res = ctx.getResources();
        String[] parts = res.getStringArray(R.array.new_tertulia_dialog_weekly_tostring);
        String[] weekdays = res.getStringArray(R.array.new_monthlyw_weekday);
        String result = parts[0];
        result += skip == 0 ? parts[1] : String.format(Locale.getDefault(), parts[2], skip + 1);
        result += String.format(parts[3], weekdays[weekday]);
        result += ".";
        return result;
    }

    @Override
    public SCHEDULES getType() {
        return SCHEDULES.WEEKLY;
    }

    // endregion

    // region Parcelable

    protected TertuliaScheduleWeekly(Parcel in) {
        weekday = in.readInt();
        skip = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        out.writeInt(weekday);
        out.writeInt(skip);
    }

    public static final Creator<TertuliaScheduleWeekly> CREATOR = new Creator<TertuliaScheduleWeekly>() {
        @Override
        public TertuliaScheduleWeekly createFromParcel(Parcel in) {
            return new TertuliaScheduleWeekly(in);
        }

        @Override
        public TertuliaScheduleWeekly[] newArray(int size) {
            return new TertuliaScheduleWeekly[size];
        }
    };

    // endregion
}
