package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.TertuliasApplication;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaCreation;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundleWeekly;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiWeekly;

public class TertuliaCreationWeekly extends TertuliaCreation {
    public final int weekday;
    public final int skip;

    public TertuliaCreationWeekly(String name, String subject, boolean isPrivate,
                                  LocationCreation location,
                                  SCHEDULES scheduleType, NewSchedule schedule,
                                  int weekday, int skip) {
        super(name, subject, isPrivate, location, scheduleType, schedule);
        this.weekday = weekday;
        this.skip = skip;
    }

    public TertuliaCreationWeekly(TertuliaCreation tertulia, CrUiWeekly schedule) {
        super(tertulia.name, tertulia.subject, tertulia.isPrivate, tertulia.location, schedule.getScheduleType(), tertulia.getSchedule());
        this.weekday = schedule.weekDayNr;
        this.skip = schedule.skip;
    }

    public TertuliaCreationWeekly(TertuliaCreation tertulia) {
        this(tertulia, new CrUiWeekly(((TertuliaCreationWeekly) tertulia).weekday, ((TertuliaCreationWeekly) tertulia).skip));
    }

    @Override
    public String toString() {
        Context ctx = TertuliasApplication.getApplication().getApplicationContext();
        Resources res = ctx.getResources();
        String[] parts = res.getStringArray(R.array.new_tertulia_dialog_weekly_tostring);
        String[] weekdays = res.getStringArray(R.array.new_monthlyw_weekday);
        String result = parts[0];
        result += skip == 0 ? parts[1] : String.format(Locale.getDefault(), parts[2], skip + 1);
        result += String.format(parts[3], weekdays[weekday]);
        result += ".";
        return result;
    }

    // region Parcelable

    protected TertuliaCreationWeekly(Parcel in) {
        super(in);
        weekday = in.readInt();
        skip = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(weekday);
        out.writeInt(skip);;
    }

    public static final Creator<TertuliaCreationWeekly> CREATOR = new Creator<TertuliaCreationWeekly>() {
        @Override
        public TertuliaCreationWeekly createFromParcel(Parcel in) {
            return new TertuliaCreationWeekly(in);
        }

        @Override
        public TertuliaCreationWeekly[] newArray(int size) {
            return new TertuliaCreationWeekly[size];
        }
    };

    // endregion
}
