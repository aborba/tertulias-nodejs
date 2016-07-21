package pt.isel.s1516v.ps.apiaccess.tertuliacreation.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;

public class CrMonthlySchedule implements Parcelable, Schedule {

    @com.google.gson.annotations.SerializedName("dayNr")
    public final int dayNr;
    @com.google.gson.annotations.SerializedName("fromStart")
    public final boolean fromStart;
    @com.google.gson.annotations.SerializedName("skip")
    public final int skip;

    public CrMonthlySchedule(int dayNr, boolean fromStart, int skip) {
        this.dayNr = dayNr;
        this.fromStart = fromStart;
        this.skip = skip;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        CrMonthlySchedule other = (CrMonthlySchedule) obj;
        return other.dayNr == dayNr && other.fromStart == fromStart && other.skip == skip;
    }

    // region CrUiSchedule

    @Override
    public Date nextEvent() {
        return null;
    }

    // endregion

    // region Parcelable

    protected CrMonthlySchedule(Parcel in) {
        dayNr = in.readInt();
        fromStart = in.readByte() != 0;
        skip = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        out.writeInt(dayNr);
        out.writeByte((byte) (fromStart ? 1 : 0));
        out.writeInt(skip);
    }

    public static final Creator<CrMonthlySchedule> CREATOR = new Creator<CrMonthlySchedule>() {
        @Override
        public CrMonthlySchedule createFromParcel(Parcel in) {
            return new CrMonthlySchedule(in);
        }

        @Override
        public CrMonthlySchedule[] newArray(int size) {
            return new CrMonthlySchedule[size];
        }
    };

    // endregion
}
