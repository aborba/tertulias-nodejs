package pt.isel.s1516v.ps.apiaccess.tertuliacreation.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthly;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;

public class CrApiMonthlySchedule extends CrApiTertulia implements Parcelable, Schedule {

    @com.google.gson.annotations.SerializedName("sc_dayNr")
    public final int dayNr;
    @com.google.gson.annotations.SerializedName("sc_fromStart")
    public final boolean fromStart;
    @com.google.gson.annotations.SerializedName("sc_skip")
    public final int skip;

    public CrApiMonthlySchedule(String name, String subject,
                               String location, String address, String zip, String city, String country,
                               String latitude, String longitude,
                               int dayNr, boolean fromStart, int skip,
//                               String scheduleType, int scheduleId,
                               boolean isPrivate) {
        super(name, subject,
                location, address, zip, city, country,
                latitude, longitude,
//                scheduleType, scheduleId,
                isPrivate);
        this.dayNr = dayNr;
        this.fromStart = fromStart;
        this.skip = skip;
    }

    public CrApiMonthlySchedule(CrUiTertulia crUiTertulia, CrUiMonthly crUiMonthly) {
        this(crUiTertulia.name, crUiTertulia.subject,
                crUiTertulia.crUiLocation.name, crUiTertulia.crUiLocation.address.address, crUiTertulia.crUiLocation.address.zip, crUiTertulia.crUiLocation.address.city, crUiTertulia.crUiLocation.address.country,
                String.valueOf(crUiTertulia.crUiLocation.geo.latitude), String.valueOf(crUiTertulia.crUiLocation.geo.longitude),
                crUiMonthly.dayNr, crUiMonthly.isFromStart, crUiMonthly.skip,
                crUiTertulia.isPrivate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        CrApiMonthlySchedule other = (CrApiMonthlySchedule) obj;
        return other.dayNr == dayNr && other.fromStart == fromStart && other.skip == skip;
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

    protected CrApiMonthlySchedule(Parcel in) {
        super(in.readString(), in.readString(),
                in.readString(), in.readString(), in.readString(), in.readString(), in.readString(),
                in.readString(), in.readString(),
//                in.readString(), in.readInt(),
                in.readByte() != 0);
        dayNr = in.readInt();
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
        out.writeInt(dayNr);
        out.writeByte((byte) (fromStart ? 1 : 0));
        out.writeInt(skip);
    }

    public static final Creator<CrApiMonthlySchedule> CREATOR = new Creator<CrApiMonthlySchedule>() {
        @Override
        public CrApiMonthlySchedule createFromParcel(Parcel in) {
            return new CrApiMonthlySchedule(in);
        }

        @Override
        public CrApiMonthlySchedule[] newArray(int size) {
            return new CrApiMonthlySchedule[size];
        }
    };

    // endregion
}
