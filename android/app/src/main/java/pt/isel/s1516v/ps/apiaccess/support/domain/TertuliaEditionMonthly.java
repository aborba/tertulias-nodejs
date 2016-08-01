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
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiTertuliaEditionBundleMonthly;

public class TertuliaEditionMonthly extends TertuliaEdition {
    public final int schedule_id;
    public final int dayNr;
    public final boolean isFromStart;
    public final int skip;

    public TertuliaEditionMonthly(ApiTertuliaEdition core, ApiLink[] links, int schedule_id, int dayNr, boolean isFromStart, int skip) {
        super(core, links);
        this.schedule_id = schedule_id;
        this.dayNr = dayNr;
        this.isFromStart = isFromStart;
        this.skip = skip;
    }

    public TertuliaEditionMonthly(ApiTertuliaEditionBundleMonthly apiReadTertuliaMonthly) {
        this(apiReadTertuliaMonthly.tertulia, apiReadTertuliaMonthly.links,
                apiReadTertuliaMonthly.monthly.sc_id, apiReadTertuliaMonthly.monthly.sc_daynr, apiReadTertuliaMonthly.monthly.sc_isfromstart, apiReadTertuliaMonthly.monthly.sc_skip);
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

    protected TertuliaEditionMonthly(Parcel in) {
        super(in);
        schedule_id = in.readInt();
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
        out.writeInt(schedule_id);
        out.writeInt(dayNr);
        out.writeByte((byte) (isFromStart ? 1 : 0));
        out.writeInt(skip);;
    }

    public static final Creator<TertuliaEditionMonthly> CREATOR = new Creator<TertuliaEditionMonthly>() {
        @Override
        public TertuliaEditionMonthly createFromParcel(Parcel in) {
            return new TertuliaEditionMonthly(in);
        }

        @Override
        public TertuliaEditionMonthly[] newArray(int size) {
            return new TertuliaEditionMonthly[size];
        }
    };

    // endregion
}
