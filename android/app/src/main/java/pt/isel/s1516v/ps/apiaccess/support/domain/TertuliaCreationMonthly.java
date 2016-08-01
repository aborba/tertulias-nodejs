package pt.isel.s1516v.ps.apiaccess.support.domain;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.TertuliasApplication;
import pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui.CrUiMonthly;

public class TertuliaCreationMonthly extends TertuliaCreation {
    public final int dayNr;
    public final boolean isFromStart;
    public final int skip;

    public TertuliaCreationMonthly(TertuliaCreation tertulia, CrUiMonthly schedule) {
        super(tertulia.name, tertulia.subject, tertulia.isPrivate,
                tertulia.location,
                schedule.getScheduleType(), tertulia.getSchedule());
        this.dayNr = schedule.dayNr;
        this.isFromStart = schedule.isFromStart;
        this.skip = schedule.skip;
    }

    public TertuliaCreationMonthly(TertuliaCreation tertulia) {
        this(tertulia, new CrUiMonthly(((TertuliaCreationMonthly)tertulia).dayNr,
                ((TertuliaCreationMonthly)tertulia).isFromStart,
                ((TertuliaCreationMonthly)tertulia).skip));
    }

    @Override
    public String toString() {
        Context ctx = TertuliasApplication.getApplication().getApplicationContext();
        Resources res = ctx.getResources();
        String[] parts = res.getStringArray(R.array.new_tertulia_dialog_monthly_tostring);
        String[] suffix = res.getStringArray(R.array.new_monthly_day_suffix);
        String result = parts[0];
        result += skip == 0 ? parts[1] : String.format(Locale.getDefault(), parts[2], skip + 1);
        result += String.format(parts[3], dayNr, suffix[dayNr]);
        if (! isFromStart)
            result += parts[4];
        result += ".";
        return result;
    }

    // region Parcelable

    protected TertuliaCreationMonthly(Parcel in) {
        super(in);
        dayNr = in.readInt();
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
        out.writeInt(dayNr);
        out.writeByte((byte) (isFromStart ? 1 : 0));
        out.writeInt(skip);;
    }

    public static final Creator<TertuliaCreationMonthly> CREATOR = new Creator<TertuliaCreationMonthly>() {
        @Override
        public TertuliaCreationMonthly createFromParcel(Parcel in) {
            return new TertuliaCreationMonthly(in);
        }

        @Override
        public TertuliaCreationMonthly[] newArray(int size) {
            return new TertuliaCreationMonthly[size];
        }
    };

    // endregion
}
