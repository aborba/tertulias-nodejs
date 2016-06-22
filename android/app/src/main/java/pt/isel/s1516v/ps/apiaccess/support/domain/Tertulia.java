package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import pt.isel.s1516v.ps.apiaccess.support.raw.RTertulia;

public class Tertulia implements Parcelable {

    public final String id;
    public final String name;
    public final String subject;
    public final Location location;
    public Date nextEventDate;
    public final String scheduleType;
    public String scheduleDescription;
    private Schedule schedule;
    public final boolean isPrivate;
    public final String role_type;
    public int messagesCount;
    public final HashMap<String, HashMap<String, String>> links;

    public Tertulia(RTertulia rtertulia) {
        id = rtertulia.id;
        name = rtertulia.name;
        subject = rtertulia.subject;
        if (!TextUtils.isEmpty(rtertulia.event))
        try {
            rtertulia.event = rtertulia.event.replace('T', ' ').substring(0, 16 );
            nextEventDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(rtertulia.event);
        } catch (ParseException e) {
            nextEventDate = null;
        }
        location = new Location(rtertulia);
        scheduleType = rtertulia.scheduleType;
        scheduleDescription = rtertulia.scheduleDescription;
        isPrivate = rtertulia.isPrivate;
        role_type = rtertulia.role;
        messagesCount = rtertulia.messagesTotal;
        links = rtertulia.links;
    }

    public Schedule getSchedule() {
        //if (schedule == null) fetchSchedule(id, schedule_type); // TODO: fetchSchedule, implementation missing
        return schedule;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        Tertulia other = (Tertulia) obj;
        return obj instanceof Tertulia && other.id == this.id && other.name == this.name;
    }

    // region Parcelable

    protected Tertulia(Parcel in) {
        id = in.readString();
        name = in.readString();
        subject = in.readString();
        long val = in.readLong();
        nextEventDate = val == 0 ? null : new Date(val);
        location = in.readParcelable(Location.class.getClassLoader());
        scheduleType = in.readString();
        isPrivate = in.readByte() != 0;
        role_type = in.readString();
        messagesCount = in.readInt();
        links = (HashMap<String, HashMap<String, String>>) in.readSerializable();
    }

    public static final Creator<Tertulia> CREATOR = new Creator<Tertulia>() {
        @Override
        public Tertulia createFromParcel(Parcel in) {
            return new Tertulia(in);
        }

        @Override
        public Tertulia[] newArray(int size) {
            return new Tertulia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(subject);
        dest.writeLong(nextEventDate == null ? 0 : nextEventDate.getTime());
        dest.writeParcelable(location, flags);
        dest.writeString(scheduleType);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeString(role_type);
        dest.writeInt(messagesCount);
        dest.writeSerializable(links);
    }

    // endregion

}
