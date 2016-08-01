package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;
import android.os.Parcelable;

import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;

public class TertuliaBase implements Parcelable, TertuliasApi {

    public final String name;
    public final String subject;
    public final boolean isPrivate;
    public final SCHEDULES scheduleType;

    public TertuliaBase(String name, String subject, boolean isPrivate, SCHEDULES scheduleType) {
        this.name = name;
        this.subject = subject;
        this.isPrivate = isPrivate;
        this.scheduleType = scheduleType;
    }

    @Override
    public String toString() { return name; }

    // region Parcelable

    protected TertuliaBase(Parcel in) {
        name = in.readString();
        subject = in.readString();
        isPrivate = in.readByte() != 0;
        scheduleType = SCHEDULES.valueOf(in.readString());
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
        out.writeString(scheduleType.name());
    }

    // endregion

}
