package pt.isel.s1516v.ps.apiaccess.tertuliacreation.api;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthlyW;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;

public class CrApiMonthlyWSchedule extends CrApiTertulia implements Parcelable, Schedule {

    @com.google.gson.annotations.SerializedName("sc_weekDay")
    public final String weekDay;
    @com.google.gson.annotations.SerializedName("sc_weekNr")
    public final int weekNr;
    @com.google.gson.annotations.SerializedName("sc_fromStart")
    public final boolean fromStart;
    @com.google.gson.annotations.SerializedName("sc_skip")
    public final int skip;

    public CrApiMonthlyWSchedule(String name, String subject,
                                 String location, String address, String zip, String city, String country,
                                 String latitude, String longitude,
                                 String weekDay, int weekNr, boolean fromStart, int skip,
//                                 String scheduleType, int scheduleId,
                                 boolean isPrivate) {
        super(name, subject,
                location, address, zip, city, country,
                latitude, longitude,
//                scheduleType, scheduleId,
                isPrivate);
        this.weekDay = weekDay;
        this.weekNr = weekNr;
        this.fromStart = fromStart;
        this.skip = skip;
    }

    public CrApiMonthlyWSchedule(Context ctx, CrUiTertulia crUiTertulia, CrUiMonthlyW crUiMonthlyW) {
        this(crUiTertulia.name, crUiTertulia.subject,
                crUiTertulia.crUiLocation.name, crUiTertulia.crUiLocation.address.address, crUiTertulia.crUiLocation.address.zip, crUiTertulia.crUiLocation.address.city, crUiTertulia.crUiLocation.address.country,
                String.valueOf(crUiTertulia.crUiLocation.geo.latitude), String.valueOf(crUiTertulia.crUiLocation.geo.longitude),
                crUiMonthlyW.getWeekDay(ctx), crUiMonthlyW.weekNr, crUiMonthlyW.isFromStart, crUiMonthlyW.skip,
                crUiTertulia.isPrivate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        CrApiMonthlyWSchedule other = (CrApiMonthlyWSchedule) obj;
        return other.weekDay.equals(weekDay) && other.weekNr == weekNr && other.fromStart == fromStart && other.skip == skip;
    }

    // region CrApiTertulia

    @Override
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
        super(in.readString(), in.readString(),
                in.readString(), in.readString(), in.readString(), in.readString(), in.readString(),
                in.readString(), in.readString(),
//                in.readString(), in.readInt(),
                in.readByte() != 0);
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
        out.writeString(tertuliaName);
        out.writeString(subject);
        out.writeString(locationName);
        out.writeString(streetAddress);
        out.writeString(zip);
        out.writeString(city);
        out.writeString(country);
        out.writeString(latitude);
        out.writeString(longitude);
//        out.writeString(scheduleType);
//        out.writeInt(scheduleId);
        out.writeByte((byte) (isPrivate ? 1 : 0));
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
