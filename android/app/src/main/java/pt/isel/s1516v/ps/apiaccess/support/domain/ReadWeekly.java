package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.Api;

import java.util.Date;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.TertuliasApplication;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiReadTertuliaCore;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiReadTertuliaWeekly;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaListItem;

public class ReadWeekly extends ReadTertulia {
    final int id;
    final int weekday;
    final int skip;

    public ReadWeekly(ApiTertuliaListItem apiTertuliaListItem, int id, int weekday, int skip) {
        super(apiTertuliaListItem);
        this.id = id;
        this.weekday = weekday;
        this.skip = skip;
    }

    public ReadWeekly(ApiReadTertuliaCore core, ApiLink[] links, int id, int weekday, int skip) {
        super(core, links);
        this.id = id;
        this.weekday = weekday;
        this.skip = skip;
    }

    public ReadWeekly(ApiReadTertuliaWeekly apiReadTertuliaWeekly) {
        this(apiReadTertuliaWeekly.tertulia, apiReadTertuliaWeekly.links,
                apiReadTertuliaWeekly.weekly.id, apiReadTertuliaWeekly.weekly.weekDay, apiReadTertuliaWeekly.weekly.skip);
    }

    @Override
    public String toString() {
        Context ctx = TertuliasApplication.getApplication().getApplicationContext();
        Resources res = ctx.getResources();
        String[] parts = res.getStringArray(R.array.new_tertulia_dialog_weekly_tostring);
        String[] weekdays = res.getStringArray(R.array.new_monthlyw_weekday);
        String result = parts[0];
        result += skip == 0 ? parts[1] : String.format(Locale.getDefault(), parts[2], skip + 1);
        result += String.format(parts[3], weekdays[weekday]);
        result += ".";
        return result;
    }

    // region Parcelable

    protected ReadWeekly(Parcel in) {
        super(in.readString(),
                in.readString(),
                in.readString(),
                (Location) in.readParcelable(Location.class.getClassLoader()),
                in.readByte() != 0 ? new Date(in.readLong()) : null,
                in.readString(),
                in.readString(),
                (Schedule) in.readParcelable(Schedule.class.getClassLoader()),
                in.readByte() != 0,
                in.readString(),
                in.readInt(),
                in.createTypedArray(ApiLink.CREATOR));
        id = in.readInt();
        weekday = in.readInt();
        skip = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        out.writeString(super.id);
        out.writeString(name);
        out.writeString(subject);
        out.writeParcelable(location, flags);
        boolean isNextEvent = nextEventDate != null;
        out.writeByte((byte) (isNextEvent ? 1 : 0));
        if (isNextEvent)
            out.writeLong(nextEventDate.getTime());
        out.writeString(scheduleType);
        out.writeString(scheduleDescription);
        out.writeParcelable(schedule, flags);
        out.writeByte((byte) (isPrivate ? 1 : 0));
        out.writeString(role_type);
        out.writeInt(messagesCount);
        out.writeTypedArray(links, flags);
        out.writeInt(id);
        out.writeInt(weekday);
        out.writeInt(skip);;
    }

    public static final Creator<ReadWeekly> CREATOR = new Parcelable.Creator<ReadWeekly>() {
        @Override
        public ReadWeekly createFromParcel(Parcel in) {
            return new ReadWeekly(in);
        }

        @Override
        public ReadWeekly[] newArray(int size) {
            return new ReadWeekly[size];
        }
    };

    // endregion
}
