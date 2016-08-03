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
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiWeekly;

public class TertuliaCreationWeekly extends TertuliaCreation {
    public final int weekday;
    public final int skip;

    public TertuliaCreationWeekly(String name, String subject, boolean isPrivate,
                                  LocationCreation location,
                                  SCHEDULES scheduleType, NewSchedule schedule,
                                  int weekday, int skip) {
        super(name, subject, isPrivate, location, null, scheduleType, schedule);
        this.weekday = weekday;
        this.skip = skip;
    }

    public TertuliaCreationWeekly(TertuliaCreation tertulia, TertuliaSchedule schedule, CrUiWeekly scheduleOLD) {
        super(tertulia.name, tertulia.subject, tertulia.isPrivate, tertulia.location, schedule, scheduleOLD.getScheduleType(), tertulia.getSchedule());
        this.weekday = scheduleOLD.weekDayNr;
        this.skip = scheduleOLD.skip;
    }

    public TertuliaCreationWeekly(TertuliaCreation tertulia) {
        this(tertulia, null, new CrUiWeekly(((TertuliaCreationWeekly) tertulia).weekday, ((TertuliaCreationWeekly) tertulia).skip));
    }

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

    // region Parcelable

    protected TertuliaCreationWeekly(Parcel in) {
        super(in);
        weekday = in.readInt();
        skip = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(weekday);
        out.writeInt(skip);
    }

    public static final Creator<TertuliaCreationWeekly> CREATOR = new Creator<TertuliaCreationWeekly>() {
        @Override
        public TertuliaCreationWeekly createFromParcel(Parcel in) {
            return new TertuliaCreationWeekly(in);
        }

        @Override
        public TertuliaCreationWeekly[] newArray(int size) {
            return new TertuliaCreationWeekly[size];
        }
    };

    // endregion
}
