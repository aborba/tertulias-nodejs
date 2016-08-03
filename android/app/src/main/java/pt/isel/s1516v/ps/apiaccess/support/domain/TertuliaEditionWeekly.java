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
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.TertuliasApplication;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundleWeekly;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiWeekly;

public class TertuliaEditionWeekly extends TertuliaEdition {
    public final int schedule_id;
    public final int weekday;
    public final int skip;

    public TertuliaEditionWeekly(ApiTertuliaEdition apiTertuliaEdition, ApiLink[] links, int schedule_id, int weekday, int skip) {
        super(apiTertuliaEdition, links);
        this.schedule_id = schedule_id;
        this.weekday = weekday;
        this.skip = skip;
    }

    public TertuliaEditionWeekly(ApiTertuliaEditionBundleWeekly apiReadTertuliaWeekly) {
        this(apiReadTertuliaWeekly.tertulia, apiReadTertuliaWeekly.links,
                apiReadTertuliaWeekly.weekly.sc_id, apiReadTertuliaWeekly.weekly.sc_weekday, apiReadTertuliaWeekly.weekly.sc_skip);
    }

    public TertuliaEditionWeekly(TertuliaEdition tertulia, CrUiWeekly weekly) {
        super(tertulia.id, tertulia.name, tertulia.subject, tertulia.isPrivate,
                tertulia.role,
                tertulia.location,
                null,
                tertulia.scheduleType.name(),
                tertulia.links);
        schedule_id = -1;
        weekday = weekly.weekDayNr + 1; // TODO: VERIFY
        skip = weekly.skip;
    }

    public String getWeekDay(Context ctx) {
        return ctx.getResources().getStringArray(R.array.new_monthlyw_weekday)[weekday - 1];
    }

    @Override
    public String toString() {
        Context ctx = TertuliasApplication.getApplication().getApplicationContext();
        Resources res = ctx.getResources();
        String[] parts = res.getStringArray(R.array.new_tertulia_dialog_weekly_tostring);
        String[] weekdays = res.getStringArray(R.array.new_monthlyw_weekday);
        String result = parts[0];
        result += skip == 0 ? parts[1] : String.format(Locale.getDefault(), parts[2], skip + 1);
        result += String.format(parts[3], weekdays[weekday - 1]);
        result += ".";
        return result;
    }

    // region Parcelable

    protected TertuliaEditionWeekly(Parcel in) {
        super(in);
        schedule_id = in.readInt();
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
        out.writeInt(schedule_id);
        out.writeInt(weekday);
        out.writeInt(skip);
    }

    public static final Creator<TertuliaEditionWeekly> CREATOR = new Parcelable.Creator<TertuliaEditionWeekly>() {
        @Override
        public TertuliaEditionWeekly createFromParcel(Parcel in) {
            return new TertuliaEditionWeekly(in);
        }

        @Override
        public TertuliaEditionWeekly[] newArray(int size) {
            return new TertuliaEditionWeekly[size];
        }
    };

    // endregion
}
