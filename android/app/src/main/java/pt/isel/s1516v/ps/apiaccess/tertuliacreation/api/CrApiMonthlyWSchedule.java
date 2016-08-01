package pt.isel.s1516v.ps.apiaccess.tertuliacreation.api;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaCreation;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaCreationMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionMonthlyW;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthlyW;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;

public class CrApiMonthlyWSchedule extends CrApiTertulia implements Parcelable, Schedule {

    @com.google.gson.annotations.SerializedName("schedule_weekday")
    public final String weekDay;
    @com.google.gson.annotations.SerializedName("schedule_weeknr")
    public final int weekNr;
    @com.google.gson.annotations.SerializedName("schedule_isfromstart")
    public final boolean fromStart;
    @com.google.gson.annotations.SerializedName("schedule_skip")
    public final int skip;

    public CrApiMonthlyWSchedule(String name, String subject, boolean isPrivate,
                                 String location, String address, String zip, String city, String country,
                                 String latitude, String longitude,
                                 String weekDay, int weekNr, boolean fromStart, int skip) {
        super(name, subject, isPrivate,
                location, address, zip, city, country,
                latitude, longitude,
                TertuliasApi.SCHEDULES.MONTHLYW.name());
        this.weekDay = weekDay;
        this.weekNr = weekNr;
        this.fromStart = fromStart;
        this.skip = skip;
    }

    public CrApiMonthlyWSchedule(Context ctx, TertuliaEditionMonthlyW tertulia) {
        this(tertulia.name, tertulia.subject, tertulia.isPrivate,
                tertulia.location.name, tertulia.location.address.address, tertulia.location.address.zip, tertulia.location.address.city, tertulia.location.address.country,
                String.valueOf(tertulia.location.geolocation.latitude), String.valueOf(tertulia.location.geolocation.longitude),
                tertulia.getWeekDay(ctx), tertulia.weeknr, tertulia.isFromStart, tertulia.skip);

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        CrApiMonthlyWSchedule other = (CrApiMonthlyWSchedule) obj;
        return other.weekDay.equals(weekDay) && other.weekNr == weekNr && other.fromStart == fromStart && other.skip == skip;
    }

    // region CrApiTertulia

//    @Override
    protected String toStringContribution() { return null; }

    // endregion

    // region CrUiSchedule

    @Override
    public Date nextEvent() {
        return null;
    }

    // endregion

    // region Parcelable

    protected CrApiMonthlyWSchedule(Parcel in) {
        super(in);
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
        super.writeToParcel(out, flags);
        out.writeString(weekDay);
        out.writeInt(weekNr);
        out.writeByte((byte) (fromStart ? 1 : 0));
        out.writeInt(skip);
    }

    public static final Creator<CrApiMonthlyWSchedule> CREATOR = new Creator<CrApiMonthlyWSchedule>() {
        @Override
        public CrApiMonthlyWSchedule createFromParcel(Parcel in) {
            return new CrApiMonthlyWSchedule(in);
        }

        @Override
        public CrApiMonthlyWSchedule[] newArray(int size) {
            return new CrApiMonthlyWSchedule[size];
        }
    };

    // endregion
}
