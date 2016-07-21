package pt.isel.s1516v.ps.apiaccess.tertuliacreation.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;

public class CrWeeklySchedule implements Parcelable, Schedule {

    @com.google.gson.annotations.SerializedName("weekDay")
    public final String weekDay;
    @com.google.gson.annotations.SerializedName("skip")
    public final int skip;

    public CrWeeklySchedule(String weekDay, int skip) {
        this.weekDay = weekDay;
        this.skip = skip;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        CrWeeklySchedule other = (CrWeeklySchedule) obj;
        return other.weekDay.equals(weekDay) && other.skip == skip;
    }

    // region CrUiSchedule

    @Override
    public Date nextEvent() {
        return null;
    }

    // endregion

    // region Parcelable

    protected CrWeeklySchedule(Parcel in) {
        weekDay = in.readString();
        skip = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        out.writeString(weekDay);
        out.writeInt(skip);
    }

    public static final Creator<CrWeeklySchedule> CREATOR = new Parcelable.Creator<CrWeeklySchedule>() {
        @Override
        public CrWeeklySchedule createFromParcel(Parcel in) {
            return new CrWeeklySchedule(in);
        }

        @Override
        public CrWeeklySchedule[] newArray(int size) {
            return new CrWeeklySchedule[size];
        }
    };

    // endregion
}
