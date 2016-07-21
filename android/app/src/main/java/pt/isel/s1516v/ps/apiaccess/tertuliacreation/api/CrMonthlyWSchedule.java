package pt.isel.s1516v.ps.apiaccess.tertuliacreation.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;

public class CrMonthlyWSchedule implements Parcelable, Schedule {

    @com.google.gson.annotations.SerializedName("weekDay")
    public final String weekDay;
    @com.google.gson.annotations.SerializedName("weekNr")
    public final int weekNr;
    @com.google.gson.annotations.SerializedName("fromStart")
    public final boolean fromStart;
    @com.google.gson.annotations.SerializedName("skip")
    public final int skip;

    public CrMonthlyWSchedule(String weekDay, int weekNr, boolean fromStart, int skip) {
        this.weekDay = weekDay;
        this.weekNr = weekNr;
        this.fromStart = fromStart;
        this.skip = skip;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        CrMonthlyWSchedule other = (CrMonthlyWSchedule) obj;
        return other.weekDay.equals(weekDay) && other.weekNr == weekNr && other.fromStart == fromStart && other.skip == skip;
    }

    // region CrUiSchedule

    @Override
    public Date nextEvent() {
        return null;
    }

    // endregion

    // region Parcelable

    protected CrMonthlyWSchedule(Parcel in) {
        weekDay = in.readString();
        weekNr = in.readInt();
        fromStart = in.readByte() != 0;
        skip = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        out.writeString(weekDay);
        out.writeInt(weekNr);
        out.writeByte((byte) (fromStart ? 1 : 0));
        out.writeInt(skip);
    }

    public static final Creator<CrMonthlyWSchedule> CREATOR = new Creator<CrMonthlyWSchedule>() {
        @Override
        public CrMonthlyWSchedule createFromParcel(Parcel in) {
            return new CrMonthlyWSchedule(in);
        }

        @Override
        public CrMonthlyWSchedule[] newArray(int size) {
            return new CrMonthlyWSchedule[size];
        }
    };

    // endregion
}
