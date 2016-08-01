package pt.isel.s1516v.ps.apiaccess.tertuliaedition.api;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaEditionWeekly;
import pt.isel.s1516v.ps.apiaccess.support.domain.Schedule;

public class EdApiWeeklySchedule extends EdApiTertulia implements Parcelable, Schedule {

    @com.google.gson.annotations.SerializedName("schedule_weekday")
    public final String weekDay;
    @com.google.gson.annotations.SerializedName("schedule_skip")
    public final int skip;

    public EdApiWeeklySchedule(int tertuliaId, String name, String subject, boolean isPrivate,
                               String roleType,
                               int locationId, String location, String address,
                               String zip, String city, String country,
                               String latitude, String longitude,
                               String weekDay, int skip,
                               String scheduleType) {
        super(tertuliaId, name, subject, isPrivate,
                roleType,
                locationId, location, address, zip, city, country,
                latitude, longitude,
                scheduleType);
        this.weekDay = weekDay;
        this.skip = skip;
    }

    public EdApiWeeklySchedule(Context ctx, TertuliaEditionWeekly tertulia) {
        this(tertulia.id, tertulia.name, tertulia.subject, tertulia.isPrivate,
                tertulia.role.name,
                tertulia.location.id, tertulia.location.name, tertulia.location.address.address,
                tertulia.location.address.zip, tertulia.location.address.city, tertulia.location.address.country,
                String.valueOf(tertulia.location.geolocation.latitude), String.valueOf(tertulia.location.geolocation.longitude),
                tertulia.getWeekDay(ctx), tertulia.skip,
                tertulia.scheduleType.name());
    }

   // region EdApiTertulia

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

    protected EdApiWeeklySchedule(Parcel in) {
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

    public static final Creator<EdApiWeeklySchedule> CREATOR = new Creator<EdApiWeeklySchedule>() {
        @Override
        public EdApiWeeklySchedule createFromParcel(Parcel in) {
            return new EdApiWeeklySchedule(in);
        }

        @Override
        public EdApiWeeklySchedule[] newArray(int size) {
            return new EdApiWeeklySchedule[size];
        }
    };

    // endregion
}
