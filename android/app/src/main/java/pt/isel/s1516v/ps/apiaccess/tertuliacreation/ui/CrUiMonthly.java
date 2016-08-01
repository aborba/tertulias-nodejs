package pt.isel.s1516v.ps.apiaccess.tertuliacreation.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.TertuliasApplication;
import pt.isel.s1516v.ps.apiaccess.support.TertuliasApi;

public class CrUiMonthly implements Parcelable, CrUiSchedule {

    public int dayNr, skip;
    public boolean isFromStart;

    private String[] days, months;

    public CrUiMonthly(int dayNr, boolean isFromStart, int skip) {
        this.dayNr = dayNr;
        this.isFromStart = isFromStart;
        this.skip = skip;
    }

    public TertuliasApi.SCHEDULES getScheduleType() {
        return TertuliasApi.SCHEDULES.WEEKLY;
    }

    public String getDays() {
        String suffix = null;
        switch (dayNr) {
            case 11:
            case 12:
            case 13:
                suffix = "th";
                break;
            default:
                switch (dayNr % 10) {
                    case 1:
                        suffix = "st";
                        break;
                    case 2:
                        suffix = "nd";
                        break;
                    case 3:
                        suffix = "rd";
                        break;
                    default:
                        suffix = "th";
                        break;
                }
        }
        return String.valueOf(dayNr) + suffix;
    }

    public String getMonths(Context ctx) {
        if (months == null)
            months = ctx.getResources().getStringArray(R.array.new_monthly_skip);
        return months[skip];
    }

    @Override
    public String toString() {
        Context ctx = TertuliasApplication.getApplication().getApplicationContext();
        Resources res = ctx.getResources();
        String[] parts = res.getStringArray(R.array.new_monthly_tostring);
        String result = parts[0];
        if (skip == 0)
            result += parts[1];
        else
            result += String.format(Locale.getDefault(), parts[2], getMonths(ctx));
        result += String.format(parts[3],
                getDays());
        if (!isFromStart)
            result += parts[4];
        result += ".";
        return result;
    }

    // region CrUiSchedule

    @Override
    public void updateViews(TextView scheduleView) {
        scheduleView.setText(toString());
    }

    @Override
    public Date nextEvent() {
        return null;
    }

    // endregion

    // region Parcelable

    protected CrUiMonthly(Parcel in) {
        dayNr = in.readInt();
        skip = in.readInt();
        isFromStart = in.readByte() != 0;
    }

    public static final Creator<CrUiMonthly> CREATOR = new Creator<CrUiMonthly>() {
        @Override
        public CrUiMonthly createFromParcel(Parcel in) {
            return new CrUiMonthly(in);
        }

        @Override
        public CrUiMonthly[] newArray(int size) {
            return new CrUiMonthly[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dayNr);
        dest.writeInt(skip);
        dest.writeByte((byte) (isFromStart ? 1 : 0));
    }

    // endregion
}
