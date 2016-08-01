package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.TertuliasApplication;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLink;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEdition;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundleMonthlyW;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthly;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthlyW;

public class TertuliaCreationMonthlyW extends TertuliaCreation {
    public final int weekday;
    public final int weeknr;
    public final boolean isFromStart;
    public final int skip;

    public TertuliaCreationMonthlyW(TertuliaCreation tertulia, CrUiMonthlyW schedule) {
        super(tertulia.name, tertulia.subject, tertulia.isPrivate, tertulia.location, schedule.getScheduleType(), tertulia.getSchedule());
        this.weekday = schedule.weekDayNr;
        this.weeknr = schedule.weekNr;
        this.isFromStart = schedule.isFromStart;
        this.skip = schedule.skip;
    }

    public TertuliaCreationMonthlyW(TertuliaCreation tertulia) {
        this(tertulia, new CrUiMonthlyW(((TertuliaCreationMonthlyW)tertulia).weekday,
                ((TertuliaCreationMonthlyW)tertulia).weeknr,
                ((TertuliaCreationMonthlyW)tertulia).isFromStart,
                ((TertuliaCreationMonthlyW)tertulia).skip));
    }

    @Override
    public String toString() {
        Context ctx = TertuliasApplication.getApplication().getApplicationContext();
        Resources res = ctx.getResources();
        String[] parts = res.getStringArray(R.array.new_tertulia_dialog_monthlyw_tostring);
        String[] weekdays = res.getStringArray(R.array.new_monthlyw_weekday);
        String[] suffix = res.getStringArray(R.array.new_monthly_day_suffix);
        String result = parts[0];
        result += skip == 0 ? parts[1] : String.format(Locale.getDefault(), parts[2], skip + 1);
        result += String.format(parts[3], weekdays[weekday], weeknr + 1, suffix[weeknr]);
        if (! isFromStart)
            result += parts[4];
        result += ".";
        return result;
    }

    // region Parcelable

    protected TertuliaCreationMonthlyW(Parcel in) {
        super(in);
        weekday = in.readInt();
        weeknr = in.readInt();
        isFromStart = in.readByte() != 0;
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
        out.writeInt(weeknr);
        out.writeByte((byte) (isFromStart ? 1 : 0));
        out.writeInt(skip);;
    }

    public static final Creator<TertuliaCreationMonthlyW> CREATOR = new Creator<TertuliaCreationMonthlyW>() {
        @Override
        public TertuliaCreationMonthlyW createFromParcel(Parcel in) {
            return new TertuliaCreationMonthlyW(in);
        }

        @Override
        public TertuliaCreationMonthlyW[] newArray(int size) {
            return new TertuliaCreationMonthlyW[size];
        }
    };

    // endregion
}
