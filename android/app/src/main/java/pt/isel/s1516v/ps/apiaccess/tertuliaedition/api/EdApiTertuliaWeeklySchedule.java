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

package pt.isel.s1516v.ps.apiaccess.tertuliaedition.api;

import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaScheduleWeekly;

public class EdApiTertuliaWeeklySchedule extends EdApiTertulia implements Schedule {

    @com.google.gson.annotations.SerializedName("schedule_weekday")
    public final int weekDay;
    @com.google.gson.annotations.SerializedName("schedule_skip")
    public final int skip;

    public EdApiTertuliaWeeklySchedule(int tertuliaId, String name, String subject, boolean isPrivate,
                                       String roleType,
                                       int locationId, String location, String address,
                                       String zip, String city, String country,
                                       String latitude, String longitude,
                                       int weekDay, int skip,
                                       String scheduleType, String myKey) {
        super(tertuliaId, name, subject, isPrivate,
                roleType,
                locationId, location, address, zip, city, country,
                latitude, longitude,
                scheduleType,
                myKey);
        this.weekDay = weekDay;
        this.skip = skip;
    }

    public EdApiTertuliaWeeklySchedule(TertuliaEdition tertulia, String mykey) {
        super(tertulia.id, tertulia.name, tertulia.subject, tertulia.isPrivate,
                tertulia.role.name,
                tertulia.location.id, tertulia.location.name, tertulia.location.address.address, tertulia.location.address.zip,
                tertulia.location.address.city, tertulia.location.address.country,
                tertulia.location.geolocation.getLatitude(), tertulia.location.geolocation.getLongitude(),
                tertulia.tertuliaSchedule.getType().name(),
                mykey);
        TertuliaScheduleWeekly schedule = (TertuliaScheduleWeekly)tertulia.tertuliaSchedule;
        weekDay = schedule.weekday + 1;
        skip = schedule.skip;
    }

   // region EdApiTertulia

//    @Override
    protected String toStringContribution() { return null; }

    // endregion

    // region Schedule

    @Override
    public Date nextEvent() {
        return null;
    }

    // endregion

    // region Parcelable

    protected EdApiTertuliaWeeklySchedule(Parcel in) {
        super(in);
        weekDay = in.readInt();
        skip = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(weekDay);
        out.writeInt(skip);
    }

    public static final Creator<EdApiTertuliaWeeklySchedule> CREATOR = new Creator<EdApiTertuliaWeeklySchedule>() {
        @Override
        public EdApiTertuliaWeeklySchedule createFromParcel(Parcel in) {
            return new EdApiTertuliaWeeklySchedule(in);
        }

        @Override
        public EdApiTertuliaWeeklySchedule[] newArray(int size) {
            return new EdApiTertuliaWeeklySchedule[size];
        }
    };

    // endregion
}
