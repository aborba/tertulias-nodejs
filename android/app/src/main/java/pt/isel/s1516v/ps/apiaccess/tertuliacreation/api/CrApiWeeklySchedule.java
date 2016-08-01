package pt.isel.s1516v.ps.apiaccess.tertuliacreation.api;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiWeekly;

public class CrApiWeeklySchedule extends CrApiTertulia implements Parcelable, Schedule {

    @com.google.gson.annotations.SerializedName("schedule_weekday")
    public final String weekDay;
    @com.google.gson.annotations.SerializedName("schedule_skip")
    public final int skip;

    public CrApiWeeklySchedule(String name, String subject,
                               String location, String address, String zip, String city, String country,
                               String latitude, String longitude,
                               String weekDay, int skip,
                               boolean isPrivate) {
        super(name, subject, isPrivate,
                location, address, zip, city, country,
                latitude, longitude,
                TertuliasApi.SCHEDULES.WEEKLY.name());
        this.weekDay = weekDay;
        this.skip = skip;
    }

    public CrApiWeeklySchedule(Context ctx, CrUiTertulia crUiTertulia, CrUiWeekly crUiWeekly) {
        this(crUiTertulia.name, crUiTertulia.subject,
                crUiTertulia.crUiLocation.name, crUiTertulia.crUiLocation.address.address, crUiTertulia.crUiLocation.address.zip, crUiTertulia.crUiLocation.address.city, crUiTertulia.crUiLocation.address.country,
                String.valueOf(crUiTertulia.crUiLocation.geo.latitude), String.valueOf(crUiTertulia.crUiLocation.geo.longitude),
                crUiWeekly.getWeekDay(ctx), crUiWeekly.skip,
                crUiTertulia.isPrivate);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        CrApiWeeklySchedule other = (CrApiWeeklySchedule) obj;
        return other.weekDay.equals(weekDay) && other.skip == skip;
    }

    // region CrApiTertulia

//    @Override
    protected String toStringContribution() { return null; }

    // endregion

    // region Schedule

    @Override
    public Date nextEvent() {
        return null;
    }

    // endregion

    // region Parcelable

    protected CrApiWeeklySchedule(Parcel in) {
        super(in);
        weekDay = in.readString();
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
        out.writeInt(skip);
    }

    public static final Creator<CrApiWeeklySchedule> CREATOR = new Parcelable.Creator<CrApiWeeklySchedule>() {
        @Override
        public CrApiWeeklySchedule createFromParcel(Parcel in) {
            return new CrApiWeeklySchedule(in);
        }

        @Override
        public CrApiWeeklySchedule[] newArray(int size) {
            return new CrApiWeeklySchedule[size];
        }
    };

    // endregion
}
