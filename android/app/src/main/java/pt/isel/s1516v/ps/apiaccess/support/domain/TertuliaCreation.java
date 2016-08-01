package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;
import android.os.Parcelable;

import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;

public class TertuliaCreation implements Parcelable, TertuliasApi {

    public final String name;
    public final String subject;
    public final boolean isPrivate;
    public LocationCreation location;
    public SCHEDULES scheduleType;
    private NewSchedule schedule;

    public TertuliaCreation(String name, String subject, boolean isPrivate, LocationCreation location, SCHEDULES scheduleType, NewSchedule schedule) {
        this.name = name;
        this.subject = subject;
        this.isPrivate = isPrivate;
        this.location = location;
        this.scheduleType = scheduleType;
        this.schedule = schedule;
    }

    public NewSchedule getSchedule() {
        return schedule;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        TertuliaCreation other = (TertuliaCreation) obj;
        return obj instanceof TertuliaCreation && other.name == this.name;
    }

    // region Parcelable

    protected TertuliaCreation(Parcel in) {
        name = in.readString();
        subject = in.readString();
        location = in.readParcelable(LocationCreation.class.getClassLoader());
        scheduleType = SCHEDULES.valueOf(in.readString());
        schedule = in.readParcelable(NewSchedule.class.getClassLoader());
        isPrivate = in.readByte() != 0;
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
        out.writeString(name);
        out.writeString(subject);
        out.writeParcelable(location, flags);
        out.writeString(scheduleType.name());
        out.writeParcelable(schedule, flags);
        out.writeByte((byte) (isPrivate ? 1 : 0));
    }

    // endregion

}
