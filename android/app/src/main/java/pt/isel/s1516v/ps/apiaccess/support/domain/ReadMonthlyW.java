package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.TertuliasApplication;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiReadTertuliaCore;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiReadTertuliaMonthly;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiReadTertuliaMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiReadTertuliaWeekly;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaListItem;

public class ReadMonthlyW extends ReadTertulia {
    final int id;
    final int weekday;
    final int weeknr;
    final boolean isFromStart;
    final int skip;

    public ReadMonthlyW(ApiTertuliaListItem apiTertuliaListItem, int id, int weekday, int weekNr, boolean isFromStart, int skip) {
        super(apiTertuliaListItem);
        this.id = id;
        this.weekday = weekday;
        this.weeknr = weekNr;
        this.isFromStart = isFromStart;
        this.skip = skip;
    }

    public ReadMonthlyW(ApiReadTertuliaCore core, ApiLink[] links, int id, int weekday, int weekNr, boolean isFromStart, int skip) {
        super(core, links);
        this.id = id;
        this.weekday = weekday;
        this.weeknr = weekNr;
        this.isFromStart = isFromStart;
        this.skip = skip;
    }

    public ReadMonthlyW(ApiReadTertuliaMonthlyW apiReadTertuliaMonthlyW) {
        this(apiReadTertuliaMonthlyW.tertulia, apiReadTertuliaMonthlyW.links,
                apiReadTertuliaMonthlyW.monthlyw.id, apiReadTertuliaMonthlyW.monthlyw.weekDay, apiReadTertuliaMonthlyW.monthlyw.weekNr,
                apiReadTertuliaMonthlyW.monthlyw.isFromStart, apiReadTertuliaMonthlyW.monthlyw.skip);
    }

    @Override
    public String toString() {
        Context ctx = TertuliasApplication.getApplication().getApplicationContext();
        Resources res = ctx.getResources();
        String[] parts = res.getStringArray(R.array.new_tertulia_dialog_monthlyw_tostring);
        String[] weekdays = res.getStringArray(R.array.new_monthlyw_weekday);
        String result = parts[0];
        result += skip == 0 ? parts[1] : String.format(Locale.getDefault(), parts[2], skip + 1);
        result += String.format(parts[3], weekdays[weekday], weeknr + 1);
        if (! isFromStart)
            result += parts[4];
        result += ".";
        return result;
    }

    // region Parcelable

    protected ReadMonthlyW(Parcel in) {
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
        weeknr = in.readInt();
        isFromStart = in.readByte() != 0;
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
        out.writeInt(weeknr);
        out.writeByte((byte) (isFromStart ? 1 : 0));
        out.writeInt(skip);;
    }

    public static final Creator<ReadMonthlyW> CREATOR = new Creator<ReadMonthlyW>() {
        @Override
        public ReadMonthlyW createFromParcel(Parcel in) {
            return new ReadMonthlyW(in);
        }

        @Override
        public ReadMonthlyW[] newArray(int size) {
            return new ReadMonthlyW[size];
        }
    };

    // endregion
}
