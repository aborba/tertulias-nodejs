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

public class TertuliaEditionMonthlyW extends TertuliaEdition {
    public final int schedule_id;
    public final int weekday;
    public final int weeknr;
    public final boolean isFromStart;
    public final int skip;

    public TertuliaEditionMonthlyW(ApiTertuliaEdition core, ApiLink[] links, int schedule_id, int weekday, int weekNr, boolean isFromStart, int skip) {
        super(core, links);
        this.schedule_id = schedule_id;
        this.weekday = weekday;
        this.weeknr = weekNr;
        this.isFromStart = isFromStart;
        this.skip = skip;
    }

    public TertuliaEditionMonthlyW(ApiTertuliaEditionBundleMonthlyW apiReadTertuliaMonthlyW) {
        this(apiReadTertuliaMonthlyW.tertulia, apiReadTertuliaMonthlyW.links,
                apiReadTertuliaMonthlyW.monthlyw.sc_id, apiReadTertuliaMonthlyW.monthlyw.sc_weekday, apiReadTertuliaMonthlyW.monthlyw.sc_weeknr,
                apiReadTertuliaMonthlyW.monthlyw.sc_isfromstart, apiReadTertuliaMonthlyW.monthlyw.sc_skip);
    }

    public String getWeekDay(Context ctx) {
        return ctx.getResources().getStringArray(R.array.new_monthlyw_weekday)[weekday];
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

    protected TertuliaEditionMonthlyW(Parcel in) {
        super(in);
        schedule_id = in.readInt();
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
        out.writeInt(schedule_id);
        out.writeInt(weekday);
        out.writeInt(weeknr);
        out.writeByte((byte) (isFromStart ? 1 : 0));
        out.writeInt(skip);;
    }

    public static final Creator<TertuliaEditionMonthlyW> CREATOR = new Creator<TertuliaEditionMonthlyW>() {
        @Override
        public TertuliaEditionMonthlyW createFromParcel(Parcel in) {
            return new TertuliaEditionMonthlyW(in);
        }

        @Override
        public TertuliaEditionMonthlyW[] newArray(int size) {
            return new TertuliaEditionMonthlyW[size];
        }
    };

    // endregion
}
