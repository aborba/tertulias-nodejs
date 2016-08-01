package pt.isel.s1516v.ps.apiaccess.tertuliaedition.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionMonthlyW;
import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;

public class EdApiMonthlyWSchedule extends EdApiTertulia implements Parcelable, Schedule {

    @com.google.gson.annotations.SerializedName("schedule_weekday")
    public final String weekDay;
    @com.google.gson.annotations.SerializedName("schedule_weeknr")
    public final int weekNr;
    @com.google.gson.annotations.SerializedName("schedule_isfromstart")
    public final boolean fromStart;
    @com.google.gson.annotations.SerializedName("schedule_skip")
    public final int skip;

    public EdApiMonthlyWSchedule(int tertuliaId, String name, String subject,
                                 String roleType,
                                 int locationId, String location, String address, String zip, String city, String country,
                                 String latitude, String longitude,
                                 String weekDay, int weekNr, boolean fromStart, int skip,
                                 String scheduleType, int scheduleId,
                                 boolean isPrivate) {
        super(tertuliaId, name, subject, isPrivate,
                roleType,
                locationId, location, address, zip, city, country,
                latitude, longitude,
                scheduleType);
        this.weekDay = weekDay;
        this.weekNr = weekNr;
        this.fromStart = fromStart;
        this.skip = skip;
    }

    public EdApiMonthlyWSchedule(TertuliaEditionMonthlyW tertulia) {
        this(tertulia.schedule_id, tertulia.name, tertulia.subject,
                tertulia.role.name,
                tertulia.location.id, tertulia.location.name, tertulia.location.address.address,
                tertulia.location.address.zip, tertulia.location.address.city, tertulia.location.address.country,
                String.valueOf(tertulia.location.geolocation.latitude), String.valueOf(tertulia.location.geolocation.longitude),
                "sunday", ((TertuliaEditionMonthlyW) tertulia).weeknr, ((TertuliaEditionMonthlyW) tertulia).isFromStart, ((TertuliaEditionMonthlyW) tertulia).skip,
                tertulia.scheduleType.name(), ((TertuliaEditionMonthlyW) tertulia).schedule_id,
                tertulia.isPrivate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        EdApiMonthlyWSchedule other = (EdApiMonthlyWSchedule) obj;
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

    protected EdApiMonthlyWSchedule(Parcel in) {
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

    public static final Creator<EdApiMonthlyWSchedule> CREATOR = new Creator<EdApiMonthlyWSchedule>() {
        @Override
        public EdApiMonthlyWSchedule createFromParcel(Parcel in) {
            return new EdApiMonthlyWSchedule(in);
        }

        @Override
        public EdApiMonthlyWSchedule[] newArray(int size) {
            return new EdApiMonthlyWSchedule[size];
        }
    };

    // endregion
}
