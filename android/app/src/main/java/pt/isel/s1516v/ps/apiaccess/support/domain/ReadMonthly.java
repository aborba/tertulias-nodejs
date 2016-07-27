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
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiReadTertuliaWeekly;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaListItem;

public class ReadMonthly extends ReadTertulia {
    final int id;
    final int dayNr;
    final boolean isFromStart;
    final int skip;

    public ReadMonthly(ApiTertuliaListItem apiTertuliaListItem, int id, int dayNr, boolean isFromStart, int skip) {
        super(apiTertuliaListItem);
        this.id = id;
        this.dayNr = dayNr;
        this.isFromStart = isFromStart;
        this.skip = skip;
    }

    public ReadMonthly(ApiReadTertuliaCore core, ApiLink[] links, int id, int dayNr, boolean isFromStart, int skip) {
        super(core, links);
        this.id = id;
        this.dayNr = dayNr;
        this.isFromStart = isFromStart;
        this.skip = skip;
    }

    public ReadMonthly(ApiReadTertuliaMonthly apiReadTertuliaMonthly) {
        this(apiReadTertuliaMonthly.tertulia, apiReadTertuliaMonthly.links,
                apiReadTertuliaMonthly.monthly.id, apiReadTertuliaMonthly.monthly.dayNr, apiReadTertuliaMonthly.monthly.isFromStart, apiReadTertuliaMonthly.monthly.skip);
    }

    @Override
    public String toString() {
        Context ctx = TertuliasApplication.getApplication().getApplicationContext();
        Resources res = ctx.getResources();
        String[] parts = res.getStringArray(R.array.new_tertulia_dialog_monthly_tostring);
        String[] suffix = res.getStringArray(R.array.new_monthly_day_suffix);
        String result = parts[0];
        result += skip == 0 ? parts[1] : String.format(Locale.getDefault(), parts[2], skip + 1);
        result += String.format(parts[3], dayNr, suffix[dayNr]);
        if (! isFromStart)
            result += parts[4];
        result += ".";
        return result;
    }

    // region Parcelable

    protected ReadMonthly(Parcel in) {
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
        out.writeInt(dayNr);
        out.writeByte((byte) (isFromStart ? 1 : 0));
        out.writeInt(skip);;
    }

    public static final Creator<ReadMonthly> CREATOR = new Creator<ReadMonthly>() {
        @Override
        public ReadMonthly createFromParcel(Parcel in) {
            return new ReadMonthly(in);
        }

        @Override
        public ReadMonthly[] newArray(int size) {
            return new ReadMonthly[size];
        }
    };

    // endregion
}
