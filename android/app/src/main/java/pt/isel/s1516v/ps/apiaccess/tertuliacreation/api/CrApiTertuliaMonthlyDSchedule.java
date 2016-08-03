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

package pt.isel.s1516v.ps.apiaccess.tertuliacreation.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaCreation;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaScheduleMonthlyD;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthly;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;

public class CrApiTertuliaMonthlyDSchedule extends CrApiTertulia implements Parcelable, Schedule {

    @com.google.gson.annotations.SerializedName("schedule_daynr")
    public final int dayNr;
    @com.google.gson.annotations.SerializedName("schedule_isfromstart")
    public final boolean isFromStart;
    @com.google.gson.annotations.SerializedName("schedule_skip")
    public final int skip;

    public CrApiTertuliaMonthlyDSchedule(String name, String subject, boolean isPrivate,
                                         String location, String address, String zip, String city, String country,
                                         String latitude, String longitude,
                                         int dayNr, boolean isFromStart, int skip) {
        super(name, subject, isPrivate,
                location, address, zip, city, country,
                latitude, longitude,
                TertuliasApi.SCHEDULES.MONTHLYD.name());
        this.dayNr = dayNr;
        this.isFromStart = isFromStart;
        this.skip = skip;
    }

    public CrApiTertuliaMonthlyDSchedule(TertuliaCreation tertulia) {
        super(tertulia.name, tertulia.subject, tertulia.isPrivate,
                tertulia.location.name, tertulia.location.address.address, tertulia.location.address.zip,
                tertulia.location.address.city, tertulia.location.address.country,
                tertulia.location.geolocation.getLatitude(), tertulia.location.geolocation.getLongitude(),
                tertulia.tertuliaSchedule.getType().name());
        this.dayNr = ((TertuliaScheduleMonthlyD)tertulia.tertuliaSchedule).dayNr;
        this.isFromStart = ((TertuliaScheduleMonthlyD)tertulia.tertuliaSchedule).isFromStart;
        this.skip = ((TertuliaScheduleMonthlyD)tertulia.tertuliaSchedule).skip;
    }

    public CrApiTertuliaMonthlyDSchedule(CrUiTertulia crUiTertulia, CrUiMonthly crUiMonthly) {
        this(crUiTertulia.name, crUiTertulia.subject, crUiTertulia.isPrivate,
                crUiTertulia.crUiLocation.name, crUiTertulia.crUiLocation.address.address, crUiTertulia.crUiLocation.address.zip, crUiTertulia.crUiLocation.address.city, crUiTertulia.crUiLocation.address.country,
                String.valueOf(crUiTertulia.crUiLocation.geo.latitude), String.valueOf(crUiTertulia.crUiLocation.geo.longitude),
                crUiMonthly.dayNr, crUiMonthly.isFromStart, crUiMonthly.skip);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || ! (obj instanceof CrApiTertuliaMonthlyDSchedule))
            return false;
        CrApiTertuliaMonthlyDSchedule other = (CrApiTertuliaMonthlyDSchedule) obj;
        return other.dayNr == dayNr && other.isFromStart == isFromStart && other.skip == skip;
    }

    // region CrApiTertulia

//    @Override
    protected String toStringContribution() { return null; }

    // endregion

    // region CrUiSchedule

    @Override
    public Date nextEvent() {
        return null;
    }

    // endregion

    // region Parcelable

    protected CrApiTertuliaMonthlyDSchedule(Parcel in) {
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

    public static final Creator<CrApiTertuliaMonthlyDSchedule> CREATOR = new Creator<CrApiTertuliaMonthlyDSchedule>() {
        @Override
        public CrApiTertuliaMonthlyDSchedule createFromParcel(Parcel in) {
            return new CrApiTertuliaMonthlyDSchedule(in);
        }

        @Override
        public CrApiTertuliaMonthlyDSchedule[] newArray(int size) {
            return new CrApiTertuliaMonthlyDSchedule[size];
        }
    };

    // endregion
}
