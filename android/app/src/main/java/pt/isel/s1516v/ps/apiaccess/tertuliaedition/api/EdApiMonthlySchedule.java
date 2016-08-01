package pt.isel.s1516v.ps.apiaccess.tertuliaedition.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionMonthly;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionWeekly;
import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;

public class EdApiMonthlySchedule extends EdApiTertulia implements Parcelable, Schedule {

    @com.google.gson.annotations.SerializedName("schedule_daynr")
    public final int dayNr;
    @com.google.gson.annotations.SerializedName("schedule_isfromstart")
    public final boolean fromStart;
    @com.google.gson.annotations.SerializedName("schedule_skip")
    public final int skip;

    public EdApiMonthlySchedule(int tertuliaId, String name, String subject,
                                String roleType,
                                int locationId, String location, String address, String zip, String city, String country,
                                String latitude, String longitude,
                                int dayNr, boolean fromStart, int skip,
                                String scheduleType, int scheduleId,
                                boolean isPrivate) {
        super(tertuliaId, name, subject, isPrivate,
                roleType,
                locationId, location, address, zip, city, country,
                latitude, longitude,
                scheduleType);
        this.dayNr = dayNr;
        this.fromStart = fromStart;
        this.skip = skip;
    }

    public EdApiMonthlySchedule(TertuliaEdition tertulia) {
        this(tertulia.id, tertulia.name, tertulia.subject,
                tertulia.role.name,
                tertulia.location.id, tertulia.location.name, tertulia.location.address.address,
                tertulia.location.address.zip, tertulia.location.address.city, tertulia.location.address.country,
                String.valueOf(tertulia.location.geolocation.latitude), String.valueOf(tertulia.location.geolocation.longitude),
                ((TertuliaEditionMonthly) tertulia).dayNr, ((TertuliaEditionMonthly) tertulia).isFromStart, ((TertuliaEditionWeekly) tertulia).skip,
                tertulia.scheduleType.name(), ((TertuliaEditionMonthly) tertulia).schedule_id,
                tertulia.isPrivate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        EdApiMonthlySchedule other = (EdApiMonthlySchedule) obj;
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

    protected EdApiMonthlySchedule(Parcel in) {
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
        out.writeInt(tertuliaId);
        out.writeString(tertuliaName);
        out.writeString(subject);
        out.writeString(roleType);
        out.writeInt(locationId);
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

    public static final Creator<EdApiMonthlySchedule> CREATOR = new Creator<EdApiMonthlySchedule>() {
        @Override
        public EdApiMonthlySchedule createFromParcel(Parcel in) {
            return new EdApiMonthlySchedule(in);
        }

        @Override
        public EdApiMonthlySchedule[] newArray(int size) {
            return new EdApiMonthlySchedule[size];
        }
    };

    // endregion
}
