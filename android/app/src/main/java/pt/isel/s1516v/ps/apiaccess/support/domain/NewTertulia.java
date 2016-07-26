package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.support.raw.RTertulia;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaCore;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaListItem;

public class NewTertulia implements Parcelable {

    public final String name;
    public final String subject;
    public NewLocation location;
    public String scheduleType;
    private NewSchedule schedule;
    public final boolean isPrivate;

    public NewSchedule getSchedule() {
        return schedule;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        NewTertulia other = (NewTertulia) obj;
        return obj instanceof NewTertulia && other.name == this.name;
    }

    // region Parcelable

    protected NewTertulia(Parcel in) {
        name = in.readString();
        subject = in.readString();
        location = in.readParcelable(NewLocation.class.getClassLoader());
        scheduleType = in.readString();
        schedule = in.readParcelable(NewSchedule.class.getClassLoader());
        isPrivate = in.readByte() != 0;
    }

    public static final Creator<NewTertulia> CREATOR = new Creator<NewTertulia>() {
        @Override
        public NewTertulia createFromParcel(Parcel in) {
            return new NewTertulia(in);
        }

        @Override
        public NewTertulia[] newArray(int size) {
            return new NewTertulia[size];
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
        out.writeString(scheduleType);
        out.writeParcelable(schedule, flags);
        out.writeByte((byte) (isPrivate ? 1 : 0));
    }

    // endregion

}
