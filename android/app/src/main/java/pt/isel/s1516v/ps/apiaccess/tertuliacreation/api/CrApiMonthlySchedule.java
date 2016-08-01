package pt.isel.s1516v.ps.apiaccess.tertuliacreation.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;
import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthly;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiTertulia;

public class CrApiMonthlySchedule extends CrApiTertulia implements Parcelable, Schedule {

    @com.google.gson.annotations.SerializedName("schedule_daynr")
    public final int dayNr;
    @com.google.gson.annotations.SerializedName("schedule_fromstart")
    public final boolean fromStart;
    @com.google.gson.annotations.SerializedName("schedule_skip")
    public final int skip;

    public CrApiMonthlySchedule(String name, String subject, boolean isPrivate,
                               String location, String address, String zip, String city, String country,
                               String latitude, String longitude,
                               int dayNr, boolean fromStart, int skip) {
        super(name, subject, isPrivate,
                location, address, zip, city, country,
                latitude, longitude,
                TertuliasApi.SCHEDULES.MONTHLYD.name());
        this.dayNr = dayNr;
        this.fromStart = fromStart;
        this.skip = skip;
    }

    public CrApiMonthlySchedule(CrUiTertulia crUiTertulia, CrUiMonthly crUiMonthly) {
        this(crUiTertulia.name, crUiTertulia.subject, crUiTertulia.isPrivate,
                crUiTertulia.crUiLocation.name, crUiTertulia.crUiLocation.address.address, crUiTertulia.crUiLocation.address.zip, crUiTertulia.crUiLocation.address.city, crUiTertulia.crUiLocation.address.country,
                String.valueOf(crUiTertulia.crUiLocation.geo.latitude), String.valueOf(crUiTertulia.crUiLocation.geo.longitude),
                crUiMonthly.dayNr, crUiMonthly.isFromStart, crUiMonthly.skip);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        CrApiMonthlySchedule other = (CrApiMonthlySchedule) obj;
        return other.dayNr == dayNr && other.fromStart == fromStart && other.skip == skip;
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

    protected CrApiMonthlySchedule(Parcel in) {
        super(in);
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
        super.writeToParcel(out, flags);
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
